
package org.webmacro.profile;

import org.webmacro.util.SimpleStack;
import java.util.*;

/**
  * A ProfileCategory manages Profile objects for a category. 
  */
final public class WMProfileCategory implements ProfileCategory {

   private final SimpleStack _pool = new SimpleStack();

   private final LinkedList _profiles = new LinkedList();
   private int _sharedProfiles = 0;
  
   private long _timestamp = 0;
   private long _sharedTimestamp = 0;

   private int _recordTime;
   private final int _samplingRate;
   private int _sampleCount = 0;

   private final String _name;

   /**
     * Record time is how long we are to record profiles. After the
     * specified amount of record time we will discard old profiles.
     */
   protected WMProfileCategory(String name, int samplingRate, int recordTime) 
   {
      _name = name;
      _recordTime = recordTime;
      _samplingRate = samplingRate;
   }

   public String getName() { return _name; } 

   public String toString() { 
      return "WMProfileCategory(" 
         + _name + "," + _recordTime + "," + _samplingRate + ")";
   }


   /**
     * Instantiate a new Profile. If a null object is returned then 
     * no profiling is to be done. The returned object will be the 
     * root of a Profile stack trace. Call its start() and stop() 
     * methods to record timing data. 
     * <P>
     * Concurrency: this method is thread-safe. You may call it from
     * multiple threads. 
     */
   synchronized public Profile newProfile() {
      if ((_samplingRate == 0) || (++_sampleCount < _samplingRate)) {
         return null;
      }
      _sampleCount = 0;

      WMProfile p = (WMProfile) _pool.pop();
      if (p == null) {
         p = new WMProfile(this);
      }
      p.timestamp = System.currentTimeMillis();
      return p;
   }

   /**
     * Add the profiler to the record queue, and clean out any 
     * profilers that have been hanging around for too long.
     */
   synchronized protected void record(WMProfile p) {
      _profiles.add(p);
      cleanup();
   }

   /**
     * Get the current Profiles
     */
   synchronized public Profile[] getProfiles()
   {
      _sharedTimestamp = System.currentTimeMillis(); 
      cleanup();
      return (Profile[]) _profiles.toArray(new Profile[0]);
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
            WMProfile wmp = (WMProfile) _profiles.getFirst();
            _timestamp = wmp.timestamp;
            if (_timestamp < cutoff) {
               _profiles.removeFirst();
               if (_timestamp > _sharedTimestamp) _pool.push(wmp);
            }
         }
      } catch (NoSuchElementException e) {
         // we emptied the list, ignore it
      }
   }
}

