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

/**
 *
 */
public class Reducer
  {
  private DistributedData data;
  float processingFactor;
  long processSizeMb;
  private long outputSizeMb;

  public Reducer( DistributedData data, float processingFactor, long processSizeMb, long outputSizeMb )
    {
    this.data = data;
    this.processingFactor = processingFactor;
    this.processSizeMb = processSizeMb;
    this.outputSizeMb = outputSizeMb;
    }

  public void execute()
    {
    blockProcessing();
    blockWriting();
    }

  private void blockWriting()
    {
    data.write( outputSizeMb );
    }

  private void blockProcessing()
    {
    float reducerSleep = processSizeMb / processingFactor * 1000;

    System.out.println( "reducerSleep = " + reducerSleep );

    Kronos.sleep( (long) reducerSleep );
    }
  }