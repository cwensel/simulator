/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import com.hellblazer.primeMover.Blocking;
import com.hellblazer.primeMover.Entity;
import com.hellblazer.primeMover.Kronos;
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
  private long sortBlockSizeMb;
  private long numMappers;
  private long sizeMb;

  public ShufflerImpl( long sortBlockSizeMb, long numMappers, long sizeMb )
    {
    this.sortBlockSizeMb = sortBlockSizeMb;
    this.numMappers = numMappers;
    this.sizeMb = sizeMb;
    }

  @Blocking
  public void execute( Network network )
    {
    // fetch
    // should fetch through network object
    network.read( sizeMb );

    // sort
    // assumes O(n log n)
    double bigO = sizeMb * Math.log10( sizeMb );
    double sortSleep = bigO / sortFactor * 1000;

    LOG.debug( "bigO = {} sortSleep = {}", bigO, sortSleep );

    Kronos.blockingSleep( (long) sortSleep );
    }
  }
