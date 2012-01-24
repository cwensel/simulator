/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
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

  public void read( double sizeMb )
    {
    long readSleepMs = (long) ( sizeMb / networkParams.getEffectiveReadMbS() * 1000 );

    LOG.debug( "readSleepMs = {}", readSleepMs );

    Kronos.blockingSleep( readSleepMs );
    }

  public void write( double sizeMB )
    {
    long writeSleepMs = (long) ( sizeMB / networkParams.getEffectiveWriteMbS() * 1000 );

    LOG.debug( "writeSleepMs = {}", writeSleepMs );

    Kronos.blockingSleep( writeSleepMs );
    }
  }
