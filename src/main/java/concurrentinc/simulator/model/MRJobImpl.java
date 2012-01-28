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
import javax.measure.quantity.DataAmount;

import com.hellblazer.primeMover.Blocking;
import com.hellblazer.primeMover.Entity;
import com.hellblazer.primeMover.Kronos;
import com.hellblazer.primeMover.SynchronousQueue;
import concurrentinc.simulator.params.MRJobParams;
import org.jscience.physics.amount.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static concurrentinc.simulator.model.Bandwidth.MB;

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

  private Amount<DataAmount> getBlockSize()
    {
    return mrJobParams.blockSize;
    }

  private int getFileReplication()
    {
    return mrJobParams.fileReplication;
    }

  private Amount<DataAmount> getOutputSize()
    {
    return getShuffleSize().times( mrJobParams.reducer.getDataFactor() );
    }

  private Amount<DataAmount> getShuffleSize()
    {
    return getInputSize().times( mrJobParams.mapper.getDataFactor() );
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

  private Amount<DataAmount> getInputSize()
    {
    return DistributedData.totalDataSize( inputData );
    }

  long getNumMappers()
    {
    return getNumBlocks();
    }

  long getNumBlocks()
    {
    return DistributedData.totalBlocks( inputData );
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
    Collection<MapProcess> mapProcesses = getMapProcesses();
    LOG.info( "starting map processes: {}", mapProcesses.size() );

    cluster.executeMaps( mapProcesses );
    }

  private void startReduces()
    {
    Collection<ReduceProcess> reduceProcesses = getReduceProcesses();

    LOG.info( "starting reduce processes: {}", reduceProcesses.size() );

    cluster.executeReduces( reduceProcesses );
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
      Amount<DataAmount> remainder = data.size;
      Amount<DataAmount> maxSplitSize = data.blockSize.divide( mappersPerBlock );

      LOG.debug( "max split size: {}", maxSplitSize );

      while( remainder.to( MB ).divide( maxSplitSize.to( MB ) ).getEstimatedValue() > 1.1 )
        {
        Amount<DataAmount> splitSize = maxSplitSize.to( MB ).isLessThan( remainder.to( MB ) ) ? maxSplitSize : remainder;

        mapProcesses.add( makeMapProcess( data, splitSize ) );

        remainder = remainder.minus( splitSize );
        }

      if( remainder.isGreaterThan( Amount.valueOf( 0, MB ) ) )
        mapProcesses.add( makeMapProcess( data, remainder ) );

      }

    return mapProcesses;
    }

  private MapProcessImpl makeMapProcess( DistributedData data, Amount<DataAmount> splitSize )
    {
    Amount<DataAmount> outputSize = splitSize.times( mrJobParams.mapper.getDataFactor() );
    DistributedData output = new DistributedData( outputSize, 0, getBlockSize(), getFileReplication() );

    // we pass data instance in so we can block on it being read by multiple processes
    Mapper mapper = new MapperImpl( data, splitSize, mrJobParams.mapper.getProcessingThroughput(), output );

    return new MapProcessImpl( this, mapper );
    }

  private Collection<ReduceProcess> getReduceProcesses()
    {
    reduceProcesses = new HashSet<ReduceProcess>();
    completedReduceProcesses = new HashSet<ReduceProcess>();

    LOG.info( "num reducers: {}", getNumReducers() );
    LOG.info( "shuffle size: {}", getShuffleSize() );
    LOG.info( "output size: {}", getOutputSize() );

    Amount<DataAmount> toProcess = getShuffleSize().divide( getNumReducers() ); // assume even distribution
    Amount<DataAmount> toWrite = getOutputSize().divide( getNumReducers() );

    LOG.info( "sort each to process: {}", toProcess );
    LOG.info( "reducer each to write: {}", toWrite );

    DistributedData data = new DistributedData( toWrite, 0, getBlockSize(), getFileReplication() );

    for( int i = 0; i < getNumReducers(); i++ )
      {
      Shuffler shuffler = new ShufflerImpl( mrJobParams.reducer.getSortBlockSize(), getNumMappers(), toProcess );

      DistributedData output = new DistributedData( toWrite.times( mrJobParams.reducer.getDataFactor() ), 0, getBlockSize(), getFileReplication() );
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
