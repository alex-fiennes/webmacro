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


package org.webmacro.util;

import java.io.*;
import java.util.*;
import java.text.MessageFormat;

/**
 * Abstract base class which implements most of the LogTarget interface,
 * to make it easier to write new log targets that plug into WM.  
 */

abstract public class AbstractLogFile implements LogTarget {

   protected Map _levels = new HashMap();
   protected boolean _trace = false;
   protected int _defaultLevel = LogSystem.NOTICE;
   protected String _defaultFormatString = "{0,time}\t{1}\t{2}\t{3}";
   protected String _formatString = _defaultFormatString;
   protected MessageFormat _mf;

   protected List _observers = new LinkedList();
   protected String _name;


   /**
     * Create a new LogFile instance reading properties from the 
     * supplied Settings object: <pre>
     *     LogTraceExceptions: true|false|yes|no|on|off
     *     LogLevel:
     */
   public AbstractLogFile(Settings s) {
      _trace = s.getBooleanSetting("LogTraceExceptions");
      String slevel = s.getSetting("LogLevel", "NOTICE");
      _defaultLevel = LogSystem.getLevel(slevel);
      String format = s.getSetting("LogFormat");
      if (format != null) 
         _formatString = format;
      _mf = new MessageFormat(_formatString);
      Settings levels = new SubSettings(s, "LogLevel");
      String[] keys = levels.getKeys();
      for (int i = 0; i < keys.length; i++) 
         _levels.put(keys[i], new Integer(LogSystem.getLevel(
                                levels.getSetting(keys[i]))));
   }


   public AbstractLogFile() {
      _mf = new MessageFormat(_formatString);
   }


   public String toString() {
      return "LogFile(name=" + _name + ", level=" + _defaultLevel + ", trace=" + _trace + ")"; 
   }

   /**
     * Set the log level for this Logfile. The default is LogSystem.NOTICE
     */
   public void setLogLevel(int level) {
      _defaultLevel = level;
      Iterator i = _observers.iterator();
      while (i.hasNext()) {
         LogSystem ls = (LogSystem) i.next();
         ls.update(this,null);
      }
   }

   /**
     * Set the log level for a specific category name. 
     */
   public void setLogLevel(String name, int level) {
      _levels.put(name, new Integer(level));   
      Iterator i = _observers.iterator();
      while (i.hasNext()) {
         LogSystem ls = (LogSystem) i.next();
         ls.update(this,name);
      }
   }

   /**
     * Set whether this LogFile traces exceptions. The 
     * default is false.
     */
   public void setTraceExceptions(boolean trace) {
      _trace = trace;
   }

   public boolean subscribe(String category, String name, int level) {
      Integer ilevel = (Integer) _levels.get(name);
      boolean sub;
      if (ilevel != null) {
         sub = (ilevel.intValue() <= level);
      } else {
         sub =(_defaultLevel <= level);
      }
      return sub;
   }


   public void addObserver(LogSystem ls) {
      _observers.add(ls);
   }

   public void removeObserver(LogSystem ls) {
      _observers.remove(ls);
   }

}


