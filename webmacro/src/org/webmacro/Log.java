
package org.webmacro;

/**
  * A Log is a place which you can write informational messages to. 
  * Messages are categorized in two ways: A Log typically has a 
  * name associated with it, and each emssage also has a severity
  * level. Each Log object should be used to return messages on 
  * a particular topic. Within that Log object select the logging
  * method that best describes the severity of the event that 
  * just occurred. 
  * <p>
  * The order of importance of log messages (ie: how likely people
  * are to read them) is ERROR, WARNING, NOTICE, INFO, DEBUG. In
  * other words people are very likely to see ERROR messages and
  * not at all likely to see DEBUG message. 
  * <p>
  * You can attach an exception to an error, warning, or debug
  * statement. 
  */
public interface Log {


   /**
     * Return the name of this log. The Log's name is associated
     * with every message that you write to the log.
     */
   public String getName();

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


