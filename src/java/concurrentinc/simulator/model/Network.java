/*
 * Copyright (c) 2007-2009 Concurrent, Inc. All Rights Reserved.
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
import concurrentinc.simulator.params.NetworkParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
//@Entity
public class Network
  {
  private static final Logger LOG = LoggerFactory.getLogger( Network.class );
  NetworkParams networkParams;

  public Network()
    {
    }

  public Network( NetworkParams networkParams )
    {
    this.networkParams = networkParams;
    }

  //  @Blocking
  public void read( long sizeMb )
    {
    float readSleep = sizeMb / networkParams.bandwidth * 1000;

    if( LOG.isDebugEnabled() )
      LOG.debug( "readSleep = " + readSleep );

    Kronos.sleep( (long) readSleep );
    }

  //  @Blocking
  public void write( long amountMb )
    {
    float writeSleep = amountMb / networkParams.bandwidth * 1000;

    if( LOG.isDebugEnabled() )
      LOG.debug( "writeSleep = " + writeSleep );

    Kronos.sleep( (long) writeSleep );
    }
  }
