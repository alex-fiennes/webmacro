/*
 * LogTargetFactory.java
 *
 * Created on August 21, 2001, 1:41 PM
 */

package org.webmacro.util;

import java.lang.reflect.Constructor;

import org.webmacro.Broker;
import org.webmacro.WebMacroException;

/**
 * The LogTargetFactory assists the Broker (and you, if you want) in creating
 * new LogTarget instances.<p>
 *
 * If your LogTarget needs configuration settings from WebMacro, create a
 * constructor with this signature:<pre>
 *
 *     public MyLogTarget (org.webmacro.util.Settings settings);
 *
 * </pre>
 *
 * If you don't need to configuration options, you should have a
 * null constructor.
 *
 * @author  e_ridge
 * @since 0.99
 */
public class LogTargetFactory {

   private static LogTargetFactory _instance = new LogTargetFactory();

   public static class LogCreationException extends WebMacroException {

      public LogCreationException(String message, Throwable throwable) {
         super(message, throwable);
      }
   }

   /** Creates new LogTargetFactory */
   private LogTargetFactory() {
   }

   /** return the only instance of this LogTargetFactory */
   public static final LogTargetFactory getInstance() {
      return _instance;
   }

   /**
    * Creates a new <code>org.webmacro.util.LogTarget</code>
    *
    * @param broker the Broker that is requesting to create the log.  The
    *        Broker is used to find the LogTarget class via the Broker's
    *        <code>.classForName()</code> method.
    * @param classname the fully-qualified classname of the LogTarget to create
    * @param settings WebMacro settings that will be passed off to the
    *        new LogTarget during its construction
    */
   public final LogTarget createLogTarget(Broker broker, String classname, Settings settings) throws LogCreationException {
      LogTarget lt = null;
      try {
         Class targetClass = broker.classForName(classname);
         Class[] args = new Class[]{Settings.class};
         try {
            // attempt to use the constructor that takes a Settings object
            Constructor constr = targetClass.getConstructor(args);
            lt = (LogTarget) constr.newInstance(new Object[]{settings});
         }
         catch (NoSuchMethodException nsme) {
            // otherwise, use the default constructor
            lt = (LogTarget) targetClass.newInstance();
         }
      }
      catch (Exception e) {
         throw new LogCreationException("Cannot create LogTarget "
                                        + classname, e);
      }

      return lt;
   }
}