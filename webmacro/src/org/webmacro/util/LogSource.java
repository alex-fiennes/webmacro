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

import org.webmacro.util.LogTarget;
import org.webmacro.Log;

/**
  * A log is a simple recipient of information about the system. It 
  * does not display or render this information, but instead passes
  * it back to a set of log targets. A log is administered by a 
  * LogManager so you cannot instantiate one directly. Instead 
  * you must ask the Logmanager or a log instance.
  */
public class LogSource implements Log {

   final private String _type;
   final private String _description;
   final private String _category;

   /**
     * The number of targets currently registered with this 
     * LogSource. 
     */
   private int _tCount = 0; // count number of targets

   final public boolean hasTargets() {
      return (_tCount != 0);
   }

   final private LogTarget[][] _targets = new LogTarget[LogSystem.NONE][];

   /**
     * A LogSource must have a category and a type: called by Logsystem
     */
   LogSource(String category, String type, String description) 
   {
      _category = category;
      _type = type;
      _description = description;
   }

   /**
     * The type of this log source, as it would print in the log. 
     * For example "sys", or "log", or "wm". This type should be
     * fairly short as it may be printed on every log line.
     */
   public String getType() { return _type; }

   /**
     * Get a description of this log source: what kind of messages
     * does it contain? This can be a sentence or so of information
     * about what kind of messages this log represents.
     */
   public String getDescription() { return _description; }

   /**
     * The category for this log source, as it woudl print in the 
     * log. This is the name which was passed to LogSystem to get 
     * the LogSystem instance from which this Log was created.
     */
   public String getCategory() { return _category; }

   /**
     * Explain myself
     */
   public String toString() {
      return "LogSource(" + _category + "," + _type + "," + _description + ")";
   }

   /**
     * Called by LogSystem to add a target
     */
   void addTarget(LogTarget t, int level) {

      if ((level < LogSystem.ALL) || (level > LogSystem.NONE)) {
         error("Attempt to target with invalid log level: " + level);   
         return;
      }

      LogTarget[] ts = _targets[level];
      if (ts == null) { // we're the first
         ts = new LogTarget[1];
         ts[0] = t;
         _targets[level] = ts;
         _tCount++;
         return;
      }

      // already got it?
      for (int i = 0; i < ts.length; i++) {
         if (ts[i] == t) { 
            return; 
          }
      }

      // add it
      LogTarget[] nts = new LogTarget[ts.length + 1];
      System.arraycopy(ts,0,nts,0,ts.length);
      nts[ts.length] = t;
      _targets[level] = nts; 
      _tCount++;
   }

   /**
     * Remove a target from a specific list
     */
   void removeTarget(LogTarget t, int level) {
      LogTarget[] ts = _targets[level];
      if (ts == null) { return; }
      boolean match = false;
      for (int i = 0; i < ts.length; i++) {
         if (ts[i] == t) {
            match = true;
         }
      }
      if (!match) {
         return;
      }
      _tCount--;
      if (ts.length == 1) {
         _targets[level] = null;
      }

      LogTarget[] nts = new LogTarget[ts.length - 1];
      int pos = 0;
      for (int i = 0; i < ts.length; i++) {
         if (ts[i] != t) {
            nts[pos++] = ts[i];
         }
      }
      _targets[level] = nts;
   }

   /**
     * Debug messages are incidental programmer notes which should 
     * not be enabled in a production system. They are useful only
     * during development.
     */
   public void debug(String msg, Exception e) {
      log(LogSystem.DEBUG,msg,e);
   }

   /**
     * A shortform for debug(msg,null)
     */
   public void debug(String msg) {
      log(LogSystem.DEBUG,msg,null);
   }

   /**
     * Info is fairly unimportant information about routine processing
     * within the system. They may be interesting on a production 
     * system, but also can typically be ignored.
     */
   public void info(String msg) {
      log(LogSystem.INFO,msg,null);
   }

   /**
     * Notices are important information about routine processing
     * within the system. For example, startup and shutdown messages.
     * They are likely interesting to people running a production 
     * system since they provide timestamps for important events.
     */
   public void notice(String msg) {
      log(LogSystem.NOTICE,msg,null);
   }

   /**
     * Warnings are messages outlining unexpected non-routine events
     * within the system. They may indicate larger problems, but in
     * and of themselves refer to problems the system is capable of
     * handling on its own. On a correctly functioning production 
     * system you would expect to see only a few warnings.
     */
   public void warning(String msg, Exception e) {
      log(LogSystem.WARNING,msg,e);
   }

   /**
     * A shortform for warning(msg,null)
     */
   public void warning(String msg) {
      log(LogSystem.WARNING,msg,null);
   }

   /**
     * A shortform for error(msg,null)
     */
   public void error(String msg) {
      log(LogSystem.ERROR,msg,null);
   }

   /**
     * An error is a major failure within the system. Typically it is 
     * something which cannot easily be handled by the system. On a 
     * correctly functioning production system you would not expect
     * to see any error messages.
     */
   public void error(String msg, Exception e) {
      log(LogSystem.ERROR,msg,e);
   }


   public boolean loggingDebug() { 
      return (_targets[LogSystem.DEBUG] != null);
   }

   public boolean loggingInfo() { 
      return (_targets[LogSystem.INFO] != null);
   }

   public boolean loggingNotice() { 
      return (_targets[LogSystem.NOTICE] != null);
   }

   public boolean loggingWarning() { 
      return (_targets[LogSystem.WARNING] != null);
   }

   protected void log(int level, String msg, Exception e) {
      LogTarget[] targets = _targets[level];
      if (targets == null) { return; }
      String sLevel = LogSystem.LEVELS[level];
      java.util.Date date = Clock.getDate();
      boolean flush = (level >= LogSystem.NOTICE);
      for (int i = 0; i < targets.length; i++) {
         targets[i].log(date,_type,sLevel,msg,e);
         if (flush) targets[i].flush();
      }
   }
}


