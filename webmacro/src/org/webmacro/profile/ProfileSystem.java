package org.webmacro.profile;

public interface ProfileSystem {

   /**
     * Return a ProfileCategory for the category 'name'. If a null
     * is returned from this method then no profiling is being 
     * done for the supplied name. 
     * <p>
     * @param name the name the category reports as in statistics
     * @param rate one of every rate events will be sampled, 0 disables
     * @param time how many milliseconds profiling information is stored
     */
   ProfileCategory newProfileCategory(String name, int rate, int time);

   /**
     * Get all profile categories
     */
   ProfileCategory[] getProfileCategories();

   /**
     * Shut down the profiling system. This method allows a profiling
     * system to save its settings to a file or database. It will be 
     * called by the application when the application is about to 
     * terminate the module containing the profiling system.
     */
   public void destroy();

}

