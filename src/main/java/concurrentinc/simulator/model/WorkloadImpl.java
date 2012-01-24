/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
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
  private final BiMap<MRJobParams, MRJobImpl> paramJobMap = HashBiMap.create();

  private WorkloadParams workloadParams;

  public WorkloadImpl( WorkloadParams workloadParams )
    {
    this();
    this.workloadParams = workloadParams;

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

  public WorkloadParams getResultWorkloadParams()
    {
    Map<MRJob, MRJobParams> paramsMap = new HashMap<MRJob, MRJobParams>();
    MRJobParamsGraph resultMRParamsGraph = new MRJobParamsGraph();
    Iterator<MRJob> iterator = getTopologicalIterator();

    while( iterator.hasNext() )
      {
      MRJob current = iterator.next();

      MRJobParams params = ( (MRJobImpl) current ).getResultMRJobParams();
      paramsMap.put( current, params );
      resultMRParamsGraph.addVertex( params );
      }

    for( MRJob fromJob : paramsMap.keySet() )
      {
      for( MRJob toJob : Graphs.successorListOf( this, fromJob ) )
        resultMRParamsGraph.addPath( paramsMap.get( fromJob ), paramsMap.get( toJob ) );
      }

    return new WorkloadParams( resultMRParamsGraph );
    }

  private void populate( MRJobParamsGraph mrParamsGraph )
    {
    Iterator<MRJobParams> paramsIterator = mrParamsGraph.getTopologicalIterator();

    while( paramsIterator.hasNext() )
      {
      MRJobParams params = paramsIterator.next();
      MRJobImpl mrJob = new MRJobImpl( params );

      paramJobMap.put( params, mrJob );

      addVertex( mrJob );
      }

    for( Integer integer : mrParamsGraph.edgeSet() )
      {
      MRJobParams source = mrParamsGraph.getEdgeSource( integer );
      MRJobParams target = mrParamsGraph.getEdgeTarget( integer );

      addEdge( paramJobMap.get( source ), paramJobMap.get( target ), integer );
      }
    }

  public Iterator<MRJob> getTopologicalIterator()
    {
    return new TopologicalOrderIterator<MRJob, Integer>( this );
    }

  public Iterator<MRJob> getReverseTopologicalIterator()
    {
    SimpleDirectedGraph<MRJob, Integer> graph = new SimpleDirectedGraph<MRJob, Integer>( Integer.class );

    Graphs.addGraphReversed( graph, this );

    return new TopologicalOrderIterator<MRJob, Integer>( graph );
    }

  public void start( Cluster cluster )
    {
    Iterator<MRJob> jobsIterator = getTopologicalIterator();

    while( jobsIterator.hasNext() )
      {
      MRJob mrJob = jobsIterator.next();
      List<MRJob> predecessors = Graphs.predecessorListOf( this, mrJob );

      // don't get the actual data, use the passed params map data
      if( predecessors.size() == 0 )
        mrJob.startJob( cluster, workloadParams.sources.get( getJobParamsFor( mrJob ) ) );
      else
        mrJob.startJobAfterPredecessors( cluster, predecessors );
      }
    }

  private MRJobParams getJobParamsFor( MRJob mrJob )
    {
    return paramJobMap.inverse().get( mrJob );
    }
  }
