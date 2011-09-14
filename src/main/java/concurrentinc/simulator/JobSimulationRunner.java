/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator;

import java.util.concurrent.ExecutionException;

import com.hellblazer.primeMover.Kronos;
import com.hellblazer.primeMover.SimulationException;
import com.hellblazer.primeMover.controllers.SimulationController;
import concurrentinc.simulator.model.Cluster;
import concurrentinc.simulator.model.ClusterImpl;
import concurrentinc.simulator.model.Workload;
import concurrentinc.simulator.model.WorkloadImpl;
import concurrentinc.simulator.params.ClusterParams;
import concurrentinc.simulator.params.WorkloadParams;
import concurrentinc.simulator.util.PrintableImpl;
import concurrentinc.simulator.util.Printer;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.Seconds;

/**
 *
 */
public class JobSimulationRunner
  {
  public class RunResults extends PrintableImpl
    {
    public Instant startTime;
    public Instant endTime;
    public Period duration;
    public int durationSeconds;
    public WorkloadParams params;

    RunResults( Instant startTime, Instant endTime, Period duration, int durationSeconds, WorkloadParams params )
      {
      this.startTime = startTime;
      this.endTime = endTime;
      this.duration = duration;
      this.durationSeconds = durationSeconds;
      this.params = params;
      }
    }

  private Instant startTime;
  private Instant endTime;
  private WorkloadParams params;

  public JobSimulationRunner( WorkloadParams params )
    {
    this.params = params;
    }

  public Instant getStartTime()
    {
    return startTime;
    }

  public void setStartTime( Instant startTime )
    {
    this.startTime = startTime;
    }

  public Instant getEndTime()
    {
    return endTime;
    }

  public void setEndTime( Instant endTime )
    {
    this.endTime = endTime;
    }

  public Period getDuration()
    {
    return new Period( getStartTime(), getEndTime(), PeriodType.standard() );
    }

  public Seconds getDurationSeconds()
    {
    return Seconds.secondsBetween( getStartTime(), getEndTime() );
    }

  public void run( ClusterParams clusterParams ) throws ExecutionException, InterruptedException, SimulationException
    {
    SimulationController controller = new SimulationController();
    Instant startInstant = new Instant( 0 ); // start at time zero

    controller.setStartTime( startInstant.getMillis() );
    controller.setCurrentTime( startInstant.getMillis() );

    Kronos.setController( controller );

    Cluster cluster = new ClusterImpl( clusterParams );
    Workload workload = new WorkloadImpl( params );

    cluster.submitWorkload( workload );

    controller.eventLoop();

    setStartTime( new Instant( controller.getSimulationStart() ) );
    setEndTime( new Instant( controller.getSimulationEnd() ) );
    }

  public String print()
    {
    return new RunResults( startTime, endTime, getDuration(), getDurationSeconds().getSeconds(), params ).print();
    }

  public String printFields()
    {
    return Printer.printFields( RunResults.class );
    }
  }
