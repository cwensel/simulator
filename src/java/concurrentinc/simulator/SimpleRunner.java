/*
 * Copyright (c) 2007-2008 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Cascading is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cascading is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cascading.  If not, see <http://www.gnu.org/licenses/>.
 */

package concurrentinc.simulator;

import com.hellblazer.primeMover.runtime.SimulationException;
import concurrentinc.simulator.params.ClusterParams;
import concurrentinc.simulator.params.JobParams;
import concurrentinc.simulator.params.MRJobParams;

import java.io.PrintWriter;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 *
 */
public class SimpleRunner
  {
  public static void run( PrintWriter writer, JobParams start, JobParams end, JobParams increment, float sample ) throws ExecutionException, InterruptedException, SimulationException
    {
    Random random = new Random();
    boolean first = true;

    ClusterParams clusterParams = new ClusterParams( 100, 100 );

    for( int inputSizeMb = start.getInputSizeMB(); inputSizeMb <= end.getInputSizeMB(); inputSizeMb += increment.getInputSizeMB() )
      {
      for( float mapperDataFactor = start.getMapperParams().dataFactor; mapperDataFactor <= end.getMapperParams().dataFactor; mapperDataFactor += increment.getMapperParams().dataFactor )
        {
        for( float reducerDataFactor = start.getReducerParams().dataFactor; reducerDataFactor <= end.getReducerParams().dataFactor; reducerDataFactor += increment.getReducerParams().dataFactor )
          {
          for( int numMappers = start.getMapperParams().numProcesses; numMappers <= end.getMapperParams().numProcesses; numMappers += increment.getMapperParams().numProcesses )
            {
            for( int numReducers = start.getReducerParams().numProcesses; numReducers <= end.getReducerParams().numProcesses; numReducers += increment.getReducerParams().numProcesses )
              {
              if( !first && sample != 1.0 && random.nextFloat() > sample )
                continue;

              JobParams jobParams = new JobParams( inputSizeMb, new MRJobParams( numMappers, numReducers ) );

              JobSimulationRunner runner = new JobSimulationRunner( jobParams );

              runner.run( clusterParams );

              if( first )
                {
                writer.println( runner.printFields() );
                first = false;
                }

              writer.println( runner.print() );
              }
            }
          }
        }
      }
    }

  }
