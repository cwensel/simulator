/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

import com.hellblazer.primeMover.Entity;

/**
 *
 */
@Entity({MapProcess.class})
public class MapProcessImpl implements MapProcess, TaskProcess
  {
  private MRJob job;
  private Mapper mapper;

  public MapProcessImpl( MRJob job, Mapper mapper )
    {
    this.job = (MRJobImpl) job;
    this.mapper = (MapperImpl) mapper;
    }

  @Override
  public DistributedData getOutputData()
    {
    return ( (MapperImpl) mapper ).getOutputData();
    }

  public void execute( Network network )
    {
    mapper.execute( network, job.runningMapProcess() );

    job.releaseMapProcess( this );
    }
  }
