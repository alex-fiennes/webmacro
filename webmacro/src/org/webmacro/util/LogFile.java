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

import java.io.*;
import java.util.*;
import java.text.MessageFormat;

public class LogFile extends AbstractLogFile {

   private PrintStream _out;

   /**
     * Create a new LogFile instance. 
     */
    public LogFile(Settings s) throws FileNotFoundException {
       super(s);
       init(s.getSetting("LogFile"));
    }

    public LogFile(String fileName) throws FileNotFoundException {
       init(fileName);
    }

   private void init(String fileName) throws FileNotFoundException {
       if ( (fileName == null) 
            || (fileName.equalsIgnoreCase("system.err") 
            || fileName.equalsIgnoreCase("none")
            || fileName.equalsIgnoreCase("stderr")))
      {
         _out = System.err;
         _name = "System.err";
      } else {
         _out = new PrintStream(new BufferedOutputStream(
                      new FileOutputStream(fileName,true)));
         _name = fileName;
      }
      if (_defaultLevel <= LogSystem.NOTICE) {
         log(Clock.getDate(), "LogFile", "NOTICE", "--- Log Started ---", 
             null);
      }
   }

   /**
     * Create a new LogFile instance
     */
   public LogFile(PrintStream out) {
      _out = out;
      _name = out.toString();
   }

   private Object[] _args = new Object[4];

   public void log(Date date, String name, String level, String message, Exception e)
   {
      synchronized(_args) {
         _args[0] = date;
         _args[1] = name;
         _args[2] = level;
         _args[3] = message;
         _out.println(_mf.format(_args));
         if (_trace && (e != null)) {
            e.printStackTrace(_out);
         }
      }
   }

   public void flush() {
      _out.flush();
   }

}


