
/*
 * Copyright (c) 1998, 1999 Semiotek Inc. All Rights Reserved.
 *
 * This software is the confidential intellectual property of
 * of Semiotek Inc.; it is copyrighted and licensed, not sold.
 * You may use it under the terms of the GNU General Public License,
 * version 2, as published by the Free Software Foundation. If you 
 * do not want to use the GPL, you may still use the software after
 * purchasing a proprietary developers license from Semiotek Inc.
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See the attached License.html file for details, or contact us
 * by e-mail at info@semiotek.com to get a copy.
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
