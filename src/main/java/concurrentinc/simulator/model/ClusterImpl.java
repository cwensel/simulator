/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import com.hellblazer.primeMover.Entity;
import concurrentinc.simulator.params.ClusterParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Entity({Cluster.class})
public class ClusterImpl implements Cluster
  {
  private static final Logger LOG = LoggerFactory.getLogger( ClusterImpl.class );

  private Network network;

  private int maxMapProcesses = 100;
  private int maxReduceProcesses = 100;
  private int maxProcesses = Integer.MAX_VALUE;

  private int currentProcesses;
  private int currentMapProcesses;
  private int currentReduceProcesses;

  private Queue<MapProcess> mapQueue = new LinkedList<MapProcess>();
  private Queue<ReduceProcess> reduceQueue = new LinkedList<ReduceProcess>();

  public ClusterImpl( ClusterParams clusterParams )
    {
    this.network = new NetworkImpl( clusterParams.networkParams );
    this.maxMapProcesses = clusterParams.maxMapProcesses;
    this.maxReduceProcesses = clusterParams.maxReduceProcesses;
    }

  public ClusterImpl( Network network, ClusterParams clusterParams )
    {
    this.network = network;
    this.maxMapProcesses = clusterParams.maxMapProcesses;
    this.maxReduceProcesses = clusterParams.maxReduceProcesses;
    }

  public ClusterImpl( NetworkImpl network, int maxMapProcesses, int maxReduceProcesses )
    {
    this.network = network;
    this.maxMapProcesses = maxMapProcesses;
    this.maxReduceProcesses = maxReduceProcesses;
    }

  public void submitWorkload( Workload workload )
    {
    LOG.info( "received workload: {}", workload );
    workload.start( this );
    }

  public void endJob( MRJob job )
    {

    }

  public void executeMaps( Collection<MapProcess> maps )
    {
    LOG.info( "executing maps = {}", maps.size() );

    queueMaps( maps );
    }

  private void queueMaps( Collection<MapProcess> maps )
    {
    mapQueue.addAll( maps );

    int numProcesses = Math.min( maxProcesses - currentProcesses, maxMapProcesses - currentMapProcesses );

    for( int i = 0; i < numProcesses; i++ )
      startMap();
    }

  public void releaseMapProcess()
    {
    currentProcesses--;
    currentMapProcesses--;

    startMap();
    }

  void startMap()
    {
    if( mapQueue.size() == 0 )
      return;

    if( currentProcesses >= maxProcesses )
      return;

    if( currentMapProcesses >= maxMapProcesses )
      return;

    currentProcesses++;
    currentMapProcesses++;

    mapQueue.remove().execute( network );
    }

  private void queueReduces( Collection<ReduceProcess> reduces )
    {
    reduceQueue.addAll( reduces );

    int numProcesses = Math.min( maxProcesses - currentProcesses, maxReduceProcesses - currentReduceProcesses );

    for( int i = 0; i < numProcesses; i++ )
      startReduce();
    }

  public void executeReduces( Collection<ReduceProcess> reduces )
    {
    LOG.info( "executing reduces = {}", reduces.size() );

    queueReduces( reduces );
    }

  public void releaseReduceProcess()
    {
    currentProcesses--;
    currentReduceProcesses--;

    startReduce();
    }

  public void startReduce()
    {
    if( reduceQueue.size() == 0 )
      return;

    if( currentProcesses >= maxProcesses )
      return;

    if( currentReduceProcesses >= maxReduceProcesses )
      return;

    currentProcesses++;
    currentReduceProcesses++;

    reduceQueue.remove().execute( network );
    }

  }
