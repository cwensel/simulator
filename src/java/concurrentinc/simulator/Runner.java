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

import java.io.PrintWriter;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 *
 */
public class Runner
  {
  public static void run( PrintWriter writer, JobParams start, JobParams end, JobParams increment, float sample ) throws ExecutionException, InterruptedException, SimulationException
    {
    Random random = new Random();
    boolean first = true;

    ClusterParams clusterParams = new ClusterParams( 100, 100 );

    for( int inputSizeMb = start.inputSizeMb; inputSizeMb <= end.inputSizeMb; inputSizeMb += increment.inputSizeMb )
      {
      for( float mapperDataFactor = start.mrParams.mapper.dataFactor; mapperDataFactor <= end.mrParams.mapper.dataFactor; mapperDataFactor += increment.mrParams.mapper.dataFactor )
        {
        for( float reducerDataFactor = start.mrParams.reducer.dataFactor; reducerDataFactor <= end.mrParams.reducer.dataFactor; reducerDataFactor += increment.mrParams.reducer.dataFactor )
          {
          for( int numMappers = start.mrParams.mapper.numProcesses; numMappers <= end.mrParams.mapper.numProcesses; numMappers += increment.mrParams.mapper.numProcesses )
            {
            for( int numReducers = start.mrParams.reducer.numProcesses; numReducers <= end.mrParams.reducer.numProcesses; numReducers += increment.mrParams.reducer.numProcesses )
              {
              if( !first && sample != 1.0 && random.nextFloat() > sample )
                continue;

              MRJobParams mrParams = new MRJobParams( numMappers, numReducers );
              JobParams params = new JobParams( inputSizeMb, mrParams );

              JobRun run = new JobRun( params );

              run.run( clusterParams );

              if( first )
                {
                writer.println( run.printFields() );
                first = false;
                }

              writer.println( run.print() );
              }
            }
          }
        }
      }
    }

  }
