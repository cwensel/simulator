/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import concurrentinc.simulator.util.PrintableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class DistributedData extends PrintableImpl
  {
  private static final Logger LOG = LoggerFactory.getLogger( DistributedData.class );

  public double sizeMb;
  public int blockSizeMb = 128;
  public int fileReplication = 3;

  public DistributedData( double sizeMb )
    {
    this.sizeMb = sizeMb;
    }

  public DistributedData( double sizeMb, int blockSizeMb, int fileReplication )
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

    int numReplicatedBlocks = getNumReplicatedBlocks();

    LOG.debug( "numReplicatedBlocks = {}", numReplicatedBlocks );

    if( runningJobMapProcesses < numReplicatedBlocks )
      return;

    LOG.debug( "reading bytes from network = {}", amountMb );

    network.read( amountMb );
    }

  public void write( Network network, long amountMb )
    {
    long writingAmount = amountMb * fileReplication;

    LOG.debug( "writing amount to network = {}", writingAmount );

    network.write( writingAmount );
    }
  }
