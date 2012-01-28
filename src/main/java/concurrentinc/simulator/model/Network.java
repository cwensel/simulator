/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import javax.measure.quantity.DataAmount;

import com.hellblazer.primeMover.Blocking;
import org.jscience.physics.amount.Amount;

/**
 *
 */
public interface Network
  {
  @Blocking
  void read( Amount<DataAmount> size );

  @Blocking
  void write( Amount<DataAmount> size );
  }