/*
 * Copyright (C) 1998-2000 Semiotek Inc.  All Rights Reserved.  
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted under the terms of either of the following
 * Open Source licenses:
 *
 * The GNU General Public License, version 2, or any later version, as
 * published by the Free Software Foundation
 * (http://www.fsf.org/copyleft/gpl.html);
 *
 *  or 
 *
 * The Semiotek Public License (http://webmacro.org/LICENSE.)  
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See www.webmacro.org for more information on the WebMacro project.  
 */


package org.webmacro.profile;

import java.util.*;

public class ProfileSystem
{

   final private static ProfileSystem _instance = new ProfileSystem();

   public static final ProfileSystem getInstance() { 
      return _instance; 
   }  

   final private LinkedList _children = new LinkedList();

   /**
     * Return a ProfileCategory for the category 'name'. If a null
     * is returned from this method then no profiling is being 
     * done for the supplied name. 
     */
   public ProfileCategory newProfileCategory(String name,int rate, int time) {
      if (_children == null) return null;
      ProfileCategory child = new ProfileCategory(name, rate, time);
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
      _children.clear();
   }

}

