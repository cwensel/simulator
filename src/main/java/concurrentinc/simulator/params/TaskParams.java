/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.params;

import concurrentinc.simulator.util.PrintableImpl;

/**
 *
 */
public class TaskParams extends PrintableImpl
  {
  public int requestedNumProcesses = 1; // requested num processes
  public float dataFactor = 1.0f; // does it create or destroy data
  public float processingThroughput = 100; // Mb /sec processing bandwidth
  }
