/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator;

import java.io.PrintWriter;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import com.hellblazer.primeMover.SimulationException;
import concurrentinc.simulator.model.DistributedData;
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

    for( double inputSizeMb = start.getSourceSizeMB(); inputSizeMb <= end.getSourceSizeMB(); inputSizeMb += increment.getSourceSizeMB() )
      {
      for( float mapperDataFactor = start.getMapperParams().getDataFactor(); mapperDataFactor <= end.getMapperParams().getDataFactor(); mapperDataFactor += increment.getMapperParams().getDataFactor() )
        {
        for( float reducerDataFactor = start.getReducerParams().getDataFactor(); reducerDataFactor <= end.getReducerParams().getDataFactor(); reducerDataFactor += increment.getReducerParams().getDataFactor() )
          {
          for( int numMappers = start.getMapperParams().getNumTaskProcesses(); numMappers <= end.getMapperParams().getNumTaskProcesses(); numMappers += increment.getMapperParams().getNumTaskProcesses() )
            {
            for( int numReducers = start.getReducerParams().getNumTaskProcesses(); numReducers <= end.getReducerParams().getNumTaskProcesses(); numReducers += increment.getReducerParams().getNumTaskProcesses() )
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
    MRJobParams mrJobParams = new MRJobParams( numMappers, numReducers );
    mrJobParams.sources.add( new DistributedData( inputSizeMb ) );
    WorkloadParams workloadParams = new WorkloadParams( mrJobParams );

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
