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

/**
 *
 */
public class JobParams
  {
  int inputSizeMb;
  int shuffleSizeMb;
  int outputSizeMb;
  int numMappers;
  int numReducers;

  public JobParams( int inputSizeMb, int shuffleSizeMb, int outputSizeMb, int numMappers, int numReducers )
    {
    this.inputSizeMb = inputSizeMb;
    this.shuffleSizeMb = shuffleSizeMb;
    this.outputSizeMb = outputSizeMb;
    this.numMappers = numMappers;
    this.numReducers = numReducers;
    }

  @Override
  public String toString()
    {
    return "JobParams{" + "inputSizeMb=" + inputSizeMb + ", shuffleSizeMb=" + shuffleSizeMb + ", outputSizeMb=" + outputSizeMb + ", numMappers=" + numMappers + ", numReducers=" + numReducers + '}';
    }

  public String print()
    {
    return inputSizeMb + "\t" + shuffleSizeMb + "\t" + outputSizeMb + "\t" + numMappers + "\t" + numReducers;
    }
  }
