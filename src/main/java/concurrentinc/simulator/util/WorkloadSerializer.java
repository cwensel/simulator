/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import concurrentinc.simulator.params.MRJobParams;
import concurrentinc.simulator.params.WorkloadParams;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.jgrapht.Graphs;

/**
 *
 */
public class WorkloadSerializer extends JsonSerializer<WorkloadParams>
  {
  @Override
  public Class<WorkloadParams> handledType()
    {
    return WorkloadParams.class;
    }

  @Override
  public void serialize( WorkloadParams value, JsonGenerator jsonGenerator, SerializerProvider provider ) throws IOException, JsonProcessingException
    {
    jsonGenerator.writeStartObject();

    jsonGenerator.writeNumberField( "totalSourceMB", value.getSourceSizeMB() );
    jsonGenerator.writeNumberField( "totalSinkMB", value.getSinkSizeMB() );

    Iterator<MRJobParams> iterator = value.mrParams.getTopologicalIterator();

    while( iterator.hasNext() )
      {
      MRJobParams next = iterator.next();

      jsonGenerator.writeObjectField( Integer.toString( System.identityHashCode( next ) ), next );

      List<MRJobParams> successors = Graphs.successorListOf( value.mrParams, next );

      if( !successors.isEmpty() )
        {
        jsonGenerator.writeArrayFieldStart( "successors" );

        for( MRJobParams successor : successors )
          jsonGenerator.writeString( Integer.toString( System.identityHashCode( successor ) ) );

        jsonGenerator.writeEndArray();
        }
      }

    jsonGenerator.writeEndObject();
    }
  }
