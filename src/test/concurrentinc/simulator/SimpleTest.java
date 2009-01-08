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
import com.hellblazer.primeMover.test.SimulationTests;
import concurrentinc.simulator.controller.SimController;
import concurrentinc.simulator.model.Cluster;
import concurrentinc.simulator.model.MRJob;
import concurrentinc.simulator.model.Network;
import concurrentinc.simulator.params.ClusterParams;
import concurrentinc.simulator.params.JobParams;
import concurrentinc.simulator.params.MRJobParams;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.joda.time.PeriodType;

/**
 *
 */
public class SimpleTest extends SimulationTests
  {
  public static final int TERA = 1 * 1024 * 1024;
  public static final int MEGA = 1 * 1024;

  public void testSimple() throws Exception
    {
    SimController controller = new SimController();
    Framework.setController( controller );
    controller.setCurrentTime( new Instant() );

    JobParams params = new JobParams( TERA, new MRJobParams( 100, 100 ) );

    MRJob mrJob = new MRJob( params.distributedData, params.getMRParams() );

    Network network = new Network();
    Cluster cluster = new Cluster( network, 100, 100 );

    Instant startTime = controller.getCurrentTime();
    System.out.println( "start: " + startTime );

    cluster.submitJob( mrJob );

    controller.eventLoop();

    Instant endTime = controller.getCurrentTime();
    System.out.println( "end: " + endTime );
    Period period = new Period( startTime, endTime, PeriodType.standard() );
    System.out.println( "duration: " + period );

    assertEquals( "PT4M15.071S", period.toString() );
    }

  public void testSimpleJobRun() throws Exception
    {
    JobParams params = new JobParams( TERA, new MRJobParams( 100, 100 ) );

    JobRun jobRun = new JobRun( params );

    jobRun.run( new ClusterParams( 100, 100 ) );

    System.out.println( "start: " + jobRun.getStartTime() );

    System.out.println( "end: " + jobRun.getEndTime() );
    System.out.println( "duration: " + jobRun.getDuration() );

    assertEquals( "PT4M15.071S", jobRun.getDuration().toString() );
    }

  public void testChainedJobRun() throws Exception
    {
    JobParams params = new JobParams( TERA, new MRJobParams( 100, 100 ), new MRJobParams( 100, 100 ) );

    JobRun jobRun = new JobRun( params );

    jobRun.run( new ClusterParams( 100, 100 ) );

    System.out.println( "start: " + jobRun.getStartTime() );

    System.out.println( "end: " + jobRun.getEndTime() );
    System.out.println( "duration: " + jobRun.getDuration() );

    assertEquals( "PT4M15.071S", jobRun.getDuration().toString() );
    }
  }
