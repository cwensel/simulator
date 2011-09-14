/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import com.hellblazer.primeMover.Blocking;

/**
 *
 */
public interface Mapper
  {
  @Blocking
  void execute( Network network, int runningMapProcesses );
  }