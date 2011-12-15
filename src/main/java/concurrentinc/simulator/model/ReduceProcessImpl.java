/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import com.hellblazer.primeMover.Entity;

/**
 *
 */
@Entity({ReduceProcess.class})
public class ReduceProcessImpl implements ReduceProcess, TaskProcess
  {
  private MRJob job;
  private Shuffler shuffler;
  private Reducer reducer;

  public ReduceProcessImpl( MRJob job, Shuffler shuffler, Reducer reducer )
    {
    this.job = job;
    this.shuffler = shuffler;
    this.reducer = reducer;
    }

  static int count = 0;

  public DistributedData getOutputData()
    {
    return ( (ReducerImpl) reducer ).getOutputData();
    }

  public void execute( Network network )
    {
    job.runningReduceProcess();

    shuffler.execute( network );
    reducer.execute( network );

    job.releaseReduceProcess( this );
    }

  }
