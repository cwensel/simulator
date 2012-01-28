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
@Entity({Mapper.class})
public class MapperImpl implements Mapper
  {
  private static final Logger LOG = LoggerFactory.getLogger( MapperImpl.class );

  private DistributedData inputData;
  private final Amount<DataAmount> splitSize;
  private DistributedData outputData;
  private float processingFactor;

  public MapperImpl( DistributedData inputData, Amount<DataAmount> splitSize, float processingFactor, DistributedData outputData )
    {
    this.inputData = inputData;
    this.splitSize = splitSize;
    this.processingFactor = processingFactor;
    this.outputData = outputData;
    }

  public DistributedData getOutputData()
    {
    return outputData;
    }

  @Blocking
  public void execute( Network network, int runningMapProcesses )
    {
    inputData.read( network, splitSize, runningMapProcesses );

    long mapperSleep = (long) ( splitSize.divide( processingFactor ).getEstimatedValue() * 1000 );

    LOG.debug( "mapperSleep = {}", mapperSleep );

    Kronos.blockingSleep( mapperSleep );
    }
  }
