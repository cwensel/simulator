/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator;

import java.io.PrintWriter;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import com.hellblazer.primeMover.SimulationException;
import concurrentinc.simulator.params.ClusterParams;
import concurrentinc.simulator.params.MRJobParams;
import concurrentinc.simulator.params.NetworkParams;
import concurrentinc.simulator.params.WorkloadParams;

/**
 *
 */
public class SimpleRunner
  {
  private NetworkParams networkParams = new NetworkParams( 10 * 1024 );
  private ClusterParams clusterParams = new ClusterParams( networkParams, 100, 100 );

  public SimpleRunner()
    {
    }

  public SimpleRunner( NetworkParams networkParams, ClusterParams clusterParams )
    {
    this.networkParams = networkParams;
    this.clusterParams = clusterParams;
    }

  public void run( PrintWriter writer, WorkloadParams start, WorkloadParams end, WorkloadParams increment, float sample ) throws ExecutionException, InterruptedException, SimulationException
    {
    Random random = new Random();
    boolean first = true;

    for( double inputSizeMb = start.getInputSizeMB(); inputSizeMb <= end.getInputSizeMB(); inputSizeMb += increment.getInputSizeMB() )
      {
      for( float mapperDataFactor = start.getMapperParams().dataFactor; mapperDataFactor <= end.getMapperParams().dataFactor; mapperDataFactor += increment.getMapperParams().dataFactor )
        {
        for( float reducerDataFactor = start.getReducerParams().dataFactor; reducerDataFactor <= end.getReducerParams().dataFactor; reducerDataFactor += increment.getReducerParams().dataFactor )
          {
          for( int numMappers = start.getMapperParams().requestedNumProcesses; numMappers <= end.getMapperParams().requestedNumProcesses; numMappers += increment.getMapperParams().requestedNumProcesses )
            {
            for( int numReducers = start.getReducerParams().requestedNumProcesses; numReducers <= end.getReducerParams().requestedNumProcesses; numReducers += increment.getReducerParams().requestedNumProcesses )
              {
              if( !first && sample != 1.0 && random.nextFloat() > sample )
                continue;

              first = run( writer, first, inputSizeMb, numMappers, numReducers );
              }
            }
          }
        }
      }
    }

  public void run( PrintWriter writer, double inputSizeMb, int numMappers, int numReducers ) throws ExecutionException, InterruptedException, SimulationException
    {
    run( writer, true, inputSizeMb, numMappers, numReducers );
    }

  private boolean run( PrintWriter writer, boolean printFields, double inputSizeMb, int numMappers, int numReducers ) throws ExecutionException, InterruptedException, SimulationException
    {
    WorkloadParams workloadParams = new WorkloadParams( inputSizeMb, new MRJobParams( numMappers, numReducers ) );

    JobSimulationRunner runner = new JobSimulationRunner( workloadParams );

    runner.run( clusterParams );

    if( printFields )
      {
      writer.println( runner.printFields() );
      printFields = false;
      }

    writer.println( runner.print() );

    return printFields;
    }

  }