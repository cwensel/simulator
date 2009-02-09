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
import com.hellblazer.primeMover.Entity;
import com.hellblazer.primeMover.Blocking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Entity({Mapper.class})
public class MapperImpl implements Mapper
  {
  private static final Logger LOG = LoggerFactory.getLogger( MapperImpl.class );

  private DistributedData data;
  private float processingFactor;
  private long allocatedSizeMb;

  public MapperImpl( DistributedData data, float processingFactor, long allocatedSizeMb )
    {
    this.data = data;
    this.processingFactor = processingFactor;
    this.allocatedSizeMb = allocatedSizeMb;
    }

  @Blocking
  public void execute( Network network, int runningMapProcesses )
    {
    blockReadingData( network, runningMapProcesses );
    blockProcessingData();
    }

  private void blockReadingData( Network network, int runningJobMapProcesses )
    {
    data.read( network, allocatedSizeMb, runningJobMapProcesses );
    }

  private void blockProcessingData()
    {
    float mapperSleep = allocatedSizeMb / processingFactor * 1000;

    if( LOG.isDebugEnabled() )
      LOG.debug( "mapperSleep = " + mapperSleep );

    Kronos.sleep( (long) mapperSleep );
    }
  }
