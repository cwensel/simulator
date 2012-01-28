/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import javax.measure.quantity.DataAmount;
import javax.measure.quantity.Quantity;
import javax.measure.unit.SI;

import com.hellblazer.primeMover.Entity;
import com.hellblazer.primeMover.Kronos;
import concurrentinc.simulator.params.NetworkParams;
import org.jscience.physics.amount.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Entity({Network.class})
public class NetworkImpl implements Network
  {
  private static final Logger LOG = LoggerFactory.getLogger( NetworkImpl.class );

  private NetworkParams networkParams;

  public NetworkImpl()
    {
    }

  public NetworkImpl( NetworkParams networkParams )
    {
    this.networkParams = networkParams;
    }

  public void read( Amount<DataAmount> size )
    {
    Amount<? extends Quantity> amount = size.to( SI.BIT ).divide( networkParams.getEffectiveReadRate().to( SI.BIT.divide( SI.SECOND ) ) );

    long readSleepMs = (long) ( amount.getEstimatedValue() * 1000 );

    LOG.debug( "readSleepMs = {}", readSleepMs );

    Kronos.blockingSleep( readSleepMs );
    }

  public void write( Amount<DataAmount> size )
    {
    Amount<? extends Quantity> amount = size.to( SI.BIT ).divide( networkParams.getEffectiveWriteRate().to( SI.BIT.divide( SI.SECOND ) ) );

    long writeSleepMs = (long) ( amount.getEstimatedValue() * 1000 );

    LOG.debug( "writeSleepMs = {}", writeSleepMs );

    Kronos.blockingSleep( writeSleepMs );
    }
  }
