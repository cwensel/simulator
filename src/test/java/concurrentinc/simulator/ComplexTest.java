/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator;

import java.util.List;

import concurrentinc.simulator.model.Bandwidth;
import concurrentinc.simulator.model.DistributedData;
import concurrentinc.simulator.params.ClusterParams;
import concurrentinc.simulator.params.MRJobParams;
import concurrentinc.simulator.params.MRJobParamsGraph;
import concurrentinc.simulator.params.WorkloadParams;
import concurrentinc.simulator.util.JsonPrinter;
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

    first.sources.add( new DistributedData( Bandwidth.TB ) );
    first.sources.add( new DistributedData( Bandwidth.TB ) );

    WorkloadParams workload = new WorkloadParams( graph );

    JobSimulationRunner jobRun = new JobSimulationRunner( workload );

    WorkloadParams resultWorkload = jobRun.run( new ClusterParams( 10, 10 ) );

    System.out.println( "start: " + jobRun.getStartTime() );
    System.out.println( "end: " + jobRun.getEndTime() );
    System.out.println( "duration: " + jobRun.getDuration() );

    List<MRJobParams> resultOrigins = resultWorkload.mrParams.getHeads();

    assertEquals( 1, resultOrigins.size() );

    MRJobParams firstResult = resultOrigins.get( 0 );
    long calculatedNumBlocks = DistributedData.totalBlocks( firstResult.sources );

    assertEquals( 2 * Bandwidth.TB / 128, calculatedNumBlocks );
    assertEquals( calculatedNumBlocks, firstResult.mapper.getNumTaskProcesses() );

    JsonPrinter printer = new JsonPrinter();

//    printer.print( new File( "input.json" ), workload );
//    printer.print( new File( "output.json" ), resultWorkload );

//    assertEquals( "PT7M1.568S", jobRun.getDuration().toString() );
    }
  }
