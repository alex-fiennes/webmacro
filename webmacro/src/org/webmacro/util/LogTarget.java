
package org.webmacro.util;

/**
  * A log target is a destination for log messages. You can register 
  * these with the LogManager to set yourself up to receive a 
  * variety of messages. For most log users the LogFile implementation
  * of this interface will be adequate. 
  */
public interface LogTarget {

   /**
     * Log out an event that just happened and which you have previously
     * accepted. 
     */
   public void log(java.util.Date date, String type, String level, String message, Exception e);

   /**
     * Flush the log. This will be called after writing methods that
     * are notice, warning, or error messages.
     */
   public void flush();

   /**
     * Return true or false if this log target would like to receive 
     * log messages for the named category, type, and logLevel. This 
     * method must return the same value every time it is called with
     * the same arguments. 
     * <p>
     * The logLevel you will be called with is one of the integers 
     * Log.ALL, Log.DEBUG, Log.INFO, Log.NOTICE, Log.WARNING, 
     * Log.ERROR, and Log.NONE in ascending order (Log.ERROR is a higher
     * number than Log.WARNING which is a higher number than Log.DEBUG).
     * In other words, the higher the logLevel the more important the 
     * log message is.
     */
   public boolean subscribe(String category, String type, int logLevel);

   /**
     * A LogSystem will register itself though this method in order to 
     * detect changes to the LogTarget. LogTargets should notify all 
     * observers when any setting changes that might affect the 
     * return value of the subscribe(...) method.
     */
   public void addObserver(LogSystem ls);

   /**
     * A LogSystem may remove itself through this method if it 
     * de-registeres the LogTarget. After this method the supplied
     * observer should no longer receive notification of updates.
     */
   public void removeObserver(LogSystem ls);

}


