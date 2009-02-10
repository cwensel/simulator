/*
 * Copyright (c) 2007-2008 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Cascading is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cascading is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cascading.  If not, see <http://www.gnu.org/licenses/>.
 */

package concurrentinc.simulator;

import com.hellblazer.primeMover.runtime.Framework;
import com.hellblazer.primeMover.runtime.SimulationException;
import concurrentinc.simulator.controller.SimController;
import concurrentinc.simulator.model.WorkloadImpl;
import concurrentinc.simulator.model.Workload;
import concurrentinc.simulator.model.*;
import concurrentinc.simulator.params.ClusterParams;
import concurrentinc.simulator.params.WorkloadParams;
import concurrentinc.simulator.util.PrintableImpl;
import concurrentinc.simulator.util.Printer;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.Seconds;

import java.util.concurrent.ExecutionException;

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
  private ClassLoader classLoader;
  private WorkloadParams params;

  public JobSimulationRunner( ClassLoader classLoader, WorkloadParams params )
    {
    this.classLoader = classLoader;
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
    SimController controller = new SimController();
    controller.setCurrentTime( new Instant() );

    Framework.setController( controller );

    Thread.currentThread().setContextClassLoader( classLoader );

    Cluster cluster = new ClusterImpl( clusterParams );
    Workload workload = new WorkloadImpl( params );

    cluster.submitWorkload( workload );

    controller.eventLoop();

    setStartTime( controller.getSimulationStart() );
    setEndTime( controller.getSimulationEnd() );
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
