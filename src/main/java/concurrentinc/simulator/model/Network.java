/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import com.hellblazer.primeMover.Blocking;

/**
 *
 */
public interface Network
  {
  @Blocking
  void read( double sizeMb );

  @Blocking
  void write( double sizeMB );
  }