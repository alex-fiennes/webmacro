/*
 * Copyright (C) 1998-2000 Semiotek Inc.  All Rights Reserved.  
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted under the terms of either of the following
 * Open Source licenses:
 *
 * The GNU General Public License, version 2, or any later version, as
 * published by the Free Software Foundation
 * (http://www.fsf.org/copyleft/gpl.html);
 *
 *  or 
 *
 * The Semiotek Public License (http://webmacro.org/LICENSE.)  
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See www.webmacro.org for more information on the WebMacro project.  
 */


package org.webmacro.util;
import java.util.Date;

/**
  * This is an optimization. "System.currentTimeMillis()" is a relatively 
  * slow method, and "new Date()" is an incredibly expensive operation. 
  * This clock performs these operations at regular intervals and makes
  * the result of the calculations available. You can therefore use this
  * class to gain rapid access to the current time in situations where
  * it is good enough to have "close" to the current time. 
  */
final public class Clock
{

   /**
     * Every tick interval the following variable is updated with the current system time
     */
   static public volatile long TIME;

   /**
     * Date information
     */
   private static long dateTime = System.currentTimeMillis();
   private static Date date = new Date();

   /**
     * The current date. This object is updated on the tick interval, 
     * but not faster than once per second.
     */
   public static Date getDate() {
      synchronized(_clock) {
         if ((TIME - dateTime) > 1000) {
            date = new Date(TIME);
         }
         return date;
      }
   }

   /**
     * The tick interval, how fast the clock updates the time. The default 
     * is once every 10 seconds.
     */
   static private int tickInterval = 10000;

   /**
     * The clock will tick at least this often. It may tick more often.
     * Setting it to zero stops the clock. The actual tick interval used
     * will be the smallest tick interval ever set. The tickInterval 
     * starts out as 10000 (ten seconds).
     */
   static public void setTickInterval(int interval) {
      if (interval < 0) interval = 0;

      if ((tickInterval == 0) || (interval < tickInterval)) 
      {
         tickInterval = interval;
         synchronized(_clock) {
            _clock.notify();
            _clock.setName("clock:" + tickInterval);
         }
      }
   }

   /**
     * Set up the clock
     */
   static private final Thread _clock = new Thread() 
   {
      synchronized public void run() {
         while (true) {
            TIME = System.currentTimeMillis();
            try {
               if (tickInterval == 0) wait();
               else wait(tickInterval);
            } catch (Exception e) { 
               e.printStackTrace();
            }
         }
      }
   };
   static {
      _clock.setDaemon(true);
      _clock.setName("clock:" + tickInterval);
      _clock.start();
   }

   public static void main(String arg[]) {
      setTickInterval(1000);
      while (true) {
         System.out.println(TIME);
      }
   }

}


