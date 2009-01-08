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
@Entity
public class Cluster
  {
  private static final Logger LOG = LoggerFactory.getLogger( Cluster.class );

  Network network;

  int maxMapProcesses = 100;
  int maxReduceProcesses = 100;
  int maxProcesses = Integer.MAX_VALUE;

  int currentProcesses;
  int currentMapProcesses;
  int currentReduceProcesses;

  Queue<MapProcess> mapQueue = new LinkedList<MapProcess>();
  Queue<ReduceProcess> reduceQueue = new LinkedList<ReduceProcess>();

  public Cluster()
    {
    }

  public Cluster( int maxMapProcesses, int maxReduceProcesses )
    {
    this.maxMapProcesses = maxMapProcesses;
    this.maxReduceProcesses = maxReduceProcesses;
    }

  public Cluster( ClusterParams clusterParams )
    {
    this.maxMapProcesses = clusterParams.maxMapProcesses;
    this.maxReduceProcesses = clusterParams.maxReduceProcesses;
    }

  public Cluster( Network network, ClusterParams clusterParams )
    {
    this.network = network;
    this.maxMapProcesses = clusterParams.maxMapProcesses;
    this.maxReduceProcesses = clusterParams.maxReduceProcesses;
    }

  public Cluster( Network network, int maxMapProcesses, int maxReduceProcesses )
    {
    this.network = network;
    this.maxMapProcesses = maxMapProcesses;
    this.maxReduceProcesses = maxReduceProcesses;
    }

  public void submitJob( MRJob job, DistributedData distributedData ) throws InterruptedException, ExecutionException
    {
    job.startJob( distributedData );
    }


  public void endJob( MRJob job )
    {

    }

  public void executeMaps( Collection<MapProcess> maps )
    {
    if( LOG.isDebugEnabled() )
      LOG.debug( "maps = " + maps.size() );

    queueMaps( maps );
    }

  private void queueMaps( Collection<MapProcess> maps )
    {
    mapQueue.addAll( maps );

    int numProcesses = Math.min( maxProcesses - currentProcesses, maxMapProcesses - currentMapProcesses );

    for( int i = 0; i < numProcesses; i++ )
      startMap();
    }

  public void releaseMap()
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

    try
      {
      mapQueue.remove().execute();
      }
    catch( InterruptedException e )
      {
      // ignore
      }
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
    if( LOG.isDebugEnabled() )
      LOG.debug( "reduces = " + reduces.size() );

    queueReduces( reduces );
    }

  public void releaseReduce()
    {
    currentProcesses--;
    currentReduceProcesses--;

    startReduce();
    }

  void startReduce()
    {
    if( reduceQueue.size() == 0 )
      return;

    if( currentProcesses >= maxProcesses )
      return;

    if( currentReduceProcesses >= maxReduceProcesses )
      return;

    currentProcesses++;
    currentReduceProcesses++;

    try
      {
      reduceQueue.remove().execute();
      }
    catch( InterruptedException e )
      {
      // ignore
      }
    }

  }
