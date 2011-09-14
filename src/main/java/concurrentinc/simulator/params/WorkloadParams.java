/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.params;

import java.util.List;

import concurrentinc.simulator.model.DistributedData;
import concurrentinc.simulator.util.PrintableImpl;

/**
 *
 */
public class WorkloadParams extends PrintableImpl
  {
  public DistributedData distributedData;
  public MRJobParamsGraph mrParams;

  public WorkloadParams( double inputSizeMb, MRJobParams... mrParams )
    {
    this.distributedData = new DistributedData( inputSizeMb );
    this.mrParams = new MRJobParamsGraph( mrParams );
    }

  public WorkloadParams( double inputSizeMb, MRJobParamsGraph mrParams )
    {
    this.distributedData = new DistributedData( inputSizeMb );
    this.mrParams = mrParams;
    }

  public WorkloadParams( double inputSizeMb, MRJobParamsGraph mrParams, int blockSizeMb, int fileReplication )
    {
    this.distributedData = new DistributedData( inputSizeMb, blockSizeMb, fileReplication );
    this.mrParams = mrParams;
    }

  public WorkloadParams( DistributedData distributedData, MRJobParamsGraph mrParams )
    {
    this.distributedData = distributedData;
    this.mrParams = mrParams;
    }

  public MRJobParams getMRParams()
    {
    List<MRJobParams> origins = mrParams.getOrigins();

    if( origins.size() != 1 )
      throw new IllegalStateException( "too many origins, found: " + origins.size() );

    return origins.get( 0 );
    }

  public double getInputSizeMB()
    {
    return distributedData.sizeMb;
    }

  public MapperParams getMapperParams()
    {
    return getMRParams().mapper;
    }

  public ReducerParams getReducerParams()
    {
    return getMRParams().reducer;
    }
  }
