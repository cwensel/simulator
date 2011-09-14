/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

//    if( fields.length == 0 )
//      return "";

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
    List list = createSimpleFields( printable );

    return join( list.toArray(), "\t" );
    }

  public static List<String> createSimpleFields( Class printable )
    {
    ArrayList all = new ArrayList();

    createFields( null, printable, all );

    return simplify( all );
    }

  public static void createFields( String prefix, Class printable, List all )
    {
    Field[] fields = printable.getFields();

    for( Field field : fields )
      {
      String path = join( ".", prefix, field.getName() );

      if( Printable.class.isAssignableFrom( field.getType() ) )
        createFields( path, field.getType(), all );
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
