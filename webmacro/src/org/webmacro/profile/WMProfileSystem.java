
package org.webmacro.profile;

import java.util.*;

final public class WMProfileSystem implements ProfileSystem
{

   final private static WMProfileSystem _instance = new WMProfileSystem();

   public static final WMProfileSystem getInstance() { 
      return _instance; 
   }  

   private LinkedList _children = new LinkedList();;

   /**
     * Return a ProfileCategory for the category 'name'. If a null
     * is returned from this method then no profiling is being 
     * done for the supplied name. 
     */
   public ProfileCategory newProfileCategory(String name,int rate, int time) {
      if (_children == null) return null;
      WMProfileCategory child = 
         new WMProfileCategory(name, rate, time);
      _children.add(child);
      return child;
   }

   /**
     * Return an array of the ProfileCategory objects that are 
     * being managed. This may return null if there is no 
     * profiling being done.
     */
   public ProfileCategory[] getProfileCategories() {
      if (_children == null) return null;
      return (ProfileCategory[]) _children.toArray(new ProfileCategory[0]);
   }

   /**
     * Shut down the profiling system. This method allows a profiling
     * system to save its settings to a file or database. It will be 
     * called by the application when the application is about to 
     * terminate the module containing the profiling system.
     */
   public void destroy() {
      _children = null;
   }

}

