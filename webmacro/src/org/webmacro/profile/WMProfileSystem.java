
package org.webmacro.profile;

import java.util.*;

public class WMProfileSystem {

   private final LinkedList _children = new LinkedList();

   private int _recordTime = 60000;
   private int _samplingRate = 0;

   /**
     * Return a ProfileCategory for the category 'name'. If a null
     * is returned from this method then no profiling is being 
     * done for the supplied name. 
     */
   ProfileCategory getProfileCategory(String name) {
      WMProfileCategory child = 
         new WMProfileCategory(_recordTime,_samplingRate);
      _children.add(child);
      return child;
   }

   public ProfileCategory[] getCategories() {
      return (ProfileCategory[]) _children.toArray(new ProfileCategory[0]);
   }

   /**
     * Initialize a Profile system. The supplied properties object
     * will contain entries that control the behavior of the 
     * profiler. In addition to any implementation extensions, 
     * the following properties must be supported:
     * <ul>
     * Profile.rate: 0, 1, ..., N
     * Profile.time: msec
     * </ul>
     * The "rate" controls the sampling rate. One out of 
     * every N requests will be profiled. Setting the rate to 0 shuts
     * profiling off altogether. Setting it to 1 samples every request.
     * Setting it to 5, for example, would profile every 5th request.
     * This determines the CPU impact of the profiling system.
     * <p>
     * The "time" controls how long data is kept for, and it is 
     * measured in milliseconds. For example, setting it to 60000
     * keeps profiling data for the last one minute. This determines
     * how much memory is consumed by the profiling system.
     * <p>
     * In addition to the above global settings a Profiler may support
     * category specific settings:
     * <P>
     * Profile.NAME.rate: 0, 1, ... N
     * </ul>
     * Where "NAME" is the name of a category. For example 
     * <ul>
     * Profile.io.rate: 5
     * </ul>
     * would enable profiling for the "io" category with a sampling
     * rate of 5. These category names match the name passed to 
     * getProfileCategory(String). 
     */
   public void init(java.util.Properties props) {
      String rate = props.getProperty("Profile.rate", "0"); 
      String time = props.getProperty("Profile.time", "60"); 
      int samplingRate = 0;
      int recordTime = 60;
      try { samplingRate = Integer.valueOf(rate).intValue(); } 
      catch (Exception e) { }
      try { recordTime = Integer.valueOf(time).intValue(); } 
      catch (Exception e) { }
      _samplingRate = samplingRate;
      _recordTime = recordTime;
   }

   /**
     * Shut down the profiling system. This method allows a profiling
     * system to save its settings to a file or database. It will be 
     * called by the application when the application is about to 
     * terminate the module containing the profiling system.
     */
   public void destroy() {
      _children.clear();
   }

}

