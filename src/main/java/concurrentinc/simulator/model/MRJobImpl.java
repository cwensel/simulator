/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
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

  private Set<MapProcess> completedMapProcesses;
  private Set<ReduceProcess> completedReduceProcesses;

  private Cluster cluster;
  private List<DistributedData> inputData;
  private SynchronousQueue<String> channel;
  private int runningMapProcesses = 0;
  private int runningReduceProcesses = 0;

  public MRJobImpl( MRJobParams mrJobParams )
    {
    this.mrJobParams = mrJobParams;
    }

  public MRJobParams getResultMRJobParams()
    {
    int mappers = completedMapProcesses.size();
    int reducers = completedReduceProcesses.size();

    // return updated values
    return new MRJobParams( mappers, mrJobParams.mapper.getDataFactor(), mrJobParams.mapper.getProcessingThroughput(), reducers, mrJobParams.reducer.getDataFactor(), mrJobParams.reducer.getProcessingThroughput(), inputData, getOutputData() );
    }

  private double getBlockSizeMb()
    {
    return mrJobParams.blockSizeMb;
    }

  private int getFileReplication()
    {
    return mrJobParams.fileReplication;
    }

  private double getOutputSizeMb()
    {
    return mrJobParams.reducer.getDataFactor() * getShuffleSizeMb();
    }

  private double getShuffleSizeMb()
    {
    return mrJobParams.mapper.getDataFactor() * getInputSizeMB();
    }

  private List<DistributedData> getOutputData()
    {
    List<DistributedData> output = new ArrayList<DistributedData>();

    Set processes = completedReduceProcesses;

    if( processes.isEmpty() )
      processes = completedMapProcesses;

    if( processes.isEmpty() )
      throw new IllegalStateException( "no map or reduce processes completed" );

    for( Object taskProcess : processes )
      output.add( ( (TaskProcess) taskProcess ).getOutputData() );

    return output;
    }

  private double getInputSizeMB()
    {
    return DistributedData.totalDataSizeMB( inputData );
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
    return mrJobParams.mapper.getNumTaskProcesses();
    }

  int getNumReducers()
    {
    return mrJobParams.reducer.getNumTaskProcesses();
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
  public void startJobAfterPredecessors( Cluster cluster, List<MRJob> predecessors )
    {
    LOG.info( "blocking on num predecessors: {}, {}", predecessors.size(), this );
    this.cluster = cluster;

    for( MRJob predecessor : predecessors )
      {
      LOG.info( "blocking on predecessor: {}", predecessor );
      predecessor.blockTillComplete();
      }

    inputData = getPredecessorOutput( predecessors );

    if( inputData.size() == 0 )
      throw new IllegalStateException( "no input data received from predecessors" );

    LOG.info( "incoming data: {}", inputData.size() );

    LOG.info( "starting job: {}", this );
    startMaps();
    }

  private List<DistributedData> getPredecessorOutput( List<MRJob> predecessors )
    {
    List<DistributedData> temp = new ArrayList<DistributedData>();

    for( MRJob predecessor : predecessors )
      temp.addAll( ( (MRJobImpl) predecessor ).getOutputData() );

    return temp;
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
    LOG.info( "input data size(): {}", inputData.size() );

    mapProcesses = new HashSet<MapProcess>();
    completedMapProcesses = new HashSet<MapProcess>();

    int minMappers = getMinNumMappers();
    long numBlocks = getNumBlocks();

    long numMappers = Math.max( minMappers, numBlocks );
    double mappersPerBlock = numMappers / numBlocks;

    LOG.info( "num mappers: {}", numMappers );
    LOG.info( "mappers per block: {}", mappersPerBlock );

    for( DistributedData data : inputData )
      {
      double remainder = data.sizeMb;
      double minSplitSize = data.blockSizeMb / mappersPerBlock;

      LOG.debug( "max split size: {}", minSplitSize );

      while( remainder / minSplitSize > 1.1 )
        {
        double splitSize = Math.min( minSplitSize, remainder );

        mapProcesses.add( makeMapProcess( data, splitSize ) );

        remainder -= splitSize;
        }

      if( remainder > 0 )
        mapProcesses.add( makeMapProcess( data, remainder ) );

      }

    return mapProcesses;
    }

  private MapProcessImpl makeMapProcess( DistributedData data, double splitSize )
    {
    DistributedData output = new DistributedData( mrJobParams.mapper.getDataFactor() * splitSize, 0, getBlockSizeMb(), getFileReplication() );
    // we pass data instance in so we can block on it being read by multiple processes
    Mapper mapper = new MapperImpl( data, splitSize, mrJobParams.mapper.getProcessingThroughput(), output );
    return new MapProcessImpl( this, mapper );
    }

  private Collection<ReduceProcess> getReduceProcesses()
    {
    reduceProcesses = new HashSet<ReduceProcess>();
    completedReduceProcesses = new HashSet<ReduceProcess>();

    LOG.info( "num reducers: {}", getNumReducers() );
    LOG.info( "shuffle size: {}", getShuffleSizeMb() );
    LOG.info( "output size: {}", getOutputSizeMb() );

    double toProcess = getShuffleSizeMb() / getNumReducers(); // assume even distribution
    double toWrite = getOutputSizeMb() / getNumReducers();

    LOG.info( "sort each to process: {}", toProcess );
    LOG.info( "reducer each to write: {}", toWrite );

    DistributedData data = new DistributedData( toWrite, 0, getBlockSizeMb(), getFileReplication() );

    for( int i = 0; i < getNumReducers(); i++ )
      {
      Shuffler shuffler = new ShufflerImpl( mrJobParams.reducer.getSortBlockSizeMb(), getNumMappers(), toProcess );

      DistributedData output = new DistributedData( mrJobParams.reducer.getDataFactor() * toWrite, 0, getBlockSizeMb(), getFileReplication() );
      Reducer reducer = new ReducerImpl( data, mrJobParams.reducer.getProcessingThroughput(), toProcess, output );

      reduceProcesses.add( new ReduceProcessImpl( this, shuffler, reducer ) );
      }

    return reduceProcesses;
    }

  @Override
  public void releaseMapProcess( MapProcess mapProcess )
    {
    if( !mapProcesses.remove( mapProcess ) )
      throw new IllegalStateException( "map process not queued, current running: " + runningMapProcesses );

    completedMapProcesses.add( mapProcess );

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

    completedReduceProcesses.add( reduceProcess );

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
