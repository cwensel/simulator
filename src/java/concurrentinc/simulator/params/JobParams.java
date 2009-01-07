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

package concurrentinc.simulator.params;

import concurrentinc.simulator.util.Printable;

/**
 *
 */
public class JobParams extends Printable
  {
  public int inputSizeMb;
  public MRJobParams mrParams;
  public float networkBandwidth = 10 * 1024; // Mb / sec;
  public int blockSizeMb = 128;
  public int fileReplication = 3;

  public JobParams( int inputSizeMb, MRJobParams mrParams )
    {
    this.inputSizeMb = inputSizeMb;
    this.mrParams = mrParams;
    }

  public JobParams( int inputSizeMb, MRJobParams mrParams, float networkBandwidth, int blockSizeMb, int fileReplication )
    {
    this.inputSizeMb = inputSizeMb;
    this.mrParams = mrParams;
    this.networkBandwidth = networkBandwidth;
    this.blockSizeMb = blockSizeMb;
    this.fileReplication = fileReplication;
    }
  }
