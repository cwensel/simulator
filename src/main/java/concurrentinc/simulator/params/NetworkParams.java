/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.params;

import javax.measure.quantity.DataRate;
import javax.measure.unit.Unit;

import concurrentinc.simulator.model.Bandwidth;
import org.jscience.physics.amount.Amount;

/**
 *
 */
public class NetworkParams
  {
  public Unit<DataRate> bandwidth = Bandwidth.Gigabit10;
  public double readEfficiency = 1.0d;
  public double writeEfficiency = 1.0d;

  public NetworkParams()
    {
    }

  public NetworkParams( Unit<DataRate> bandwidth )
    {
    this.bandwidth = bandwidth;
    }

  public NetworkParams( Unit<DataRate> bandwidth, double readEfficiency, double writeEfficiency )
    {
    this.bandwidth = bandwidth;
    this.readEfficiency = readEfficiency;
    this.writeEfficiency = writeEfficiency;
    }

  public Amount<DataRate> getEffectiveReadRate()
    {
    return Amount.valueOf( 1, bandwidth ).times( readEfficiency );
    }

  public Amount<DataRate> getEffectiveWriteRate()
    {
    return Amount.valueOf( 1, bandwidth ).times( writeEfficiency );
    }
  }
