
package org.webmacro.util;

import org.webmacro.Log;

/**
  * TimeLoop is a scheduler which can schedule Runnable objects 
  * for execution or repeate execution. A TimeLoop is configured
  * with two key parameters: periods and duration.
  * <p>
  * You can schedule a job to execute once, P periods from now, 
  * where P must be less than the number of periods in the TimeLoop.
  * <p>
  * You can also schedule a job to execute once every P periods, 
  * where P must be less than the number of periods in the timeloop.
  * TimeLoop will be somewhat inaccurate in this case unless the
  * number of periods in the TimeLoop is a multiple of P: otherwise
  * it cannot distribute the repeat job evenly over its time periods.
  */
public class TimeLoop extends Thread
{

   final long _duration;
   final int _periods;

   final Runnable[][] _tasks;
   final boolean[][] _repeats;
   final Object[] _locks;
   static final Log _syslog = LogManager.getSystemLog();

   /**
     * Create a new TimeLoop. 
     * @param duration how long is each period? (milliseconds)
     * @param periods how many periods are there in total?
     */
   public TimeLoop(long duration, int periods) {
      setName("TimeLoop(" + duration + "," + periods + ")");
      _duration = duration;
      _periods = periods;

      _tasks = new Runnable[periods][];
      _repeats = new boolean[periods][];
      _locks = new Object[periods];

      for (int i = 0; i < periods; i++) {
         _tasks[i] = new Runnable[3];
         _repeats[i] = new boolean[3];
         _locks[i] = new Object();
      }
   }

   public String toString() {
      return getName();
   }

   final private int now() {
      return (int) ((System.currentTimeMillis()/_duration) % _periods); 
   }

   /**
     * Do not call this method. Call start().
     */
   public void run() {
      int period = now();
      while( !isInterrupted()) {
         try {
            sleep(_duration);
         } catch (InterruptedException e) {
            continue;
         }
         int newPeriod = now();
         while (period != newPeriod) {
            if (++period >= _periods) period = 0;

            synchronized(_locks[period]) {
               Runnable tasks[] = _tasks[period];
               boolean repeats[] = _repeats[period];
               for (int i = 0; i < tasks.length; i++) {
                  if (tasks[i] != null) {
                     try {
                        tasks[i].run();
                     } catch (Exception e) {
                        _syslog.warning(this + ": task " + tasks[i] 
                              + " threw an exception", e);
                     } finally {
                        if (!repeats[i]) {
                           tasks[i] = null;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   /**
     * Schedule a task to run repeatedly, once every waitPeriods. 
     * For example, in a 10 period TimeLoop scheduleRepeat(r,2) 
     * invoked during period 0 would schedule the job to run in periods 
     * 0, 2, 4, 6, and 8. Note that in a 5 period TimeLoop calling 
     * scheduleRepeat(r,3) would cause the job to be scheduled only
     * at time period 3.
     */
   public void scheduleRepeat(Runnable task, int waitPeriods) {
      if (waitPeriods < 1) waitPeriods = 1;
      if (waitPeriods > _periods) waitPeriods = _periods;

      int now = now();
      for (int i = 0; i < _periods; i += waitPeriods) {
         schedule(task,now + i,true);
      }
   }

   /**
     * A convenience method which translates your milliseconds into
     * wait periods. The milliseconds specify wait duration. 
     */
   public void scheduleRepeatTime(Runnable task, long milliseconds) {
      scheduleRepeat(task, (int) (milliseconds/_duration));
   }

   /**
     * Schedule a job to run in the specified number of waitPeriods. 
     * Note that waitPeriods must be less than the total number of
     * periods available in the TimeLoop. For example, if you calld
     * schedule(r,5) at period 7 in a 10 period TimeLoop then r 
     * would be scheduled to run in period 2, which will be executed
     * (waitPeriods * duration) milliseconds from now.
     */
   public void schedule(Runnable task, int waitPeriods) {
      schedule(task,waitPeriods,false);
   }

   /**
     * A convenience method which translates your milliseconds into
     * wait periods. The milliseconds specify wait duration. 
     */
   public void scheduleTime(Runnable task, long milliseconds) {
      schedule(task, (int) (milliseconds/_duration));
   }

   /**
     * Private implementation of schedule: schedule a task once for 
     * a specific period (calculated from waitPeriods + now()) and
     * set whether it repeats in that specific period or not.
     */
   private void schedule(Runnable task, int waitPeriods, boolean repeat)
   {
      if (waitPeriods < 1) waitPeriods = 1;
      if (waitPeriods > _periods) waitPeriods = _periods;

      int period = (now() + waitPeriods) % _periods;

      synchronized(_locks[period]) {
         Runnable tasks[] = _tasks[period];
         boolean repeats[] = _repeats[period];

         for (int i = 0; i < tasks.length; i++) {
            if (tasks[i] == null) {
               tasks[i] = task;
               repeats[i] = repeat;
               return;
            }
         }

         // grow slow since we level out
         int size = 1 + (int) (tasks.length * 1.25); 

         Runnable ntasks[] = new Runnable[ size ];
         boolean[] nrepeats = new boolean[ size ];

         System.arraycopy(tasks,0,ntasks,0,tasks.length);
         System.arraycopy(repeats,0,nrepeats,0,repeats.length);
         ntasks[tasks.length] = task;
         nrepeats[repeats.length] = repeat;
         _tasks[period] = ntasks;
         _repeats[period] = nrepeats;
      }
   }

   /**
     * Test the TimeLoop
     */
   public static void main(String arg[]) {

      try {

         final TimeLoop t = new TimeLoop(1000,19);
         t.setDaemon(true);
         t.start();

         final Runnable r3 = new Runnable() {
            public void run() { 
               System.out.println(this); 
            }
            public String toString() { return "every 3 sec"; }
         };
      
         final Runnable r5 = new Runnable() {
            public void run() { 
               System.out.println(this); 
            }
            public String toString() { return "every 5 sec"; }
         };
      
         final Runnable r7 = new Runnable() {
            public void run() { 
               System.out.println(this); 
            }
            public String toString() { 
               return "every 7 sec"; 
            }
         };
   
         for (int i = 0; i < 100; i++) {
            final int n = i;
            Thread th = new Thread() {
               public void run() {
                  for(int i = 0; i < 5; i++) {
                     try { sleep( 1000 * (n % 5 ) ); }
                     catch (Exception e) { e.printStackTrace(); }
                     t.scheduleRepeat(r3, 3);
                     t.scheduleRepeat(r5, 5);
                     t.scheduleRepeat(r7, 7);
                  }
               }
            };
            th.setDaemon(true);
            th.start();
         }

         sleep(1000 * 120);

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

}

