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
import java.util.concurrent.ExecutionException;

/**
 *
 */
public class Runner
  {
  public static void run( PrintWriter writer ) throws ExecutionException, InterruptedException, SimulationException
    {
    boolean first = true;

    ClusterParams clusterParams = new ClusterParams( 100, 100 );

    for( int inputSizeMb = 1; inputSizeMb <= 1 * 1024 * 1024; inputSizeMb += 1024 * 500 )
      {
      for( int shuffleSizeMb = 1; shuffleSizeMb <= 1 * 1024 * 1024; shuffleSizeMb += 1024 * 500 )
        {
        for( int outputSizeMb = 1; outputSizeMb <= 1 * 1024 * 1024; outputSizeMb += 1024 * 500 )
          {
          for( int numMappers = 1; numMappers <= 1001; numMappers += 250 )
            {
            for( int numReducers = 1; numReducers <= 1001; numReducers += 250 )
              {
              JobParams params = new JobParams( inputSizeMb, shuffleSizeMb, outputSizeMb, numMappers, numReducers );

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
