package org.webmacro.profile;

public interface ProfileSystem {

   /**
     * Return a ProfileCategory for the category 'name'. If a null
     * is returned from this method then no profiling is being 
     * done for the supplied name. 
     */
   ProfileCategory getProfileCategory(String name);

   /**
     * Get all profile categories
     */
   ProfileCategory[] getProfileCategories();

   /**
     * Initialize a Profile system. The supplied properties object
     * will contain entries that control the behavior of the 
     * profiler. In addition to any implementation extensions, 
     * the following properties must be supported:
     * <ul>
     * Profile.enabled: true|false
     * Profile.rate: 0, 1, ..., N
     * </ul>
     * The "enabled" flag controls whether profiling is enabled or 
     * disabled. The "rate" controls the sampling rate. One out of 
     * every N requests will be profiled. Setting the rate to 0 shuts
     * profiling off altogether. Setting it to 1 samples every request.
     * Setting it to 5, for example, would profile every 5th request.
     * <p>
     * In addition to the above global settings a Profiler must support
     * category specific settings:
     * <P>
     * Profile.NAME.enabled: true|false
     * Profile.NAME.rate: 0, 1, ... N
     * </ul>
     * Where "NAME" is the name of a category. For example 
     * <ul>
     * Profile.io.enabled: true
     * </ul>
     * would enable profiling for the "io" category. These category
     * names match the name passed to getProfileCategory(String). 
     */
   public void init(java.util.Properties props);

   /**
     * Shut down the profiling system. This method allows a profiling
     * system to save its settings to a file or database. It will be 
     * called by the application when the application is about to 
     * terminate the module containing the profiling system.
     */
   public void destroy();

}

