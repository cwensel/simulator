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
import com.hellblazer.primeMover.runtime.Framework;
import concurrentinc.simulator.params.ClusterParams;
import concurrentinc.simulator.params.JobParams;
import concurrentinc.simulator.params.MRJobParams;
import concurrentinc.simulator.params.NetworkParams;
import concurrentinc.simulator.model.Network;
import concurrentinc.simulator.model.DistributedData;
import concurrentinc.simulator.controller.SimController;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.joda.time.PeriodType;

/**
 *
 */
public class NetworkTest extends SimulationTests
  {
  public static final int TERA = 1 * 1024 * 1024;
  public static final int MEGA = 1 * 1024;

  public void testSimple() throws Exception
    {
    SimController controller = new SimController();
    controller.setCurrentTime( new Instant() );
//
    Framework.setController( controller );

    NetworkParams networkParams = new NetworkParams( );
    Network network = new Network( networkParams );
    DistributedData distributedData = new DistributedData( 1024 );

    distributedData.read( network, 1, 1 );

    controller.eventLoop();

    Instant start = controller.getSimulationStart();
    Instant end = controller.getSimulationEnd();

    Period duration = new Period( start, end, PeriodType.standard() );

    System.out.println( "start = " + start );
    System.out.println( "end = " + end );
    System.out.println( "duration = " + duration );

    }

  }