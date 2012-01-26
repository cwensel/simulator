/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.util;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.map.module.SimpleSerializers;

/**
 *
 */
public class JsonPrinter
  {
  ObjectMapper mapper;
  ObjectWriter writer;

  private SimpleSerializers serializers;

  public JsonPrinter()
    {
    mapper = getMapper();
    writer = mapper.writer();
    }

  public String print( Object object )
    {
    try
      {
      return writer.writeValueAsString( object );
      }
    catch( IOException exception )
      {
      throw new RuntimeException( "failed creating json", exception );
      }
    }

  public void print( File file, Object object )
    {
    try
      {
      writer.writeValue( file, object );
      }
    catch( IOException exception )
      {
      throw new RuntimeException( "failed creating json", exception );
      }
    }

  private ObjectMapper getMapper()
    {
    ObjectMapper mapper = new ObjectMapper();

    SimpleModule module = new SimpleModule( "SimulatorModule", new Version( 1, 0, 0, null ) );
    serializers = new SimpleSerializers();

    serializers.addSerializer( new WorkloadSerializer() );

    module.setSerializers( serializers );

    mapper.registerModule( module );

    return mapper;
    }
  }
