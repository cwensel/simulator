/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import java.util.Collection;
import java.util.List;

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
  public int numBlocks;
  public double blockSizeMb = 128;
  public int fileReplication = 3;

  public DistributedData( double sizeMb )
    {
    this.sizeMb = sizeMb;
    }

  public DistributedData( double sizeMb, int numBlocks, double blockSizeMb, int fileReplication )
    {
    this.sizeMb = sizeMb;
    this.numBlocks = numBlocks;
    this.blockSizeMb = blockSizeMb;
    this.fileReplication = fileReplication;
    }

  public int getNumBlocks()
    {
    if( numBlocks != 0 )
      return numBlocks;

    return (int) Math.ceil( sizeMb / blockSizeMb ); // round up, last block is small
    }

  public int getNumReplicatedBlocks()
    {
    return getNumBlocks() * fileReplication;
    }

  public void read( Network network, double amountMb, int runningJobMapProcesses )
    {
//    double readingBlocks = Math.ceil( amountMb / blockSizeMb );
//
//    if( readingBlocks > 1 )
//      throw new IllegalStateException( "cannot read more than one block for now" );

    int numReplicatedBlocks = getNumReplicatedBlocks();

    LOG.debug( "numReplicatedBlocks = {}", numReplicatedBlocks );

    if( runningJobMapProcesses < numReplicatedBlocks )
      return;

    LOG.debug( "reading bytes from network = {}", amountMb );

    network.read( amountMb );
    }

  public void write( Network network, double amountMb )
    {
    double writingAmount = amountMb * fileReplication;

    LOG.debug( "writing amount to network = {}", writingAmount );

    network.write( writingAmount );
    }

  public static double totalDataSizeMB( Collection<DistributedData> distributedData )
    {
    double total = 0;

    for( DistributedData data : distributedData )
      total += data.sizeMb;

    return total;
    }

  public static long totalBlocks( List<DistributedData> distributedData )
    {
    long total = 0;

    for( DistributedData data : distributedData )
      total += data.getNumBlocks();

    return total;
    }
  }
