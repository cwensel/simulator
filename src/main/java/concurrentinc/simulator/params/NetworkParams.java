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

  public NetworkParams()
    {
    }

  public NetworkParams( double bandwidthMbS )
    {
    this.bandwidthMbS = bandwidthMbS;
    }
  }
