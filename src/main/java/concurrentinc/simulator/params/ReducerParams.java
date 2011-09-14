/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.params;

/**
 *
 */
public class ReducerParams extends TaskParams
  {
  public long sortBlockSizeMb = 100;


  public ReducerParams( int requestedNumProcesses )
    {
    this.requestedNumProcesses = requestedNumProcesses;
    }

  public ReducerParams( int requestedNumProcesses, float dataFactor, float processingThroughput )
    {
    this.requestedNumProcesses = requestedNumProcesses;
    this.processingThroughput = processingThroughput;
    this.dataFactor = dataFactor;
    }

  public ReducerParams( int requestedNumProcesses, float processingThroughput, float dataFactor, long sortBlockSizeMb )
    {
    this.requestedNumProcesses = requestedNumProcesses;
    this.processingThroughput = processingThroughput;
    this.dataFactor = dataFactor;
    this.sortBlockSizeMb = sortBlockSizeMb;
    }
  }