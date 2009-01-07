/*
 * Copyright (c) 2007-2008 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Cascading is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cascading is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cascading.  If not, see <http://www.gnu.org/licenses/>.
 */

package concurrentinc.simulator;

import com.hellblazer.primeMover.transform.TransformingClassLoader;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 *
 */
public class Dump
  {

  public static void main( String[] args ) throws Exception
    {
    ClassLoader loader = Dump.class.getClassLoader();
    TransformingClassLoader transformingClassLoader = new TransformingClassLoader( loader, loader );
    Thread.currentThread().setContextClassLoader( transformingClassLoader );

    Class<?> runnerType = transformingClassLoader.loadClass( "concurrentinc.simulator.SimpleRunner" );
    Class<?> jobParamsType = transformingClassLoader.loadClass( "concurrentinc.simulator.JobParams" );
    Method method = runnerType.getDeclaredMethod( "run", PrintWriter.class, jobParamsType, jobParamsType, jobParamsType, float.class );

    PrintWriter printWriter = new PrintWriter( System.out );

    if( args.length == 1 )
      printWriter = new PrintWriter( new FileWriter( args[ 0 ] ) );

    Class<?> mrParamsType = transformingClassLoader.loadClass( "concurrentinc.simulator.MRJobParams" );

    Constructor<?> mrCtor = mrParamsType.getConstructor( int.class, int.class );
    Constructor<?> jobCtor = jobParamsType.getConstructor( int.class, mrParamsType );

    Object mrParamsStart = mrCtor.newInstance( 1, 1 );
    Object mrParamsEnd = mrCtor.newInstance( 1001, 1001 );
    Object mrParamsIncrement = mrCtor.newInstance( 5, 5 );

    Object start = jobCtor.newInstance( 1 * 1024 * 1024, mrParamsStart );
    Object end = jobCtor.newInstance( 1 * 1024 * 1024, mrParamsEnd );
    Object increment = jobCtor.newInstance( 1024, mrParamsIncrement );

    float sample = 0.01f;

    method.invoke( null, printWriter, start, end, increment, sample );

    printWriter.flush();
    }

  }
