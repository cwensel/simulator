/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import com.hellblazer.primeMover.Entity;
import com.hellblazer.primeMover.Kronos;
import concurrentinc.simulator.params.NetworkParams;
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

  public void read( long sizeMb )
    {
    long readSleep = (long) ( sizeMb / networkParams.bandwidthMbS * 1000 );

    LOG.debug( "readSleep = {}", readSleep );

    Kronos.blockingSleep( (long) readSleep );
    }

  public void write( long sizeMB )
    {
    long writeSleep = (long) ( sizeMB / networkParams.bandwidthMbS * 1000 );

    LOG.debug( "writeSleep = {}", writeSleep );

    Kronos.blockingSleep( (long) writeSleep );
    }
  }
