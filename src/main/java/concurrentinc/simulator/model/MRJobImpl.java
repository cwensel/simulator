/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hellblazer.primeMover.Entity;
import com.hellblazer.primeMover.Kronos;
import com.hellblazer.primeMover.SynchronousQueue;
import concurrentinc.simulator.params.MRJobParams;

/**
 *
 */
@Entity({MRJob.class})
public class MRJobImpl implements MRJob
  {
  private Set<MapProcess> mapProcesses;
  private Set<ReduceProcess> reduceProcesses;
  private Cluster cluster;
  private DistributedData inputData;
  private MRJobParams mrJobParams;
  private SynchronousQueue<String> channel;
  private int runningMapProcesses = 0;
  private int runningReduceProcesses = 0;

  public MRJobImpl( MRJobParams mrJobParams )
    {
    this.mrJobParams = mrJobParams;
    }

  private double getBlockSizeMb()
    {
    return inputData.blockSizeMb;
    }

  private int getFileReplication()
    {
    return inputData.fileReplication;
    }

  private int getOutputSizeMb()
    {
    return (int) ( mrJobParams.reducer.dataFactor * getShuffleSizeMb() );
    }

  private int getShuffleSizeMb()
    {
    return (int) ( mrJobParams.mapper.dataFactor * getInputSizeMB() );
    }

  private double getInputSizeMB()
    {
    return inputData.sizeMb;
    }

  int getNumMappers()
    {
    double mod = getInputSizeMB() % getMinNumMappers();
    return Math.max( getMinNumMappers() + ( mod == 0 ? 0 : 1 ), getNumBlocks() );
    }

  int getNumBlocks()
    {
    return (int) Math.ceil( getInputSizeMB() / getBlockSizeMb() );
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

  DistributedData getInputData()
    {
    return inputData;
    }

  @Override
  public void startJob( Cluster cluster, DistributedData inputData )
    {
    this.cluster = cluster;
    this.inputData = inputData;
    startMaps();
    }

  @Override
  public void startJob( Cluster cluster, List<MRJob> predecessors )
    {
    this.cluster = cluster;

    for( MRJob predecessor : predecessors )
      predecessor.blockTillComplete();

    int outputSizeMb = 0;

    for( MRJob predecessor : predecessors )
      outputSizeMb += ( (MRJobImpl) predecessor ).getOutputSizeMb();

    inputData = new DistributedData( outputSizeMb );
    startMaps();
    }

  //  @Blocking
  @Override
  public void blockTillComplete()
    {
    channel = Kronos.createChannel( String.class );
    channel.take();
    }

  @Override
  public void endJob()
    {
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

    DistributedData data = new DistributedData( getFileReplication() );

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
