/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.params;

import java.util.ArrayList;
import java.util.List;

import concurrentinc.simulator.model.DistributedData;
import concurrentinc.simulator.util.PrintableImpl;

/**
 *
 */
public class MRJobParams extends PrintableImpl
  {
  public double blockSizeMb = 128;
  public int fileReplication = 3;

  public List<DistributedData> sources = new ArrayList<DistributedData>();
  public List<DistributedData> sinks = new ArrayList<DistributedData>();

  public MapperParams mapper;
  public ReducerParams reducer;

  public MRJobParams( MapperParams mapper, ReducerParams reducer )
    {
    this.mapper = mapper;
    this.reducer = reducer;
    }

  public MRJobParams( int numProcessesMapper, float dataFactorMapper, float processingBandwidthMapper, int numProcessesReducer, float dataFactorReducer, float processingBandwidthReducer )
    {
    this.mapper = new MapperParams( numProcessesMapper, dataFactorMapper, processingBandwidthMapper );
    this.reducer = new ReducerParams( numProcessesReducer, dataFactorReducer, processingBandwidthReducer );
    }

  public MRJobParams( int numProcessesMapper, float dataFactorMapper, float processingBandwidthMapper, int numProcessesReducer, float dataFactorReducer, float processingBandwidthReducer, List<DistributedData> sources, List<DistributedData> sinks )
    {
    this.mapper = new MapperParams( numProcessesMapper, dataFactorMapper, processingBandwidthMapper );
    this.reducer = new ReducerParams( numProcessesReducer, dataFactorReducer, processingBandwidthReducer );

    this.sources.addAll( sources );
    this.sinks.addAll( sinks );
    }

  public MRJobParams( int numProcessesMapper, int numProcessesReducer )
    {
    this.mapper = new MapperParams( numProcessesMapper );
    this.reducer = new ReducerParams( numProcessesReducer );
    }

  public double getTotalSourceMB()
    {
    return DistributedData.totalDataSizeMB( sources );
    }

  public double getTotalSinkMB()
    {
    return DistributedData.totalDataSizeMB( sinks );
    }
  }
