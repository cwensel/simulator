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

import com.hellblazer.primeMover.Kronos;

/**
 *
 */
public class DistributedData
  {
  float networkFactor;
  long sizeMb;
  long blockSizeMb;
  int fileReplication;

  long readCounter;

  public DistributedData( float networkFactor, int fileReplication )
    {
    this.networkFactor = networkFactor;
    this.fileReplication = fileReplication;
    }

  public DistributedData( float networkFactor, long sizeMb, long blockSizeMb, int fileReplication )
    {
    this.networkFactor = networkFactor;
    this.sizeMb = sizeMb;
    this.blockSizeMb = blockSizeMb;
    this.fileReplication = fileReplication;
    }

  private int getNumBlocks()
    {
    return (int) Math.ceil( sizeMb / blockSizeMb ); // round up, last block is small
    }

  private int getNumReplicatedBlocks()
    {
    return getNumBlocks() * fileReplication;
    }

  public void read( long amountMb )
    {
    if( readCounter++ < getNumReplicatedBlocks() )
      return;

    float readSleep = amountMb / networkFactor * 1000;

    System.out.println( "readSleep = " + readSleep );

    Kronos.sleep( (long) readSleep );
    }

  public void write( long amountMb )
    {
    float writeSleep = amountMb / networkFactor * fileReplication * 1000;

    System.out.println( "writeSleep = " + writeSleep );

    Kronos.sleep( (long) writeSleep );
    }
  }