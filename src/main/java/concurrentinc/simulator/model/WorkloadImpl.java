/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hellblazer.primeMover.Entity;
import concurrentinc.simulator.params.MRJobParams;
import concurrentinc.simulator.params.MRJobParamsGraph;
import concurrentinc.simulator.params.WorkloadParams;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

/**
 *
 */
@Entity({Workload.class})
public class WorkloadImpl extends SimpleDirectedGraph<MRJob, Integer> implements Workload
  {
  private DistributedData distributedData;

  public WorkloadImpl( WorkloadParams workloadParams )
    {
    this();
    this.distributedData = workloadParams.distributedData;

    populate( workloadParams.mrParams );
    }

  private WorkloadImpl()
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
    Map<MRJobParams, MRJobImpl> map = new HashMap<MRJobParams, MRJobImpl>();
    Iterator<MRJobParams> paramsIterator = mrParams.getTopologicalIterator();

    while( paramsIterator.hasNext() )
      {
      MRJobParams params = paramsIterator.next();
      MRJobImpl mrJob = new MRJobImpl( params );

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

  public void start( Cluster cluster )
    {
    Iterator<MRJob> jobsIterator = getToplogicalIterator();

    while( jobsIterator.hasNext() )
      {
      MRJob mrJob = jobsIterator.next();
      List<MRJob> predecessors = Graphs.predecessorListOf( this, mrJob );

      if( predecessors.size() != 0 )
        mrJob.startJob( cluster, predecessors );
      else
        mrJob.startJob( cluster, distributedData );
      }
    }
  }
