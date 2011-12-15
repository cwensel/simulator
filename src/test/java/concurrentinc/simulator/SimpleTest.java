/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator;

import concurrentinc.simulator.model.Bandwidth;
import concurrentinc.simulator.model.DistributedData;
import concurrentinc.simulator.params.ClusterParams;
import concurrentinc.simulator.params.MRJobParams;
import concurrentinc.simulator.params.WorkloadParams;
import junit.framework.TestCase;

/**
 *
 */
public class SimpleTest extends TestCase
  {

  public void testShortJobRun() throws Exception
    {
    MRJobParams mrJobParams = new MRJobParams( 1, 1 );
    mrJobParams.source.add( new DistributedData( Bandwidth.TB ) );

    WorkloadParams params = new WorkloadParams( mrJobParams );

    JobSimulationRunner jobRun = new JobSimulationRunner( params );

    jobRun.run( new ClusterParams( 1, 1 ) );

    System.out.println( "start: " + jobRun.getStartTime() );

    System.out.println( "end: " + jobRun.getEndTime() );
    System.out.println( "duration: " + jobRun.getDuration() );

    assertEquals( "PT8H13M14.214S", jobRun.getDuration().toString() );
    }

  public void testSimpleJobRun() throws Exception
    {
    MRJobParams mrJobParams = new MRJobParams( 100, 100 );
    mrJobParams.source.add( new DistributedData( Bandwidth.TB ) );

    WorkloadParams params = new WorkloadParams( mrJobParams );

    JobSimulationRunner jobRun = new JobSimulationRunner( params );

    jobRun.run( new ClusterParams( 100, 100 ) );

    System.out.println( "start: " + jobRun.getStartTime() );

    System.out.println( "end: " + jobRun.getEndTime() );
    System.out.println( "duration: " + jobRun.getDuration() );

    assertEquals( "PT4M35.551S", jobRun.getDuration().toString() );
    }

  public void testChainedJobRun() throws Exception
    {
    MRJobParams mrJobParams = new MRJobParams( 100, 100 );
    mrJobParams.source.add( new DistributedData( Bandwidth.TB ) );

    WorkloadParams params = new WorkloadParams( mrJobParams, new MRJobParams( 100, 100 ) );

    JobSimulationRunner jobRun = new JobSimulationRunner( params );

    jobRun.run( new ClusterParams( 100, 100 ) );

    System.out.println( "start: " + jobRun.getStartTime() );

    System.out.println( "end: " + jobRun.getEndTime() );
    System.out.println( "duration: " + jobRun.getDuration() );

    assertEquals( "PT4M35.551S", jobRun.getDuration().toString() );
    }
  }
