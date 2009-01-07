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

package concurrentinc.simulator;

import concurrentinc.simulator.util.Printable;

/**
 *
 */
public class MRJobParams extends Printable
  {
  public MapperParams mapper;
  public ReducerParams reducer;

  public MRJobParams( MapperParams mapper, ReducerParams reducer )
    {
    this.mapper = mapper;
    this.reducer = reducer;
    }

  public MRJobParams( int numProcessesMapper, float dataFactorMapper, float processingBandwidthMapper, int numProcessesReducer, float dataFactorReducer, float processingBandwidthReducer )
    {
    this.mapper = new MapperParams( numProcessesMapper, dataFactorMapper, processingBandwidthMapper );
    this.reducer = new ReducerParams( numProcessesReducer, dataFactorReducer, processingBandwidthReducer );
    }

  public MRJobParams( int numProcessesMapper, int numProcessesReducer )
    {
    this.mapper = new MapperParams( numProcessesMapper );
    this.reducer = new ReducerParams( numProcessesReducer );
    }
  }
