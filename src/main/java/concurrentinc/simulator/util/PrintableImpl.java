/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.util;

/**
 *
 */
public class PrintableImpl implements Printable
  {
  public String printFields()
    {
    return Printer.printFields( getClass() );
    }

  public String print()
    {
    return Printer.print( this );
    }
  }