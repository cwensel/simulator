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
import com.hellblazer.primeMover.runtime.Kalachakra;
import com.hellblazer.primeMover.Kronos;
import org.joda.time.Instant;
import org.joda.time.Seconds;
import concurrentinc.simulator.controller.RTController;
import concurrentinc.simulator.controller.SimController;

import java.util.concurrent.ExecutionException;

/**
 *
 */
public class SimpleTest extends SimulationTests
  {
  public static final int TERA = 1 * 1024 * 1024;

  public void testSimple() throws ExecutionException, InterruptedException
    {
    Kalachakra controller = new RTController();
    Framework.setController( controller );
    Thread.currentThread().setContextClassLoader( getClassLoader() );
    controller.setCurrentTime( new Instant() );

    long time = System.currentTimeMillis();

    Job job = new Job( TERA, TERA, TERA, 10, 10 );
    Cluster cluster = new Cluster();

    cluster.submit( job );

    System.out.println( ( System.currentTimeMillis() - time ) / 1000 );
    }
  }
