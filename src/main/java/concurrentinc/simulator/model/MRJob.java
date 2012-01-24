/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import java.util.List;

import com.hellblazer.primeMover.Blocking;
import com.hellblazer.primeMover.Continuable;
import com.hellblazer.primeMover.NonEvent;

/**
 *
 */
public interface MRJob
  {
  void startJob( Cluster cluster, List<DistributedData> inputData );

  @Continuable
  void startJobAfterPredecessors( Cluster cluster, List<MRJob> predecessors );

  @Blocking
  void blockTillComplete();

  void endJob();

  void releaseMapProcess( MapProcess mapProcess );

  void releaseReduceProcess( ReduceProcess reduceProcess );

  //  @Blocking
  @NonEvent
  int runningMapProcess();

  //  @Blocking
  @NonEvent
  int runningReduceProcess();
  }