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

package concurrentinc.simulator;

import com.hellblazer.primeMover.Continuable;
import com.hellblazer.primeMover.Entity;
import com.hellblazer.primeMover.Channel;
import com.hellblazer.primeMover.Blocking;

import java.util.concurrent.Callable;

/**
 *
 */
public class MapProcess
  {
  private Channel channel;
  Mapper mapper;

  public MapProcess( Channel channel, Mapper mapper )
    {
    this.channel = channel;
    this.mapper = mapper;
    }

  public void execute()
    {
    System.out.println( "begin map process" );
    Object object = channel.take();
    mapper.execute();
    channel.put( object );
    System.out.println( "end map process" );
    }
  }
