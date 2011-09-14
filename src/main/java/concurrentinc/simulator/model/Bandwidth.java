/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;

/**
 *
 */
public interface Bandwidth
  {
  long MB = 1;
  long GB = MB * 1024;
  long TB = GB * 1024;

  // see http://en.wikipedia.org/wiki/List_of_device_bit_rates

  double Megabit1 = 1.25 * MB; // 10BASE-X
  double Megabit10 = 12.5 * MB; // 100BASE-X
  double Gigabit1 = 125 * MB; // 1000BASE-X
  double Gigabit10 = 1.25 * GB; // 10GBASE-X
  double Gigabit100 = 12.5 * GB; // 100GBASE-X
  }