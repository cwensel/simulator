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

import com.hellblazer.primeMover.Blocking;
import com.hellblazer.primeMover.Channel;
import com.hellblazer.primeMover.Entity;
import com.hellblazer.primeMover.Kronos;
import concurrentinc.simulator.params.MRJobParams;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
@Entity
public class MRJob
  {
  private Set<MapProcess> maps;
  private Set<ReduceProcess> reduces;
  private Cluster cluster;
  private DistributedData inputData;
  private MRJobParams mrJobParams;
  private Channel channel;

  public MRJob( Cluster cluster, MRJobParams mrJobParams )
    {
    this.cluster = cluster;
    this.mrJobParams = mrJobParams;
    }

  private int getBlockSizeMb()
    {
    return inputData.blockSizeMb;
    }

  private int getFileReplication()
    {
    return inputData.fileReplication;
    }

  private int getOutputSizeMb()
    {
    return (int) ( mrJobParams.reducer.dataFactor * getShuffleSizeMb() );
    }

  private int getShuffleSizeMb()
    {
    return (int) ( mrJobParams.mapper.dataFactor * getInputSizeMB() );
    }

  private int getInputSizeMB()
    {
    return inputData.sizeMb;
    }

  int getNumMappers()
    {
    return Math.max( getMinNumMappers(), (int) Math.ceil( getInputSizeMB() / getBlockSizeMb() ) );
    }

  int getMinNumMappers()
    {
    return mrJobParams.mapper.numProcesses;
    }

  int getNumReducers()
    {
    return mrJobParams.reducer.numProcesses;
    }

  DistributedData getInputData()
    {
    return inputData;
    }

  public void startJob( DistributedData inputData )
    {
    this.inputData = inputData;
    startMaps();
    }

  public void startJob( List<MRJob> predecessors )
    {
    for( MRJob predecessor : predecessors )
      predecessor.blockTillComplete();

    int outputSizeMb = 0;

    for( MRJob predecessor : predecessors )
      outputSizeMb += predecessor.getOutputSizeMb();

    inputData = new DistributedData( outputSizeMb );
    startMaps();
    }

  @Blocking
  public void blockTillComplete()
    {
    channel = Kronos.createChannel();
    channel.take();
    }

  public void endJob()
    {
    cluster.endJob( this );

    if( channel != null )
      channel.put( "done" );
    }

  private void startMaps()
    {
    cluster.executeMaps( getMapProcesses() );
    }

  private void startReduces()
    {
    cluster.executeReduces( getReduceProcesses() );
    }

  private Collection<MapProcess> getMapProcesses()
    {
    maps = new HashSet<MapProcess>();

    int size = getInputSizeMB();
    int subBlockSize = (int) Math.floor( getInputSizeMB() / getNumMappers() );

    DistributedData data = new DistributedData( getInputSizeMB(), getBlockSizeMb(), getFileReplication() );

    for( int i = 0; i < getNumMappers(); i++ )
      {
      long toProcess = Math.min( subBlockSize, size );
      Mapper mapper = new Mapper( data, mrJobParams.mapper.processingBandwidth, toProcess );
      maps.add( new MapProcess( this, mapper ) );

      size -= toProcess;
      }

    return maps;
    }

  private Collection<ReduceProcess> getReduceProcesses()
    {
    reduces = new HashSet<ReduceProcess>();

    long toProcess = getShuffleSizeMb() / getNumReducers(); // assume even distribution
    long toWrite = getOutputSizeMb() / getNumReducers();

    DistributedData data = new DistributedData( getFileReplication() );

    for( int i = 0; i < getNumReducers(); i++ )
      {
      Shuffler shuffler = new Shuffler( mrJobParams.reducer.sortBlockSizeMb, getNumMappers(), toProcess );
      Reducer reducer = new Reducer( data, mrJobParams.reducer.processingBandwidth, toProcess, toWrite );
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
