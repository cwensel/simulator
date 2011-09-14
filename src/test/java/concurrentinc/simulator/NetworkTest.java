/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator;

import com.hellblazer.primeMover.Kronos;
import com.hellblazer.primeMover.controllers.SimulationController;
import concurrentinc.simulator.model.Bandwidth;
import concurrentinc.simulator.model.Network;
import concurrentinc.simulator.model.NetworkImpl;
import concurrentinc.simulator.params.NetworkParams;
import junit.framework.TestCase;
import org.joda.time.Period;
import org.joda.time.PeriodType;

/**
 *
 */
public class NetworkTest extends TestCase
  {
  public void testSimple() throws Exception
    {
    SimulationController controller = new SimulationController();

    Kronos.setController( controller );

    NetworkParams networkParams = new NetworkParams();
    Network network = new NetworkImpl( networkParams );

    network.read( Bandwidth.TB );

    controller.eventLoop();

    long start = controller.getSimulationStart();
    long end = controller.getSimulationEnd();

    Period duration = new Period( start, end, PeriodType.standard() );

    System.out.println( "start = " + start );
    System.out.println( "end = " + end );
    System.out.println( "duration = " + duration );

    assertEquals( "PT13M39.200S", duration.toString() );
    }
  }