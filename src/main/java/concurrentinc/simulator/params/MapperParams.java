/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.params;

/**
 *
 */
public class MapperParams extends TaskParams
  {
  public MapperParams( int requestedNumProcesses )
    {
    this.requestedNumProcesses = requestedNumProcesses;
    }

  public MapperParams( int requestedNumProcesses, float dataFactor, float processingThroughput )
    {
    this.requestedNumProcesses = requestedNumProcesses;
    this.processingThroughput = processingThroughput;
    this.dataFactor = dataFactor;
    }
  }
