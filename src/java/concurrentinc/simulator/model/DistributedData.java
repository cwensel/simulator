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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class DistributedData
  {
  private static final Logger LOG = LoggerFactory.getLogger( DistributedData.class );

  public int sizeMb;
  public int blockSizeMb = 128;
  public int fileReplication = 3;

  public DistributedData( int sizeMb )
    {
    this.sizeMb = sizeMb;
    }

  public DistributedData( int sizeMb, int blockSizeMb, int fileReplication )
    {
    this.sizeMb = sizeMb;
    this.blockSizeMb = blockSizeMb;
    this.fileReplication = fileReplication;
    }

  public int getNumBlocks()
    {
    return (int) Math.ceil( sizeMb / blockSizeMb ); // round up, last block is small
    }

  public int getNumReplicatedBlocks()
    {
    return getNumBlocks() * fileReplication;
    }

  public void read( Network network, long amountMb, int runningJobMapProcesses )
    {
    double readingBlocks = Math.ceil( amountMb / blockSizeMb );

    if( readingBlocks > 1 )
      throw new IllegalStateException( "cannot read more than one block for now" );

    if( runningJobMapProcesses < getNumReplicatedBlocks() )
      return;

    network.read( amountMb );
    }

  public void write( Network network, long amountMb )
    {
    network.write( amountMb * fileReplication );
    }
  }
