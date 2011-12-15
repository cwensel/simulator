/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hellblazer.primeMover.Blocking;
import com.hellblazer.primeMover.Entity;
import com.hellblazer.primeMover.Kronos;
import com.hellblazer.primeMover.SynchronousQueue;
import concurrentinc.simulator.params.MRJobParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Entity({MRJob.class})
public class MRJobImpl implements MRJob
  {
  private static final Logger LOG = LoggerFactory.getLogger( MRJobImpl.class );

  private MRJobParams mrJobParams;

  private Set<MapProcess> mapProcesses;
  private Set<ReduceProcess> reduceProcesses;
  private Cluster cluster;
  private List<DistributedData> inputData;
  private SynchronousQueue<String> channel;
  private int runningMapProcesses = 0;
  private int runningReduceProcesses = 0;

  public MRJobImpl( MRJobParams mrJobParams )
    {
    this.mrJobParams = mrJobParams;
    }

  private double getBlockSizeMb()
    {
    return mrJobParams.blockSizeMb;
    }

  private int getFileReplication()
    {
    return mrJobParams.fileReplication;
    }

  private int getOutputSizeMb()
    {
    return (int) ( mrJobParams.reducer.dataFactor * getShuffleSizeMb() );
    }

  private int getShuffleSizeMb()
    {
    return (int) ( mrJobParams.mapper.dataFactor * getInputSizeMB() );
    }

  private List<DistributedData> getOutputData()
    {
    List<DistributedData> output = new ArrayList<DistributedData>();

    Set processes = reduceProcesses;

    if( reduceProcesses.isEmpty() )
      processes = mapProcesses;

    for( Object taskProcess : processes )
      output.add( ( (TaskProcess) taskProcess ).getOutputData() );

    return output;
    }

  private double getInputSizeMB()
    {
    return DistributedData.totalDataSize( inputData );
    }

  long getNumMappers()
    {
    double mod = getInputSizeMB() % getMinNumMappers();
    return Math.max( getMinNumMappers() + ( mod == 0 ? 0 : 1 ), getNumBlocks() );
    }

  long getNumBlocks()
    {
    return DistributedData.totalBlocks( inputData );
    }

  int computeSplitSizeMb()
    {
    double goalSize = getInputSizeMB() / getMinNumMappers();
    return (int) Math.max( 1d, Math.min( goalSize, (double) getBlockSizeMb() ) );
    }

  int getMinNumMappers()
    {
    return mrJobParams.mapper.requestedNumProcesses;
    }

  int getNumReducers()
    {
    return mrJobParams.reducer.requestedNumProcesses;
    }

  List<DistributedData> getInputData()
    {
    return inputData;
    }

  @Override
  public void startJob( Cluster cluster, List<DistributedData> inputData )
    {
    LOG.info( "starting job: {}", this );
    this.cluster = cluster;
    this.inputData = inputData;
    startMaps();
    }

  @Override
  public void blockOnPredecessors( Cluster cluster, List<MRJob> predecessors )
    {
    LOG.info( "blocking on predecessors: {}", this );
    this.cluster = cluster;

    for( MRJob predecessor : predecessors )
      {
      LOG.info( "blocking on predecessor: {}", predecessor );
      predecessor.blockTillComplete();
      }

    List<DistributedData> temp = new ArrayList<DistributedData>();

    for( MRJob predecessor : predecessors )
      temp.addAll( ( (MRJobImpl) predecessor ).getOutputData() );

    inputData = temp;
    startMaps();
    }

  @Blocking
  @Override
  public void blockTillComplete()
    {
    LOG.info( "blocking in job: {}", this );
    channel = Kronos.createChannel( String.class );
    String result = channel.take();
    LOG.info( "unblocking with: {}, in job: {}", result, this );
    }

  @Override
  public void endJob()
    {
    LOG.info( "ending job: {}", this );
    cluster.endJob( this );

    if( channel != null )
      channel.put( "done" );
    }

  private void startMaps()
    {
    cluster.executeMaps( getMapProcesses() );
    }

  private void startReduces()
    {
    cluster.executeReduces( getReduceProcesses() );
    }

  private Collection<MapProcess> getMapProcesses()
    {
    mapProcesses = new HashSet<MapProcess>();

    DistributedData data = new DistributedData( getInputSizeMB(), getBlockSizeMb(), getFileReplication() );

    double remainingSize = getInputSizeMB();
    int splitSize = computeSplitSizeMb();

    for( int i = 0; i < getNumMappers(); i++ )
      {
      long toProcess = (long) Math.min( splitSize, remainingSize );
      Mapper mapper = new MapperImpl( data, mrJobParams.mapper.processingThroughput, toProcess );
      mapProcesses.add( new MapProcessImpl( this, mapper ) );

      remainingSize -= toProcess;
      }

    return mapProcesses;
    }

  private Collection<ReduceProcess> getReduceProcesses()
    {
    reduceProcesses = new HashSet<ReduceProcess>();

    long toProcess = getShuffleSizeMb() / getNumReducers(); // assume even distribution
    long toWrite = getOutputSizeMb() / getNumReducers();

    DistributedData data = new DistributedData( toWrite, getBlockSizeMb(), getFileReplication() );

    for( int i = 0; i < getNumReducers(); i++ )
      {
      Shuffler shuffler = new ShufflerImpl( mrJobParams.reducer.sortBlockSizeMb, getNumMappers(), toProcess );
      Reducer reducer = new ReducerImpl( data, mrJobParams.reducer.processingThroughput, toProcess, toWrite );
      reduceProcesses.add( new ReduceProcessImpl( this, shuffler, reducer ) );
      }

    return reduceProcesses;
    }

  @Override
  public void releaseMapProcess( MapProcess mapProcess )
    {
    if( !mapProcesses.remove( mapProcess ) )
      throw new IllegalStateException( "map process not queued, current running: " + runningMapProcesses );

    runningMapProcesses--;

    cluster.releaseMapProcess();

    if( mapProcesses.isEmpty() )
      startReduces();
    }

  @Override
  public void releaseReduceProcess( ReduceProcess reduceProcess )
    {
    if( !reduceProcesses.remove( reduceProcess ) )
      throw new IllegalStateException( "reduce process not queued, current running: " + runningReduceProcesses );

    runningReduceProcesses--;

    cluster.releaseReduceProcess();

    if( reduceProcesses.isEmpty() )
      endJob();
    }

  //  @Blocking
  @Override
  public int runningMapProcess()
    {
    return runningMapProcesses++;
    }

  //  @Blocking
  @Override
  public int runningReduceProcess()
    {
    return runningReduceProcesses++;
    }
  }
