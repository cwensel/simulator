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
@Entity({Mapper.class})
public class MapperImpl implements Mapper
  {
  private static final Logger LOG = LoggerFactory.getLogger( MapperImpl.class );

  private DistributedData data;
  private float processingFactor;
  private long allocatedSizeMb;

  public MapperImpl( DistributedData data, float processingFactor, long allocatedSizeMb )
    {
    this.data = data;
    this.processingFactor = processingFactor;
    this.allocatedSizeMb = allocatedSizeMb;
    }

  public DistributedData getOutputData()
    {
    return data;
    }

  @Blocking
  public void execute( Network network, int runningMapProcesses )
    {
    data.read( network, allocatedSizeMb, runningMapProcesses );

    float mapperSleep = allocatedSizeMb / processingFactor * 1000;

    LOG.debug( "mapperSleep = {}", mapperSleep );

    Kronos.blockingSleep( (long) mapperSleep );
    }
  }
