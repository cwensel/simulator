/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import javax.measure.quantity.DataAmount;

import com.hellblazer.primeMover.Blocking;
import com.hellblazer.primeMover.Entity;
import com.hellblazer.primeMover.Kronos;
import org.jscience.physics.amount.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Entity({Reducer.class})
public class ReducerImpl implements Reducer
  {
  private static final Logger LOG = LoggerFactory.getLogger( ReducerImpl.class );

  private DistributedData inputData;
  private float processingFactor;
  private Amount<DataAmount> processSize;
  private final DistributedData outputData;

  public ReducerImpl( DistributedData inputData, float processingFactor, Amount<DataAmount> processSize, DistributedData outputData )
    {
    this.inputData = inputData;
    this.processingFactor = processingFactor;
    this.processSize = processSize;
    this.outputData = outputData;
    }

  public DistributedData getOutputData()
    {
    return outputData;
    }

  @Blocking
  public void execute( Network network )
    {
    long reducerSleep = (long) ( processSize.longValue( Bandwidth.MB ) / processingFactor * 1000 );

    LOG.debug( "reducerSleep = {}", reducerSleep );

    Kronos.blockingSleep( (long) reducerSleep );

    inputData.write( network, outputData.size );
    }

  }
