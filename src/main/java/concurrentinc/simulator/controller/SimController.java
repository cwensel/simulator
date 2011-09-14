/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.hellblazer.primeMover.controllers.SimulationController;

/**
 *
 */
public class SimController extends SimulationController
  {
  public Map<UUID, Object> entities = new HashMap<UUID, Object>();

//  @Override
//  public Object getEntity( UUID id )
//    {
//    return entities.get( id );
//    }
//
//  @Override
//  protected void register( UUID id, Object entity )
//    {
//    entities.put( id, entity );
//    }

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
