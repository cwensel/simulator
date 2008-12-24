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

    Class<?> type = transformingClassLoader.loadClass( "concurrentinc.simulator.Runner" );
    Method method = type.getDeclaredMethod( "run", PrintWriter.class );


    PrintWriter printWriter = new PrintWriter( System.out );

    if( args.length == 1 )
      printWriter = new PrintWriter( new FileWriter( args[ 0 ] ) );

    method.invoke( null, printWriter );

    printWriter.flush();
    }

  }
