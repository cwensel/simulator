/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import java.util.Collection;

/**
 *
 */
public interface Cluster
  {
  void endJob( MRJob job );

  void executeMaps( Collection<MapProcess> maps );

  void releaseMapProcess();

  void executeReduces( Collection<ReduceProcess> reduces );

  void releaseReduceProcess();

  void startReduce();

  void submitWorkload( Workload workload );
  }