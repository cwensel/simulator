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

import com.hellblazer.primeMover.test.SimulationTests;
import concurrentinc.simulator.params.ClusterParams;
import concurrentinc.simulator.params.JobParams;
import concurrentinc.simulator.params.MRJobParams;

/**
 *
 */
public class SimpleTest extends SimulationTests
  {
  public static final int TERA = 1 * 1024 * 1024;
  public static final int MEGA = 1 * 1024;

  public void testSimpleJobRun() throws Exception
    {
    JobParams params = new JobParams( TERA, new MRJobParams( 100, 100 ) );

    JobSimulationRunner jobRun = new JobSimulationRunner( params );

    jobRun.run( new ClusterParams( 100, 100 ) );

    System.out.println( "start: " + jobRun.getStartTime() );

    System.out.println( "end: " + jobRun.getEndTime() );
    System.out.println( "duration: " + jobRun.getDuration() );

    assertEquals( "PT4M14.977S", jobRun.getDuration().toString() );
    }

  public void testChainedJobRun() throws Exception
    {
    JobParams params = new JobParams( TERA, new MRJobParams( 100, 100 ), new MRJobParams( 100, 100 ) );

    JobSimulationRunner jobRun = new JobSimulationRunner( params );

    jobRun.run( new ClusterParams( 100, 100 ) );

    System.out.println( "start: " + jobRun.getStartTime() );

    System.out.println( "end: " + jobRun.getEndTime() );
    System.out.println( "duration: " + jobRun.getDuration() );

    assertEquals( "PT8M29.954S", jobRun.getDuration().toString() );
    }
  }
