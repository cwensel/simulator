/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.params;

import concurrentinc.simulator.util.PrintableImpl;

/**
 *
 */
public class ClusterParams extends PrintableImpl
  {
  public NetworkParams networkParams;
  public int maxMapProcesses;
  public int maxReduceProcesses;

  public ClusterParams( NetworkParams networkParams, int maxMapProcesses, int maxReduceProcesses )
    {
    this.networkParams = networkParams;
    this.maxMapProcesses = maxMapProcesses;
    this.maxReduceProcesses = maxReduceProcesses;
    }

  public ClusterParams( int maxMapProcesses, int maxReduceProcesses )
    {
    this.networkParams = new NetworkParams();
    this.maxMapProcesses = maxMapProcesses;
    this.maxReduceProcesses = maxReduceProcesses;
    }
  }
