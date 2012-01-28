/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package concurrentinc.simulator.model;


import javax.measure.quantity.DataAmount;
import javax.measure.quantity.DataRate;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.measure.unit.UnitFormat;

import static javax.measure.unit.SI.GIGA;
import static javax.measure.unit.SI.MEGA;

/** Nominal bandwidth for common interfaces. */
public class Bandwidth
  {
  public static Unit<DataAmount> KB = NonSI.BYTE.times( 1024 );
  public static Unit<DataAmount> MB = KB.times( 1024 );
  public static Unit<DataAmount> GB = MB.times( 1024 );
  public static Unit<DataAmount> TB = GB.times( 1024 );

  // see http://en.wikipedia.org/wiki/List_of_device_bit_rates

  public static Unit<DataRate> Megabit1 = MEGA( DataRate.UNIT ); // 10BASE-X
  public static Unit<DataRate> Megabit10 = MEGA( DataRate.UNIT ).times( 10 ); // 100BASE-X
  public static Unit<DataRate> Gigabit1 = GIGA( DataRate.UNIT ); // 1000BASE-X
  public static Unit<DataRate> Gigabit10 = GIGA( DataRate.UNIT ).times( 10 ); // 10GBASE-X
  public static Unit<DataRate> Gigabit100 = GIGA( DataRate.UNIT ).times( 100 ); // 100GBASE-X

  static
    {
    UnitFormat.getInstance().label( KB, "KB" );
    UnitFormat.getInstance().label( MB, "MB" );
    UnitFormat.getInstance().label( GB, "GB" );
    UnitFormat.getInstance().label( TB, "TB" );

    UnitFormat.getInstance().label( Megabit1, "Mb" );
    UnitFormat.getInstance().label( Gigabit1, "Gb" );
    }
  }