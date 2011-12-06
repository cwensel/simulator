/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

/** Nominal bandwidth for common interfaces. */
public class Bandwidth
  {
  public static long BytesInMb = 1 * 1024 * 1024;

  public static long MB = 1;
  public static long GB = MB * 1024;
  public static long TB = GB * 1024;

  // see http://en.wikipedia.org/wiki/List_of_device_bit_rates

  public static double Megabit1 = 1.25 * MB; // 10BASE-X
  public static double Megabit10 = 12.5 * MB; // 100BASE-X
  public static double Gigabit1 = 125 * MB; // 1000BASE-X
  public static double Gigabit10 = 1.25 * GB; // 10GBASE-X
  public static double Gigabit100 = 12.5 * GB; // 100GBASE-X

  public static double toMbFromBytes( double bytes )
    {
    return bytes / BytesInMb;
    }
  }