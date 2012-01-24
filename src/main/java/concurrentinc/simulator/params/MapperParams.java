/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
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
    this.setRequestedNumProcesses( requestedNumProcesses );
    }

  public MapperParams( int requestedNumProcesses, float dataFactor, float processingThroughput )
    {
    this.setRequestedNumProcesses( requestedNumProcesses );
    this.setProcessingThroughput( processingThroughput );
    this.setDataFactor( dataFactor );
    }
  }
