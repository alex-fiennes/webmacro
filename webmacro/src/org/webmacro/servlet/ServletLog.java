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


/**
 *
 * Implementation of AbstractLogFile which directs log information to
 * the servlet log.
 * @since 0.96
 * @author Brian Goetz
 */

package org.webmacro.servlet;

import org.webmacro.util.*;

import java.io.*;
import java.util.*;
import java.text.MessageFormat;
import javax.servlet.*;

public class ServletLog extends AbstractLogFile {

   private static String servletDefaultFormat = "WebMacro:{1}\t{2}\t{3}";
   private ServletContext _servletContext;

   public ServletLog(ServletContext sc, Settings s) {
     super(s);
     if (_formatString == _defaultFormatString) 
        _mf = new MessageFormat(servletDefaultFormat);
     _servletContext = sc;
     _name = sc.toString();
     if (_defaultLevel <= LogSystem.NOTICE) 
         log(Clock.getDate(), "LogFile", "NOTICE", "--- Log Started ---", 
             null);
   }

   private Object[] _args = new Object[4];

   public void log(Date date, String name, String level, String message, 
                   Exception e)
   {
     synchronized(_args) {
       _args[0] = date;
       _args[1] = name;
       _args[2] = level;
       _args[3] = message;
       if (e == null)
         _servletContext.log(_mf.format(_args));
       else
         _servletContext.log(e, _mf.format(_args));
     }
   }

   public void flush() {
   }

}


