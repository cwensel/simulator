/*
 * Copyright (c) 2007-2008 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Cascading is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cascading is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cascading.  If not, see <http://www.gnu.org/licenses/>.
 */

package concurrentinc.simulator.model;

import com.hellblazer.primeMover.Entity;
import concurrentinc.simulator.params.ClusterParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

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
    workload.start( this );
    }

  public void submitMRJob( MRJob job, DistributedData distributedData ) throws InterruptedException, ExecutionException
    {
    job.startJob( this, distributedData );
    }

  public void endJob( MRJob job )
    {

    }

  public void executeMaps( Collection<MapProcess> maps )
    {
    LOG.debug( "maps = {}", maps.size() );

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
    LOG.debug( "reduces = {}", reduces.size() );

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
