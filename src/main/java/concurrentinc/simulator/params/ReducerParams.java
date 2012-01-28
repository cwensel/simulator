/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.params;

import javax.measure.quantity.DataAmount;

import org.jscience.physics.amount.Amount;

import static concurrentinc.simulator.model.Bandwidth.MB;

/**
 *
 */
public class ReducerParams extends TaskParams
  {
  private Amount<DataAmount> sortBlockSize = Amount.valueOf( 100, MB );

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

  public ReducerParams( int requestedNumProcesses, float processingThroughput, float dataFactor, Amount<DataAmount> sortBlockSize )
    {
    this.setNumTaskProcesses( requestedNumProcesses );
    this.setProcessingThroughput( processingThroughput );
    this.setDataFactor( dataFactor );
    this.setSortBlockSize( sortBlockSize );
    }

  public Amount<DataAmount> getSortBlockSize()
    {
    return sortBlockSize;
    }

  public void setSortBlockSize( Amount<DataAmount> sortBlockSize )
    {
    if( sortBlockSize.getExactValue() == 0 )
      throw new IllegalArgumentException( "sort block size may not be zero" );

    this.sortBlockSize = sortBlockSize;
    }
  }