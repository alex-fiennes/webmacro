
package org.webmacro.util;

import org.webmacro.Log;

/**
  * A log is a simple recipient of information about the system. It 
  * does not display or render this information, but instead passes
  * it back to a set of log targets. A log is administered by a 
  * LogManager so you cannot instantiate one directly. Instead 
  * you must ask the Logmanager or a log instance.
  */
public class LogSource implements Log {

   final private String _name;

   final static private String[] _levels = 
      { "DEBUG", "INFO", "NOTICE", "WARNING", "ERROR" };

   /**
     * Get an integer corresponding to a string level name. The
     * level names supported are: ALL, DEBUG, INFO, NOTICE, 
     * WARNING, ERROR, and NONE.
     */
   final static public int getLevel(String level) {
      if (level == null) {
         return 0;
      }
      for (int i = 0; i < _levels.length; i++) {
         if (_levels[i].equalsIgnoreCase(level)) {
            return i;
         }
      }
      if ("NONE".equalsIgnoreCase(level)) {
         return _levels.length;
      }  else {
         return 0;
      }
   }

   final static private int DEBUG = 0;
   final static private int INFO = 1;
   final static private int NOTICE = 2;
   final static private int WARNING = 3;
   final static private int ERROR = 4;

   final static private int ALL = DEBUG;
   final static private int NONE = ERROR;
   
   final private LogTarget[][] _targets = new LogTarget[ERROR + 1][];

   protected LogSource(String name) {
      _name = name;
   }

   public String getName() { return _name; }

   /**
     * Add a new LogTarget to receive messages at or above the specified 
     * log level. The level can be set to 'null' to indicate NONE.
     */
   public void addTarget(LogTarget t, String level) {
      if (level == null) {
         return;
      }
      int ilevel = getLevel(level);

      for (int i = ilevel; i < NONE; i++) {
         addTarget(t,i);
      }
   }

   private void addTarget(LogTarget t, int level) {
      LogTarget[] ts = _targets[level];
      if (ts == null) { // we're the first
         ts = new LogTarget[1];
         ts[0] = t;
         _targets[level] = ts;
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
   }

   /**
     * Remove a target from all lists
     */
   protected void removeTarget(LogTarget t) {
      for (int level = 0; level < _targets.length; level++) {
         removeTarget(t,level);
      }
   }

   /**
     * Remove a target from a specific list
     */
   protected void removeTarget(LogTarget t, int level) {
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
      log(DEBUG,msg,e);
   }

   /**
     * A shortform for debug(msg,null)
     */
   public void debug(String msg) {
      log(DEBUG,msg,null);
   }

   /**
     * Info is fairly unimportant information about routine processing
     * within the system. They may be interesting on a production 
     * system, but also can typically be ignored.
     */
   public void info(String msg) {
      log(INFO,msg,null);
   }

   /**
     * Notices are important information about routine processing
     * within the system. For example, startup and shutdown messages.
     * They are likely interesting to people running a production 
     * system since they provide timestamps for important events.
     */
   public void notice(String msg) {
      log(NOTICE,msg,null);
   }

   /**
     * Warnings are messages outlining unexpected non-routine events
     * within the system. They may indicate larger problems, but in
     * and of themselves refer to problems the system is capable of
     * handling on its own. On a correctly functioning production 
     * system you would expect to see only a few warnings.
     */
   public void warning(String msg, Exception e) {
      log(WARNING,msg,e);
   }

   /**
     * A shortform for debug(msg,null)
     */
   public void warning(String msg) {
      log(WARNING,msg,null);
   }

   /**
     * A shortform for debug(msg,null)
     */
   public void error(String msg) {
      log(ERROR,msg,null);
   }

   /**
     * An error is a major failure within the system. Typically it is 
     * something which cannot easily be handled by the system. On a 
     * correctly functioning production system you would not expect
     * to see any error messages.
     */
   public void error(String msg, Exception e) {
      log(ERROR,msg,e);
   }


   protected void log(int level, String msg, Exception e) {
      LogTarget[] targets = _targets[level];
      if (targets == null) { return; }
      String sLevel = _levels[level];
      for (int i = 0; i < targets.length; i++) {
         targets[i].log(_name,sLevel,msg,e);
      }
   }
}


