/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import com.hellblazer.primeMover.Blocking;
import com.hellblazer.primeMover.Entity;
import com.hellblazer.primeMover.Kronos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Entity({Reducer.class})
public class ReducerImpl implements Reducer
  {
  private static final Logger LOG = LoggerFactory.getLogger( ReducerImpl.class );

  private DistributedData data;
  private float processingFactor;
  private long processSizeMb;
  private long outputSizeMb;

  public ReducerImpl( DistributedData data, float processingFactor, long processSizeMb, long outputSizeMb )
    {
    this.data = data;
    this.processingFactor = processingFactor;
    this.processSizeMb = processSizeMb;
    this.outputSizeMb = outputSizeMb;
    }

  public DistributedData getOutputData()
    {
    return data;
    }

  @Blocking
  public void execute( Network network )
    {
    float reducerSleep = processSizeMb / processingFactor * 1000;

    LOG.debug( "reducerSleep = {}", reducerSleep );

    Kronos.blockingSleep( (long) reducerSleep );

    data.write( network, outputSizeMb );
    }

  }
