
/*
 * Copyright (c) 1998, 1999 Semiotek Inc. All Rights Reserved.
 *
 * This software is the confidential intellectual property of
 * of Semiotek Inc.; it is copyrighted and licensed, not sold.
 * You may use it under the terms of the GNU General Public License,
 * version 2, as published by the Free Software Foundation. If you 
 * do not want to use the GPL, you may still use the software after
 * purchasing a proprietary developers license from Semiotek Inc.
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See the attached License.html file for details, or contact us
 * by e-mail at info@semiotek.com to get a copy.
 */


package org.webmacro.util;

import java.io.*;
import java.util.*;
import java.text.*;

/**
  * The Semiotek Log interface is intended to make logging as simple as 
  * possible in order to encourage its use throughout the system. Log
  * messages are categorized two ways : what type/level they are, 
  * and what part of the system they come from. The type of the log 
  * level ranges from "error" (most severe) to "debug" (least severe). 
  * The part of the system is represented by the "class" of the log 
  * message--this is an arbitrary string.
  * <P>
  * The Log class is divided into static methods which are used to control
  * the overall behavior of the logging system, and instance methods which
  * are used to submit log messages to the log.
  * <P>
  * The expected way to use this class is to create a Log object in a 
  * package called "log" and then call it from various points in that 
  * package to generate log messages. The "log" object should not be 
  * visible beyond the scope of its intended callers.
  *
  */
final public class Log
{

   // static portion relates to overall control & log behavior


   // STATIC VARIABLES


   /**
     * Use this variable in your code to decide whether or not to include 
     * debug() log messages. A good technique is to use a local variable in
     * your class like this:
     *<blockquote>
     *    final boolean debug = false && Log.debug
     *</blockquote>
     * @see #debug()
     */
   public static final boolean debug = true;

   /**
     * All the current log object names and types
     */
   final private static Vector logsRegistered = new Vector();

   /**
     * The log types that are selected for output. null means all.
     */
   private static Hashtable typesEnabled = null;

   /**
     * setLevel(Log.NONE) turns off all log messages
     */
   public static final Constant NONE     = new Constant("NONE",0);

   /**
     * use with setLevel() to turn off all log messages except ERROR
     * @see #error()
     */
   public static final Constant ERROR     = new Constant("ERROR",1);

   /**
     * use with setLevel() to turn off all log messages except ERROR/WARNING
     * @see #warning()
     */
   public static final Constant WARNING   = new Constant("WARNING",2);

   /**
     * use with setLevel() to turn off messages other than ERROR/WARNING/INFO
     * @see #info()
     */
   public static final Constant INFO      = new Constant("INFO",3);

   /**
     * use with setLevel() to turn off all log messages except 
     * ERROR/WARNING/INFO/EXCEPTION
     * @see #exception()
     */
   public static final Constant EXCEPTION = new Constant("EXCEPTION",4);

   /**
     * use with setLevel() to turn off all log messages except 
     * ERROR/WARNING/INFO/EXCEPTION/DEBUG
     * @see #debug()
     */
   public static final Constant DEBUG     = new Constant("DEBUG",5);

   /**
     * use with setLevel() to turn on all log messages
     */
   public static final Constant ALL       = new Constant("ALL",6);

   /**
     * Used to flag which types are enabled
     */
   private static final String TYPE_ENABLED = "ENABLED";

   /**
     * system independent line separator
     */
   private final static String NEWLINE = System.getProperty("line.separator");

   /**
     * Current level of log messages being printed, defaults to INFO
     */
   static private int myLevel = INFO.getOrder();

   /**
     * Current target for the log
     */
   static private PrintWriter myTarget = new PrintWriter(System.out,true);

   /**
     * Whether or not we should be stack tracing exceptions
     */
   static private boolean iTraceExceptions = false;

