/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.hellblazer.primeMover.controllers.RealTimeController;

/**
 *
 */
public class RTController extends RealTimeController
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
  }
