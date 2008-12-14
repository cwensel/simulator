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

import com.hellblazer.primeMover.Entity;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.Callable;

/**
 *
 */
public class Job
  {
  long inputSizeMb; // the input to mappers
  long shuffleSizeMb; // the output of mappers into shuffle, same as input to reducers
  long outputSizeMb; // the output of reducers

  int numMappers;
  int numReducers;

  int fileReplication = 3;
  int blockSizeMb = 128;

  float mapProcessingFactor = 0; // Mb / sec
  float reduceProcessingFactor = 0; // Mb / sec

  float networkFactor = 10 * 1000; // Mb / sec
  long sortBlockSizeMb = 100;

  public Job( long inputSizeMb, long shuffleSizeMb, long outputSizeMb, int numMappers, int numReducers )
    {
    this.inputSizeMb = inputSizeMb;
    this.shuffleSizeMb = shuffleSizeMb;
    this.outputSizeMb = outputSizeMb;
    this.numMappers = numMappers;
    this.numReducers = numReducers;
    }

  public int getNumMappers()
    {
    return Math.max( getMinNumMappers(), (int) Math.ceil( inputSizeMb / blockSizeMb ) );
    }

  public int getMinNumMappers()
    {
    return numMappers;
    }

  public int getNumReducers()
    {
    return numReducers;
    }

  public Collection<MapProcess> getMapProcesses()
    {
    Set<MapProcess> maps = new HashSet<MapProcess>();

    long size = inputSizeMb;
    long subBlockSize = (long) Math.floor( inputSizeMb / getNumMappers() );

    for( int i = 0; i < getNumMappers(); i++ )
      {
      long toProcess = Math.min( subBlockSize, size );

      maps.add( new MapProcess( new Mapper( mapProcessingFactor, toProcess ) ) );

      size -= toProcess;
      }

    return maps;
    }

  public Collection<ReduceProcess> getReduceProcesses()
    {
    Set<ReduceProcess> reduces = new HashSet<ReduceProcess>();

    long toProcess = shuffleSizeMb / getNumReducers(); // assume even distribution

    for( int i = 0; i < getNumReducers(); i++ )
      {
      Shuffler shuffler = new Shuffler( networkFactor, sortBlockSizeMb, getNumMappers(), toProcess );
      Reducer reducer = new Reducer( reduceProcessingFactor, toProcess );
      reduces.add( new ReduceProcess( shuffler, reducer ) );
      }

    return reduces;
    }
  }
