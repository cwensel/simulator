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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class Shuffler
  {
  private static final Logger LOG = LoggerFactory.getLogger( Shuffler.class );

  private float sortFactor = 1024; // Gb / sec
  private float networkFactor;
  long sortBlockSizeMb;
  int numMappers;
  long sizeMb;

  public Shuffler( float networkFactor, long sortBlockSizeMb, int numMappers, long sizeMb )
    {
    this.networkFactor = networkFactor;
    this.sortBlockSizeMb = sortBlockSizeMb;
    this.numMappers = numMappers;
    this.sizeMb = sizeMb;
    }

  public void execute()
    {
    // fetch
    // should fetch through network object
    float fetchSleep = sizeMb / networkFactor * 1000;

    if( LOG.isDebugEnabled() )
      LOG.debug( "fetchSleep = " + fetchSleep );

    Kronos.sleep( (long) fetchSleep );

    // sort
    // assumes O(n log n)
    double bigO = sizeMb * Math.log10( sizeMb );
    double sortSleep = bigO / sortFactor * 1000;

    if( LOG.isDebugEnabled() )
      LOG.debug( "bigO = " + bigO + " sortSleep = " + sortSleep );

    Kronos.sleep( (long) sortSleep );
    }
  }
