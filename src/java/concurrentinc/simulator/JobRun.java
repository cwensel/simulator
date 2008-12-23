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
import concurrentinc.simulator.model.Cluster;
import concurrentinc.simulator.model.Job;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.Seconds;

import java.util.concurrent.ExecutionException;

/**
 *
 */
public class JobRun
  {
  private String[] fields = new String[]{"startTime", "endTime", "duration", "durationSeconds"};
  private Instant startTime;
  private Instant endTime;
  private JobParams params;

  public JobRun( JobParams params )
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
    SimController controller = new SimController();
    controller.setCurrentTime( new Instant() );

    Framework.setController( controller );

    Cluster cluster = new Cluster( clusterParams.maxMapProcesses, clusterParams.maxReduceProcesses );

    Job job = new Job( params.inputSizeMb, params.shuffleSizeMb, params.outputSizeMb, params.numMappers, params.numReducers );

    setStartTime( controller.getCurrentTime() );

    cluster.submitJob( job );

    controller.eventLoop();

    setEndTime( controller.getCurrentTime() );
    }

  @Override
  public String toString()
    {
    return "JobRun{" + "startTime=" + startTime + ", endTime=" + endTime + ", params=" + params + '}';
    }

  public String print()
    {
    return startTime + "\t" + endTime + "\t" + getDuration() + "\t" + getDurationSeconds().getSeconds() + "\t" + params.print();
    }

  public String printFields()
    {
    return fields[ 0 ] + "\t" + fields[ 1 ] + "\t" + fields[ 2 ] + "\t" + fields[ 3 ] + "\t" + params.printFields();
    }
  }
