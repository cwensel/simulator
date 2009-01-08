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

package concurrentinc.simulator.params;

import concurrentinc.simulator.util.Printable;
import concurrentinc.simulator.util.Printer;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class MRJobParamsGraph extends SimpleDirectedGraph<MRJobParams, Integer> implements Printable
  {
  public MRJobParamsGraph( MRJobParams... jobParams )
    {
    this();

    addChained( jobParams );
    }

  public void addChained( MRJobParams... jobParams )
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

  public Iterator<MRJobParams> getToplogicalIterator()
    {
    return new TopologicalOrderIterator<MRJobParams, Integer>( this );
    }

  public List<MRJobParams> getOrigins()
    {
    List<MRJobParams> list = new ArrayList<MRJobParams>();
    Iterator<MRJobParams> iterator = getToplogicalIterator();

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
    Iterator<MRJobParams> iterator = getToplogicalIterator();

    while( iterator.hasNext() )
      {
      MRJobParams params = iterator.next();

      list.add( Printer.print( params ) );
      }

    return Printer.join( list.toArray(), "\t" );
    }
  }
