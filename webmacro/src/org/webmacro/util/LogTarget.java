
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
   public void log(String name, String level, String message, Exception e);

   /**
     * Register a new Log object. Typically a LogTarget will look at 
     * l.getType() and then decide to call l.addTarget(this,N) several
     * times for suitable values of N--in order to set itself up to
     * receive messages.
     */
   public void attach(LogSource l);

   /**
     * Get the name of this log, eg: what file it writes to
     */
   public String getName();

}


