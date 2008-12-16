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

package concurrentinc.simulator;

import com.hellblazer.primeMover.Entity;

import java.util.*;
import java.util.concurrent.*;

/**
 *
 */
@Entity
public class  Cluster
  {
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

  public void submitJob( Job job ) throws InterruptedException, ExecutionException
    {
    job.startJob( this );
    }


  public void endJob( Job job )
    {

    }

  public  void executeMaps( Collection<MapProcess> maps )
    {
    System.out.println( "maps = " + maps.size() );

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

  public void executeReduces( Collection<ReduceProcess> reduces )
    {
    System.out.println( "reduces = " + reduces.size() );

    queueReduces( reduces );
    }

  }
