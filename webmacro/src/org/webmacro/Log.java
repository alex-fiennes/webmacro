
package org.webmacro;

/**
  * A log is a simple recipient of information about the system. It 
  * does not display or render this information, but instead passes
  * it back to a set of log targets. A log is administered by a 
  * LogManager so you cannot instantiate one directly. Instead 
  * you must ask the Logmanager or a log instance.
  */
public interface Log {

   /**
     * Debug messages are incidental programmer notes which should 
     * not be enabled in a production system. They are useful only
     * during development.
     */
   public void debug(String msg, Exception e);

   /**
     * A shortform for debug(msg,null)
     */
   public void debug(String msg); 

   /**
     * Info is fairly unimportant information about routine processing
     * within the system. They may be interesting on a production 
     * system, but also can typically be ignored.
     */
   public void info(String msg);

   /**
     * Notices are important information about routine processing
     * within the system. For example, startup and shutdown messages.
     * They are likely interesting to people running a production 
     * system since they provide timestamps for important events.
     */
   public void notice(String msg);

   /**
     * Warnings are messages outlining unexpected non-routine events
     * within the system. They may indicate larger problems, but in
     * and of themselves refer to problems the system is capable of
     * handling on its own. On a correctly functioning production 
     * system you would expect to see only a few warnings.
     */
   public void warning(String msg, Exception e);

   /**
     * A shortform for debug(msg,null)
     */
   public void warning(String msg);

   /**
     * A shortform for debug(msg,null)
     */
   public void error(String msg);

   /**
     * An error is a major failure within the system. Typically it is 
     * something which cannot easily be handled by the system. On a 
     * correctly functioning production system you would not expect
     * to see any error messages.
     */
   public void error(String msg, Exception e);

}


