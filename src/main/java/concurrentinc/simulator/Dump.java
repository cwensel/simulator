/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator;


import java.io.FileWriter;
import java.io.PrintWriter;

import concurrentinc.simulator.model.DistributedData;
import concurrentinc.simulator.params.MRJobParams;
import concurrentinc.simulator.params.WorkloadParams;

/**
 *
 */
public class Dump
  {
  public static void main( String[] args ) throws Exception
    {
    PrintWriter printWriter = new PrintWriter( System.out );

    if( args.length == 1 )
      printWriter = new PrintWriter( new FileWriter( args[ 0 ] ) );

    MRJobParams mrParamsStart = new MRJobParams( 1, 1 );
    MRJobParams mrParamsEnd = new MRJobParams( 1001, 1001 );
    MRJobParams mrParamsIncrement = new MRJobParams( 5, 5 );

    mrParamsStart.source.add( new DistributedData( 1 * 1024 * 1024 ) );
    mrParamsEnd.source.add( new DistributedData( 1 * 1024 * 1024 ) );
    mrParamsIncrement.source.add( new DistributedData( 1 * 1024 ) );

    WorkloadParams start = new WorkloadParams( mrParamsStart );
    WorkloadParams end = new WorkloadParams( mrParamsEnd );
    WorkloadParams increment = new WorkloadParams( mrParamsIncrement );

    float sample = 0.01f;

    new SimpleRunner().run( printWriter, start, end, increment, sample );

    printWriter.flush();
    }
  }
