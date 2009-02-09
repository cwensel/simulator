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

package concurrentinc.simulator.controller;

import com.hellblazer.primeMover.controllers.SimulationController;
import com.hellblazer.primeMover.runtime.Event;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

import org.joda.time.Instant;

/**
 *
 */
public class SimController extends SimulationController
  {
  public Map<UUID, Object> entities = new HashMap<UUID, Object>();

  @Override
  public Object getEntity( UUID id )
    {
    return entities.get( id );
    }

  @Override
  protected void register( UUID id, Object entity )
    {
    entities.put( id, entity );
    }

//  @Override
//  public void post( Event event )
//    {
//    System.out.println( "event = " + event );
//    super.post( event );    //To change body of overridden methods use File | Settings | File Templates.
//    }
//
//  @Override
//  protected void sendEvent( String s, String s1, Class<?>[] classes, Object[] objects, Instant instant ) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException
//    {
//    System.out.println( "s = " + s );
//    super.sendEvent( s, s1, classes, objects, instant );
//    }
  }
