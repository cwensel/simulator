/*
 * Copyright (c) 2007-2009 Concurrent, Inc. All Rights Reserved.
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

package concurrentinc.simulator.util;

import java.lang.reflect.Field;
import java.util.*;

/**
 *
 */
public class Printer
  {
  public static String print( Printable printable )
    {
    if( printable == null )
      return "";

    Field[] fields = printable.getClass().getFields();

    if( fields.length == 0 )
      throw new IllegalStateException( "no printable fields in: " + printable.getClass().getName() );

    List all = new ArrayList();

    try
      {
      for( Field field : fields )
        {
        Object value = field.get( printable );

        if( Printable.class.isAssignableFrom( field.getType() ) )
          all.add( print( (Printable) value ) );
        else
          all.add( value == null ? "" : value );
        }
      }
    catch( IllegalAccessException e )
      {
      e.printStackTrace();
      }

    return join( all.toArray(), "\t" );
    }

  public static String printFields( Class printable )
    {
    ArrayList all = new ArrayList();

    printFields( null, printable, all );

    return join( simplify( all ).toArray(), "\t" );
    }

  static void printFields( String prefix, Class printable, List all )
    {
    Field[] fields = printable.getFields();

    for( Field field : fields )
      {
      String path = join( ".", prefix, field.getName() );

      if( Printable.class.isAssignableFrom( field.getType() ) )
        printFields( path, field.getType(), all );
      else
        all.add( path );
      }
    }

  public static String join( String delim, String... strings )
    {
    return join( strings, delim );
    }

  public static String join( Object[] list, String delim )
    {
    StringBuffer buffer = new StringBuffer();

    for( Object s : list )
      {
      if( s == null )
        continue;

      if( buffer.length() != 0 )
        buffer.append( delim );

      buffer.append( s );
      }

    return buffer.toString();
    }

  static List simplify( List<String> list )
    {
    int depth = 0;
    Set last = null;

    while( true )
      {
      Set set = new LinkedHashSet();

      int maxLength = 0;

      for( String string : list )
        {
        String[] split = string.split( "\\." );

        int from = Math.min( depth, split.length - 1 );
        int to = split.length;

        String[] result = Arrays.copyOfRange( split, from, to );
        maxLength = Math.max( maxLength, result.length );
        String simple = join( result, "." );

        set.add( simple );
        }

      if( maxLength == 1 || set.size() != list.size() )
        break;

      last = set;
      depth++;
      }

    if( last == null )
      return list;

    return new ArrayList( last );
    }

  }
