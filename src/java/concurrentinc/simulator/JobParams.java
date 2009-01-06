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

import java.util.Arrays;

/**
 *
 */
public class JobParams
  {
  private String[] fields = new String[]{"inputSizeMb", "shuffleSizeMb", "outputSizeMb", "numMappers", "numReducers", "networkBandwidth", "blockSizeMb", "fileReplication"};

  public int inputSizeMb;
  public int shuffleSizeMb;
  public int outputSizeMb;
  public int numMappers;
  public int numReducers;
  public float networkBandwidth = 10 * 1024; // Mb / sec;
  public int blockSizeMb = 128;
  public int fileReplication = 3;

  public JobParams( int inputSizeMb, int shuffleSizeMb, int outputSizeMb, int numMappers, int numReducers )
    {
    this.inputSizeMb = inputSizeMb;
    this.shuffleSizeMb = shuffleSizeMb;
    this.outputSizeMb = outputSizeMb;
    this.numMappers = numMappers;
    this.numReducers = numReducers;
    }

  public JobParams( int inputSizeMb, int shuffleSizeMb, int outputSizeMb, int numMappers, int numReducers, float networkBandwidth, int blockSizeMb, int fileReplication )
    {
    this.inputSizeMb = inputSizeMb;
    this.shuffleSizeMb = shuffleSizeMb;
    this.outputSizeMb = outputSizeMb;
    this.numMappers = numMappers;
    this.numReducers = numReducers;
    this.networkBandwidth = networkBandwidth;
    this.blockSizeMb = blockSizeMb;
    this.fileReplication = fileReplication;
    }

  @Override
  public String toString()
    {
    return "JobParams{" + "fields=" + ( fields == null ? null : Arrays.asList( fields ) ) + ", inputSizeMb=" + inputSizeMb + ", shuffleSizeMb=" + shuffleSizeMb + ", outputSizeMb=" + outputSizeMb + ", numMappers=" + numMappers + ", numReducers=" + numReducers + ", networkBandwidth=" + networkBandwidth + ", blockSizeMb=" + blockSizeMb + ", fileReplication=" + fileReplication + '}';
    }

  public String print()
    {
    return inputSizeMb + "\t" + shuffleSizeMb + "\t" + outputSizeMb + "\t" + numMappers + "\t" + numReducers + "\t" + networkBandwidth + "\t" + blockSizeMb + "\t" + fileReplication;
    }

  public String printFields()
    {
    return fields[ 0 ] + "\t" + fields[ 1 ] + "\t" + fields[ 2 ] + "\t" + fields[ 3 ] + "\t" + fields[ 4 ] + "\t" + fields[ 5 ] + "\t" + fields[ 6 ] + "\t" + fields[ 7 ];
    }
  }
