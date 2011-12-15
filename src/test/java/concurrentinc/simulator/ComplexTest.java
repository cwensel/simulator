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
import concurrentinc.simulator.params.MRJobParamsGraph;
import concurrentinc.simulator.params.WorkloadParams;
import junit.framework.TestCase;

/**
 *
 */
public class ComplexTest extends TestCase
  {
  public void testChainedJobRun() throws Exception
    {
    MRJobParams first = new MRJobParams( 100, 100 );
    MRJobParams second = new MRJobParams( 100, 100 );
    MRJobParams third = new MRJobParams( 100, 100 );

    MRJobParamsGraph graph = new MRJobParamsGraph();

    graph.addPath( first, second );
    graph.addPath( second, third );

    DistributedData source = new DistributedData( Bandwidth.TB );

    first.source.add( source );

    WorkloadParams workload = new WorkloadParams( graph );

    JobSimulationRunner jobRun = new JobSimulationRunner( workload );

    jobRun.run( new ClusterParams( 10, 10 ) );

    System.out.println( "start: " + jobRun.getStartTime() );
    System.out.println( "end: " + jobRun.getEndTime() );
    System.out.println( "duration: " + jobRun.getDuration() );

//    assertEquals( "PT7M1.568S", jobRun.getDuration().toString() );
    }
  }
