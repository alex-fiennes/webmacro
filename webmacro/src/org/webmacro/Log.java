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

  /**
   * Ask the log system if it wants these kinds of log messages.
   * This is because the overhead of creating log messages is high,
   * even if we're not going to log them, because it usually involves
   * several string concatenations.  */

   public boolean loggingDebug();
   public boolean loggingInfo();
   public boolean loggingNotice();
   public boolean loggingWarning();
}


