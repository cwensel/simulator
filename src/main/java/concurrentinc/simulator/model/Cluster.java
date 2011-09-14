/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

/**
 *
 */
public interface Cluster
  {
  void submitMRJob( MRJob job, DistributedData distributedData ) throws InterruptedException, ExecutionException;

  void endJob( MRJob job );

  void executeMaps( Collection<MapProcess> maps );

  void releaseMapProcess();

  void executeReduces( Collection<ReduceProcess> reduces );

  void releaseReduceProcess();

  void startReduce();

  void submitWorkload( Workload workload );
  }