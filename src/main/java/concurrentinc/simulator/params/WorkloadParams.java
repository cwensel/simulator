/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.params;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import concurrentinc.simulator.model.DistributedData;
import concurrentinc.simulator.util.PrintableImpl;

/**
 *
 */
public class WorkloadParams extends PrintableImpl
  {
  public MRJobParamsGraph mrParams;
  public Map<MRJobParams, List<DistributedData>> sources;

  public WorkloadParams( MRJobParams... mrJobParams )
    {
    this( new MRJobParamsGraph( mrJobParams ) );
    }

  public WorkloadParams( MRJobParamsGraph mrParams )
    {
    this.mrParams = mrParams;
    this.sources = new HashMap<MRJobParams, List<DistributedData>>();

    for( MRJobParams mrJobParams : mrParams.getOrigins() )
      this.sources.put( mrJobParams, mrJobParams.sources );
    }

  public WorkloadParams( MRJobParamsGraph mrParams, Map<MRJobParams, List<DistributedData>> sources )
    {
    this.mrParams = mrParams;
    this.sources = sources;
    }

  /*
   * use for testing
   */

  public MRJobParams getMRParams()
    {
    List<MRJobParams> origins = mrParams.getOrigins();

    if( origins.size() != 1 )
      throw new IllegalStateException( "too many origins, found: " + origins.size() );

    return origins.get( 0 );
    }

  public double getInputSizeMB()
    {
    Set<DistributedData> sourceSet = new HashSet<DistributedData>();

    for( List<DistributedData> datas : sources.values() )
      sourceSet.addAll( datas );

    return DistributedData.totalDataSize( sourceSet );
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