   /**
     * How timestamps in the log are written out
     */
   static private DateFormat dateFmt 
      = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT);


   // STATIC METHODS


  /**
    * Get the log constant that corresponds to the supplied integer. If 
    * the integer is out of range the closest constant will be returned.
    */
   static final public Constant getConstant(int i) {
      Constant ret;
      switch(i) {
         case 0: ret = NONE; break;
         case 1: ret = ERROR; break;
         case 2: ret = WARNING; break;
         case 3: ret = INFO; break;
         case 4: ret = EXCEPTION; break;
         case 5: ret = DEBUG; break;
         case 6: ret = ALL; break;
         default: 
                 if (i < 0) {
                    ret = NONE;
                 } else {
                    ret = ALL;
                 } 
                 break;
      }
      return ret;
   }

   /**
     * Find the constant that matches the supplied string by name. Case 
     * is ignored. If the supplied name does not match any constants, then
     * the constant ALL is returned.
     */
   static final public Constant getConstant(String name) {
      Constant ret;
      if (name.equalsIgnoreCase(NONE.getName())) { ret = NONE; } 
      else if (name.equalsIgnoreCase(ERROR.getName())) { ret = ERROR; }
      else if (name.equalsIgnoreCase(WARNING.getName())) { ret = WARNING; }
      else if (name.equalsIgnoreCase(INFO.getName())) { ret = INFO; }
      else if (name.equalsIgnoreCase(EXCEPTION.getName())) { ret = EXCEPTION; }
      else if (name.equalsIgnoreCase(DEBUG.getName())) { ret = DEBUG; }
      else { ret = ALL; }

      return ret;
   }

   /**
     * Log level: what messages to we print out?
     * @param logLevelConstant use one of the log constants defined above
     */
   static final public void setLevel(Constant logLevel) {
      myLevel = logLevel.getOrder();
      myTarget.println("*** LOG LEVEL SET TO: " + logLevel);
   }

   /**
     * Log target: where do we write log messages?
     * @param target the PrintWriter you want to send log messages to
     */
   static final public void setTarget(PrintWriter target) { 
      if (target == null) {
         target = new PrintWriter(new OutputStreamWriter(System.err));
      }
      myTarget = target;
      myTarget.println("*** BEGIN: " + dateFmt.format(new Date()) + "***");
   }


   /**
     * Log target: where do we write log messages?
     * @param target the OutputStream you want to send log messages to
     */
   static final public void setTarget(OutputStream target) { 
      setTarget( (target == null) ?
         null : new PrintWriter(new OutputStreamWriter(target)));
   }

   /**
     * Log target: set the log to be this file
     * @param logfile the name of a file you want to log messages to
     * @throws IOException if the file could not be opened
     */
   static final public void setTarget(String logfile) throws IOException
   {
      PrintWriter out;
      if (logfile != null) {
         out = new PrintWriter(new FileWriter(logfile,true));
      } else {
         out = new PrintWriter(new OutputStreamWriter(System.err));
      }
      setTarget(out);
      out.flush();
   }

   /**
     * Log includes exception stacktraces
     * @param trace true if you want exception stacktraces, false otherwise
     */
   static final public void traceExceptions(boolean trace) {
      iTraceExceptions = trace;
   }


   /**
     * Private utility method for writing the log (do not add newline)
     */
   static final private void write(String level, String type, Object message)
   {

      if ((typesEnabled != null) && typesEnabled.get(type) != TYPE_ENABLED) {
         return;
      }

      try {
         myTarget.print(dateFmt.format(new Date())
                         + "\t" + type 
                         + "\t" + level 
                         + "\t" + message); 
         myTarget.flush();
      } catch (java.lang.Exception e) {
         System.err.println("** COULD NOT WRITE LOG! SWITCHING TO STDERR **");
         System.err.println(dateFmt.format(new Date()) 
               + "\t" + level + "\t" + message);
         System.err.flush();
         setTarget(new PrintWriter(System.err));
      }
   }

   /**
     * Private utility method for writing the log (add newline)
     */
   static final private void writeln(String level, String type, Object message)
   {
      write(level,type,message + NEWLINE);
   }


   /**
     * Return an enumeration containing a list of the registered log types
     */
   static final public Enumeration getTypes() {
      return logsRegistered.elements();
   }

   /**
     * Return an enumeration of the logs types that are currently enabled.
     */
   static final public Enumeration getTypesEnabled() {
      if (typesEnabled == null) {
         return logsRegistered.elements();
      } else {
         return typesEnabled.keys();
      }
   }

   /**
     * Allow printing of only the supplied log types
     */
   static final public void enableTypes(String[] types) 
   {
      disableAllTypes();
      for (int i = 0; i < types.length; i++) {
         enableType(types[i]);
      }
   }

   /**
     * Allow printing of the supplied type. If all types are currently
     * allowed (via enableAllTypes) then this does nothing; if all types
     * are currently disallowed (via disableAllTypes) then this re-enables
     * just this one type. You can call it repeatedly to re-enable 
     * several different types.
     */
   static public final void enableType(String type) {
      Hashtable types = typesEnabled;
      if (typesEnabled == null) {
         return; // all enabled already
      } else {
         types.put(type,TYPE_ENABLED);
      }
   }

   /**
     * Allow printing of all types. 
     */
   static public final void enableAllTypes() {
      typesEnabled = null;
   }

   /**
     * Disallow printing of all types. You probably want to call 
     * enableType() a few times after this.
     */
   static public final void disableAllTypes() {
      typesEnabled = new Hashtable();
   }



   // INSTANCE VARIABLES


   /**
     * The type name for this log type, should be fairly short
     */
   private String logType;

   /**
     * A description of what this log type is for
     */
   private String logDescr;


   // INSTANCE METHODS


   /**
     * Create a new log in the specified class. It is recomended that
     * the type paramter be a word of seven or fewer characters in order
     * to produce a consistent looking log.
     * <P>
     * @param type a single word representing package/category 
     * @description a brief explanation of what this category means
     */
   public Log(String type, String description)
   {
      this.logType = type.intern(); // allow "==" tests for equality
      this.logDescr = description;
      logsRegistered.addElement(this);
   }

   /**
     * Use to write a log message that indicates a programming error
     * or a system level misconfiguration: for example, unable to 
     * locate the config file, etc. Error should not be used
     * to indicate an error in data being processed, but rather an 
     * error condition that is the fault of the program itself.
     */
   final  public void error(Object logMessage) {
      if (myLevel >= ERROR.getOrder()) { 
         writeln("ERROR", logType, logMessage);
      }
   }

   /**
     * Use to write a log message that indicats suspicious but 
     * non-fatal behavior in the program; or else which represents a 
     * fatal error that is the fault of data passed to the program. 
     */
   final  public void warning(Object logMessage) {
      if (myLevel >= WARNING.getOrder()) { 
         writeln("WARN", logType, logMessage);
      }
   }

   /**
     * Write a log message that simply informs of some interesting 
     * events, such as program start up or shut down. 
     */
   final  public void info(Object logMessage) {
      if (myLevel >= INFO.getOrder()) { 
         writeln("INFO", logType, logMessage);
      }
   }

   /**
     * Write a log message that indicates an exceptional condition 
     * has occurred. It is normal to pass an actual exception in as 
     * the object to be logged--if the object passed is actually an 
     * exception and stack tracing is enabled, this message will then
     * be able to generate the appropraite stack trace for the Log.
     */
   final public void exception(Object logMessage) {
      if (myLevel < EXCEPTION.getOrder()) {
         return;
      } 

      if (iTraceExceptions && logMessage instanceof Exception) {
         Exception e = (Exception) logMessage;
         write("EXCPT", logType, "");
         e.printStackTrace(myTarget); 
         myTarget.flush();
      }  else {
         writeln("EXCPT", logType, logMessage);
      }
   }

   /**
     * Use to write debugging information to the log. This is information
     * that would normally only be of interest to someone trying to 
     * discover the actual behavior of the program at runtime. 
     * <p>
     * It is normal to wrap a Log.debug() like this:
     * <blockquote><pre>
     * if (Log.debug) { log.debug("debug msg"); } 
     * </pre></blockquote>
     * The "debug" boolean in the Log class can then be toggled on or 
     * off in the source, thereby allowing debug statements to be optimized
     * out of the source code at compile time. 
     */
   final  public void debug(Object logMessage) {
      if (myLevel >= DEBUG.getOrder()) { 
         writeln("DEBUG", logType, logMessage);
      }
   }


   // TEST HARNESS


   /**
     * Test harness: opens "log" in the current directory
     */
   public static void main(String arg[]) {

      try {
         System.out.println("Logging to \"log\" in the current directory.");
         Log.setTarget("log"); 
         Log log = new Log("testing", "just used for testing");
         for (int i = NONE.getOrder(); i <= ALL.getOrder(); i++) {
            log.setLevel(getConstant(i));
            log.error("Testing error");
            log.warning("This is a warning");
            log.info("This is informative.");
            log.exception("An exception");
            log.debug("Debug junk");
         }
      } catch (java.lang.Exception e) {
         e.printStackTrace();
      }


   }

}

