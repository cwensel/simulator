/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import java.util.Collection;
import java.util.List;
import javax.measure.quantity.DataAmount;

import concurrentinc.simulator.util.PrintableImpl;
import org.jscience.physics.amount.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static concurrentinc.simulator.model.Bandwidth.MB;

/**
 *
 */
public class DistributedData extends PrintableImpl
  {
  private static final Logger LOG = LoggerFactory.getLogger( DistributedData.class );

  public Amount<DataAmount> size;
  public int numBlocks;
  public Amount<DataAmount> blockSize = Amount.valueOf( 128l, MB );
  public int fileReplication = 3;

  public DistributedData( Amount<DataAmount> size )
    {
    this.size = size;
    }

  public DistributedData( Amount<DataAmount> size, int numBlocks, Amount<DataAmount> blockSize, int fileReplication )
    {
    this.size = size;
    this.numBlocks = numBlocks;
    this.blockSize = blockSize;
    this.fileReplication = fileReplication;
    }

  public int getNumBlocks()
    {
    if( numBlocks != 0 )
      return numBlocks;

    return (int) Math.ceil( size.to( MB ).divide( blockSize.to( MB ) ).getEstimatedValue() );
    }

  public int getNumReplicatedBlocks()
    {
    return getNumBlocks() * fileReplication;
    }

  public void read( Network network, Amount<DataAmount> amountMb, int runningJobMapProcesses )
    {
    int numReplicatedBlocks = getNumReplicatedBlocks();

    LOG.debug( "numReplicatedBlocks = {}", numReplicatedBlocks );

    if( runningJobMapProcesses < numReplicatedBlocks )
      return;

    LOG.debug( "reading bytes from network = {}", amountMb );

    network.read( amountMb );
    }

  public void write( Network network, Amount<DataAmount> amount )
    {
    Amount<DataAmount> writingAmount = amount.times( fileReplication );

    LOG.debug( "writing amount to network = {}", writingAmount );

    network.write( writingAmount );
    }

  public static Amount<DataAmount> totalDataSize( Collection<DistributedData> distributedData )
    {
    Amount<DataAmount> total = Amount.valueOf( 0, MB );

    for( DistributedData data : distributedData )
      total = total.plus( data.size );

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
