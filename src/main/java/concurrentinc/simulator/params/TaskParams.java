/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
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
  private int numTaskProcesses = 1; // requested num processes
  private float dataFactor = 1.0f; // does it create or destroy data
  private float processingThroughput = 100; // Mb /sec processing bandwidth

  public int getNumTaskProcesses()
    {
    return numTaskProcesses;
    }

  public void setNumTaskProcesses( int numTaskProcesses )
    {
    this.numTaskProcesses = numTaskProcesses;
    }

  public float getDataFactor()
    {
    return dataFactor;
    }

  public void setDataFactor( float dataFactor )
    {
    if( dataFactor == 0 )
      throw new IllegalArgumentException( "data factor may not be zero" );

    this.dataFactor = dataFactor;
    }

  public float getProcessingThroughput()
    {
    return processingThroughput;
    }

  public void setProcessingThroughput( float processingThroughput )
    {
    if( processingThroughput == 0 )
      throw new IllegalArgumentException( "processing throughput may not be zero" );

    this.processingThroughput = processingThroughput;
    }
  }
