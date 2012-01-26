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
  public MapperParams( int numTaskProcesses )
    {
    this.setNumTaskProcesses( numTaskProcesses );
    }

  public MapperParams( int numTaskProcesses, float dataFactor, float processingThroughput )
    {
    this.setNumTaskProcesses( numTaskProcesses );
    this.setProcessingThroughput( processingThroughput );
    this.setDataFactor( dataFactor );
    }
  }
