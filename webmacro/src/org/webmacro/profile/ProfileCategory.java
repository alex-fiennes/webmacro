
package org.webmacro.profile;

/**
  * A ProfileCategory manages Profile objects for a category. 
  */
public interface ProfileCategory {

   /**
     * Instantiate a new Profile. If a null object is returned then 
     * no profiling is to be done. The returned object will be the 
     * root of a Profile stack trace. Call its start() and stop() 
     * methods to record timing data. 
     * <P>
     * Concurrency: this method is thread-safe. You may call it from
     * multiple threads. 
     */
   public Profile newProfile();


   public Profile[] getProfiles();

   public String getName();

}


