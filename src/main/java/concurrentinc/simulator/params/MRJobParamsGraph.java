/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.params;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import concurrentinc.simulator.util.Printable;
import concurrentinc.simulator.util.Printer;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

/**
 *
 */
public class MRJobParamsGraph extends SimpleDirectedGraph<MRJobParams, Integer> implements Printable
  {
  public MRJobParamsGraph( MRJobParams... jobParams )
    {
    this();

    addPath( jobParams );
    }

  public MRJobParamsGraph()
    {
    super( new EdgeFactory<MRJobParams, Integer>()
    {
    int count = 0;

    public Integer createEdge( MRJobParams mrJobParams, MRJobParams mrJobParams1 )
      {
      return count++;
      }
    } );
    }

  public void addPath( MRJobParams... jobParams )
    {
    MRJobParams last = null;

    for( MRJobParams jobParam : jobParams )
      {
      addVertex( jobParam );

      if( last != null )
        addEdge( last, jobParam );

      last = jobParam;
      }
    }

  public Iterator<MRJobParams> getTopologicalIterator()
    {
    return new TopologicalOrderIterator<MRJobParams, Integer>( this );
    }

  public List<MRJobParams> getOrigins()
    {
    List<MRJobParams> list = new ArrayList<MRJobParams>();
    Iterator<MRJobParams> iterator = getTopologicalIterator();

    while( iterator.hasNext() )
      {
      MRJobParams params = iterator.next();

      if( inDegreeOf( params ) != 0 )
        break;

      list.add( params );
      }

    return list;
    }

  private int size()
    {
    return vertexSet().size();
    }

  public String printFields()
    {
    List<String> fields = Printer.createSimpleFields( MRJobParams.class );

    int size = size();

    if( size == 1 )
      return Printer.join( fields.toArray(), "\t" );

    ArrayList<String> list = new ArrayList<String>();

    for( int i = 0; i < size; i++ )
      {
      for( String field : fields )
        list.add( String.format( "%s.%d", field, i++ ) );
      }

    return Printer.join( list.toArray(), "\t" );
    }

  public String print()
    {
    List<String> list = new ArrayList<String>();
    Iterator<MRJobParams> iterator = getTopologicalIterator();

    while( iterator.hasNext() )
      {
      MRJobParams params = iterator.next();

      list.add( Printer.print( params ) );
      }

    return Printer.join( list.toArray(), "\t" );
    }

  public List<MRJobParams> getPredecessors( MRJobParams param )
    {
    return Graphs.predecessorListOf( this, param );
    }
  }
