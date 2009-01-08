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

import concurrentinc.simulator.params.JobParams;
import concurrentinc.simulator.params.MRJobParams;
import concurrentinc.simulator.params.MRJobParamsGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Job extends SimpleDirectedGraph<MRJob, Integer>
  {
  private DistributedData distributedData;
  private Cluster cluster;

  public Job( Cluster cluster, JobParams jobParams )
    {
    this();
    this.cluster = cluster;
    this.distributedData = jobParams.distributedData;

    populate( jobParams.mrParams );
    }

  private Job()
    {
    super( new EdgeFactory<MRJob, Integer>()
    {
    int count = 0;

    public Integer createEdge( MRJob mrJob, MRJob mrJob1 )
      {
      return count++;
      }
    } );

    }

  private void populate( MRJobParamsGraph mrParams )
    {
    Map<MRJobParams, MRJob> map = new HashMap<MRJobParams, MRJob>();
    Iterator<MRJobParams> paramsIterator = mrParams.getToplogicalIterator();

    while( paramsIterator.hasNext() )
      {
      MRJobParams params = paramsIterator.next();
      MRJob mrJob = new MRJob( cluster, params );

      map.put( params, mrJob );

      addVertex( mrJob );
      }

    for( Integer integer : mrParams.edgeSet() )
      {
      MRJobParams source = mrParams.getEdgeSource( integer );
      MRJobParams target = mrParams.getEdgeTarget( integer );

      addEdge( map.get( source ), map.get( target ), integer );
      }
    }

  public Iterator<MRJob> getToplogicalIterator()
    {
    return new TopologicalOrderIterator<MRJob, Integer>( this );
    }

  public Iterator<MRJob> getReverseToplogicalIterator()
    {
    SimpleDirectedGraph<MRJob, Integer> graph = new SimpleDirectedGraph<MRJob, Integer>( Integer.class );

    Graphs.addGraphReversed( graph, this );

    return new TopologicalOrderIterator<MRJob, Integer>( graph );
    }


  public void start()
    {
    Iterator<MRJob> jobsIterator = getToplogicalIterator();

    while( jobsIterator.hasNext() )
      {
      MRJob mrJob = jobsIterator.next();
      List<MRJob> predecessors = Graphs.predecessorListOf( this, mrJob );

      if( predecessors.size() != 0 )
        mrJob.startJob( predecessors );
      else
        mrJob.startJob( distributedData );
      }
    }
  }
