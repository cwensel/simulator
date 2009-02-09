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

import com.hellblazer.primeMover.Entity;

/**
 *
 */
@Entity({ReduceProcess.class})
public class ReduceProcessImpl implements ReduceProcess
  {
  private MRJob job;
  private Shuffler shuffler;
  private Reducer reducer;

  public ReduceProcessImpl( MRJob job, Shuffler shuffler, Reducer reducer )
    {
    this.job = job;
    this.shuffler = shuffler;
    this.reducer = reducer;
    }

  public void execute( Network network )
    {
    job.runningReduceProcess();

    shuffler.execute( network );
    reducer.execute( network );

    job.releaseReduceProcess( this );
    }

  }
