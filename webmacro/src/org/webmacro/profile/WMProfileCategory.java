
package org.webmacro.profile;

import org.webmacro.util.SimpleStack;
import java.util.*;

/**
  * A ProfileCategory manages Profiler objects for a category. 
  */
public class WMProfileCategory implements ProfileCategory {

   private final SimpleStack _pool = new SimpleStack();

   private final LinkedList _profiles = new LinkedList();
   private int _sharedProfiles = 0;
  
   private long _timestamp = 0;
   private long _sharedTimestamp = 0;

   private int _recordTime;
   private final int _samplingRate;
   private int _sampleCount = 0;

   /**
     * Record time is how long we are to record profiles. After the
     * specified amount of record time we will discard old profiles.
     */
   protected WMProfileCategory(int recordTime, int samplingRate) {
      _recordTime = recordTime;
      _samplingRate = samplingRate;
   }

   /**
     * Instantiate a new Profiler. If a null object is returned then 
     * no profiling is to be done. The returned object will be the 
     * root of a Profile stack trace. Call its start() and stop() 
     * methods to record timing data. 
     * <P>
     * Concurrency: this method is thread-safe. You may call it from
     * multiple threads. 
     */
   synchronized public Profiler newProfiler() {

      if ((_samplingRate == 0) || (++_sampleCount < _samplingRate)) {
         return null;
      }
      _sampleCount = 0;

      WMProfiler p = (WMProfiler) _pool.pop();
      if (p == null) {
         p = new WMProfiler(this);
      }
      p.timestamp = System.currentTimeMillis();
      return p;
   }

   /**
     * Add the profiler to the record queue, and clean out any 
     * profilers that have been hanging around for too long.
     */
   synchronized protected void record(WMProfiler p) {
      _profiles.add(p);
      cleanup();
   }

   /**
     * Get the current Profiles
     */
   synchronized public Profiler[] getProfiles()
   {
      _sharedTimestamp = System.currentTimeMillis(); 
      cleanup();
      return (Profiler[]) _profiles.toArray(new Profiler[0]);
   }

   /**
     * Eliminate any profilers that are too old, recycling any 
     * that have not been shared with any other object.
     */
   private void cleanup() {
      if (_profiles.size() == 0) return;

      long cutoff = System.currentTimeMillis() - _recordTime;
      try {
         while (_timestamp < cutoff) {
            Object o = _profiles.removeFirst();
            if (_timestamp > _sharedTimestamp) _pool.push(o);
            WMProfiler wmp = (WMProfiler) _profiles.getFirst();
            _timestamp = wmp.timestamp;
         }
      } catch (NoSuchElementException e) {
         // we emptied the list, ignore it
      }
   }
}

