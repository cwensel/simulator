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

package concurrentinc.simulator.model;

import com.hellblazer.primeMover.Entity;
import concurrentinc.simulator.JobParams;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@Entity
public class Job
  {
  long inputSizeMb; // the input to mappers
  long shuffleSizeMb; // the output of mappers into shuffle, same as input to reducers
  long outputSizeMb; // the output of reducers

  int numMappers;
  int numReducers;

  int fileReplication = 3;
  int blockSizeMb = 128;

  float networkBandwidth = 10 * 1024; // Mb / sec

  float mapProcessingFactor = 100; // Mb / sec
  float reduceProcessingFactor = 100; // Mb / sec

  long sortBlockSizeMb = 100;

  private Set<MapProcess> maps;
  private Set<ReduceProcess> reduces;
  private Cluster cluster;

  public Job( JobParams params )
    {
    this.inputSizeMb = params.inputSizeMb;
    this.shuffleSizeMb = (long) ( params.mrParams.mapper.dataFactor * this.inputSizeMb );
    this.outputSizeMb = (long) ( params.mrParams.reducer.dataFactor * this.shuffleSizeMb );
    this.numMappers = params.mrParams.mapper.numProcesses;
    this.numReducers = params.mrParams.reducer.numProcesses;
    this.networkBandwidth = params.networkBandwidth;
    this.blockSizeMb = params.blockSizeMb;
    this.fileReplication = params.fileReplication;
    }

  int getNumMappers()
    {
    return Math.max( getMinNumMappers(), (int) Math.ceil( inputSizeMb / blockSizeMb ) );
    }

  int getMinNumMappers()
    {
    return numMappers;
    }

  int getNumReducers()
    {
    return numReducers;
    }

  public void startJob( Cluster cluster )
    {
    this.cluster = cluster;
    startMaps();
    }

  public void endJob()
    {
    cluster.endJob( this );
    }

  public void startMaps()
    {
    cluster.executeMaps( getMapProcesses() );
    }

  public void startReduces()
    {
    cluster.executeReduces( getReduceProcesses() );
    }

  private Collection<MapProcess> getMapProcesses()
    {
    maps = new HashSet<MapProcess>();

    long size = inputSizeMb;
    long subBlockSize = (long) Math.floor( inputSizeMb / getNumMappers() );

    DistributedData data = new DistributedData( networkBandwidth, inputSizeMb, blockSizeMb, fileReplication );

    for( int i = 0; i < getNumMappers(); i++ )
      {
      long toProcess = Math.min( subBlockSize, size );
      Mapper mapper = new Mapper( data, mapProcessingFactor, toProcess );
      maps.add( new MapProcess( this, mapper ) );

      size -= toProcess;
      }

    return maps;
    }

  private Collection<ReduceProcess> getReduceProcesses()
    {
    reduces = new HashSet<ReduceProcess>();

    long toProcess = shuffleSizeMb / getNumReducers(); // assume even distribution
    long toWrite = outputSizeMb / getNumReducers();

    DistributedData data = new DistributedData( networkBandwidth, fileReplication );

    for( int i = 0; i < getNumReducers(); i++ )
      {
      Shuffler shuffler = new Shuffler( networkBandwidth, sortBlockSizeMb, getNumMappers(), toProcess );
      Reducer reducer = new Reducer( data, reduceProcessingFactor, toProcess, toWrite );
      reduces.add( new ReduceProcess( this, shuffler, reducer ) );
      }

    return reduces;
    }

  public void releaseMap( MapProcess mapProcess )
    {
    if( !maps.remove( mapProcess ) )
      throw new IllegalStateException( "map process not queued" );

    cluster.releaseMap();

    if( maps.isEmpty() )
      startReduces();
    }

  public void releaseReduce( ReduceProcess reduceProcess )
    {
    if( !reduces.remove( reduceProcess ) )
      throw new IllegalStateException( "reduce process not queued" );

    cluster.releaseReduce();

    if( reduces.isEmpty() )
      endJob();
    }
  }
