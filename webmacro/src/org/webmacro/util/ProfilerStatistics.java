
package org.webmacro.util;

/**
  * ProfileStatistics are data points about some object which is 
  * being profiled using the Profiler class. 
  */
final public class ProfilerStatistics {

   final String name;
   long time = 0;
   long calls = 0;

   ProfilerStatistics(String name) {
      this.name = name;
   }

   public synchronized String getName() { return name; }
   public synchronized long getTime() { return time; }
   public synchronized long getCalls() { return calls; }
}

