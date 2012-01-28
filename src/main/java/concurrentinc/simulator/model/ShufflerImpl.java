/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import javax.measure.quantity.DataAmount;

import com.hellblazer.primeMover.Blocking;
import com.hellblazer.primeMover.Entity;
import com.hellblazer.primeMover.Kronos;
import org.jscience.physics.amount.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Entity({Shuffler.class})
public class ShufflerImpl implements Shuffler
  {
  private static final Logger LOG = LoggerFactory.getLogger( ShufflerImpl.class );

  private float sortFactor = 1024; // Mb / sec
  private Amount<DataAmount> sortBlockSize;
  private long numMappers;
  private Amount<DataAmount> size;

  public ShufflerImpl( Amount<DataAmount> sortBlockSize, long numMappers, Amount<DataAmount> size )
    {
    this.sortBlockSize = sortBlockSize;
    this.numMappers = numMappers;
    this.size = size;
    }

  @Blocking
  public void execute( Network network )
    {
    // fetch
    // should fetch through network object
    network.read( size );

    // sort
    // assumes O(n log n)
    double bigO = size.longValue( Bandwidth.MB ) * Math.log10( size.longValue( Bandwidth.MB ) );
    double sortSleep = bigO / sortFactor * 1000;

    LOG.debug( "bigO = {} sortSleep = {}", bigO, sortSleep );

    Kronos.blockingSleep( (long) sortSleep );
    }
  }
