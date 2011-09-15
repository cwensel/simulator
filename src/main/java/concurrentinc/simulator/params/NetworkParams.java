/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.params;

import concurrentinc.simulator.model.Bandwidth;

/**
 *
 */
public class NetworkParams
  {
  public double bandwidthMbS = Bandwidth.Gigabit10;
  public double readEfficiency = 1.0d;
  public double writeEfficiency = 1.0d;

  public NetworkParams()
    {
    }

  public NetworkParams( double bandwidthMbS )
    {
    this.bandwidthMbS = bandwidthMbS;
    }

  public NetworkParams( double bandwidthMbS, double readEfficiency, double writeEfficiency )
    {
    this.bandwidthMbS = bandwidthMbS;
    this.readEfficiency = readEfficiency;
    this.writeEfficiency = writeEfficiency;
    }

  public double getEffectiveReadMbS()
    {
    return bandwidthMbS * readEfficiency;
    }

  public double getEffectiveWriteMbS()
    {
    return bandwidthMbS * writeEfficiency;
    }

  }
