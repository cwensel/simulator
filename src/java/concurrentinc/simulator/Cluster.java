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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 *
 */
@Entity
public class Cluster
  {
  int maxMapProcesses = 100;
  int maxReduceProcesses= 100;
  int maxProcesses = 100;

  public void submit( Job job ) throws InterruptedException, ExecutionException
    {
    ExecutorService mapExecutor = Executors.newFixedThreadPool( Math.min(maxProcesses, maxMapProcesses ));
    Collection<MapProcess> maps = job.getMapProcesses();

    List<Future<Boolean>> mapFutures = mapExecutor.invokeAll( maps );

    for( Future<Boolean> mapFuture : mapFutures )
      mapFuture.get();

    ExecutorService reduceExecutor = Executors.newFixedThreadPool(  Math.min(maxProcesses, maxReduceProcesses ) );
    Collection<ReduceProcess> reduces = job.getReduceProcesses();

    List<Future<Boolean>> reduceFutures = reduceExecutor.invokeAll( reduces );

    for( Future<Boolean> reduceFuture : reduceFutures )
      reduceFuture.get();
    }
  }
