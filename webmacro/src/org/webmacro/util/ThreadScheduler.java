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

/**
  * Creates scheduling events at regular intervals by running a high 
  * priority thread which sleeps in a loop.
  */
final public class ThreadScheduler
{
   private static Thread _scheduler;
   final private static Object lock = new Object();

   /**
     * Stop scheduling thread events
     */
   final public static void stop() {
      synchronized(lock) {
         if (null != _scheduler) {
            _scheduler.interrupt();
         }
         _scheduler = null;
      }
   }

   /**
     * Start scheduling thread events
     */
   final public static void start(final long interval) {
      synchronized(lock) {
         stop();
         _scheduler = new Thread() {
            synchronized public void run() {
               final long duration = interval;
               try {
                  while(true) {
                     sleep(duration);
                  }
               } catch (InterruptedException e) {
                  //
               }

            }
         };
         _scheduler.setPriority(Thread.MAX_PRIORITY);
         _scheduler.setDaemon(true);
         _scheduler.setName("org.webmacro.util.ThreadScheduler");
         _scheduler.start();     
      }
   }

}
