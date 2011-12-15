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
  public List<DistributedData> distributedData;
  public MRJobParamsGraph mrParams;

  public WorkloadParams( MRJobParams... mrJobParams )
    {
    this( new MRJobParamsGraph( mrJobParams ) );
    }

  public WorkloadParams( MRJobParamsGraph mrParams )
    {
    this.distributedData = mrParams.getOrigins().get( 0 ).source;
    this.mrParams = mrParams;
    }

  public MRJobParams getMRParams()
    {
    List<MRJobParams> origins = mrParams.getOrigins();

    if( origins.size() != 1 )
      throw new IllegalStateException( "too many origins, found: " + origins.size() );

    return origins.get( 0 );
    }

  /**
   * use for testing
   *
   * @return
   */
  public double getInputSizeMB()
    {
    return DistributedData.totalDataSize( distributedData );
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
