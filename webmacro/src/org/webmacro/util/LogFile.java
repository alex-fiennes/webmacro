
package org.webmacro.util;

import java.text.MessageFormat;
import java.util.Properties;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.text.MessageFormat;
import java.util.Properties;
import java.io.IOException;

public class LogFile implements LogTarget {

   Properties _levels;
   boolean _trace;
   PrintStream _out;
   MessageFormat _mf; 

   /**
     * Create a new LogFile instance. The LogFile can be configured 
     * to accept different kinds of log messages, and to print them
     * out in different ways. 
     * <p>
     * The levels Properties maps a log name to a log level string
     * describing what level of logging is desired for that target. 
     * You can use the special name * to refer to all targets. The 
     * levels correspond to severity levels: ALL, DEBUG, INFO, 
     * NOTICE, WARNING, ERROR, and NONE.
     * <p>
     * @param fileName where the log should write, null means System.err
     * @param levels a hashtable mapping log names to levels
     * @param trace true if this log should trace out exceptions
     * @param flush true if this log should flush after every message
     */
   public LogFile(String file, String format, Properties levels, boolean trace) 
      throws IOException
   {
      this(((file != null) ? 
           new PrintStream(new FileOutputStream(file,true)) : System.err),
           format, levels, trace);
   }

   public 
   LogFile(PrintStream out, String format, Properties levels, boolean trace)
   {
      _mf = new MessageFormat(format);
      _levels = levels;
      _trace = trace;
      _out = out;
   }

   private Object[] _args = new Object[4];
   private long _timestamp = 0;
   public void log(String name, String level, String message, Exception e)
   {
      long time = System.currentTimeMillis();
      synchronized(_args) {
         if ((time - _timestamp) > 1000) {
            _args[0] = new Date();
         }
         _args[1] = name;
         _args[2] = level;
         _args[3] = message;
         _out.println(_mf.format(_args));
         if (_trace && (e != null)) {
            e.printStackTrace(_out);
         }
      }
   }

   public void attach(LogSource l) {
      String name = l.getName();
      String level = _levels.getProperty(name);
      if (level == null) {
         level = (String) _levels.getProperty("*");
      }
      l.addTarget(this, level);   
   }
}


