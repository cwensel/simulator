/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.params;

/**
 *
 */
public class ReducerParams extends TaskParams
  {
  private long sortBlockSizeMb = 100;

  public ReducerParams( int requestedNumProcesses )
    {
    this.setNumTaskProcesses( requestedNumProcesses );
    }

  public ReducerParams( int requestedNumProcesses, float dataFactor, float processingThroughput )
    {
    this.setNumTaskProcesses( requestedNumProcesses );
    this.setProcessingThroughput( processingThroughput );
    this.setDataFactor( dataFactor );
    }

  public ReducerParams( int requestedNumProcesses, float processingThroughput, float dataFactor, long sortBlockSizeMb )
    {
    this.setNumTaskProcesses( requestedNumProcesses );
    this.setProcessingThroughput( processingThroughput );
    this.setDataFactor( dataFactor );
    this.setSortBlockSizeMb( sortBlockSizeMb );
    }

  public long getSortBlockSizeMb()
    {
    return sortBlockSizeMb;
    }

  public void setSortBlockSizeMb( long sortBlockSizeMb )
    {
    if( sortBlockSizeMb == 0 )
      throw new IllegalArgumentException( "sort block size may not be zero" );

    this.sortBlockSizeMb = sortBlockSizeMb;
    }
  }