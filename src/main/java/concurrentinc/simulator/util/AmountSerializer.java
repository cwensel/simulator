/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.util;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.jscience.physics.amount.Amount;

/**
 *
 */
public class AmountSerializer extends JsonSerializer<Amount>
  {
  @Override
  public Class<Amount> handledType()
    {
    return Amount.class;
    }

  @Override
  public void serialize( Amount value, JsonGenerator jsonGenerator, SerializerProvider provider ) throws IOException, JsonProcessingException
    {
    jsonGenerator.writeString( value.toString() );
    }
  }
