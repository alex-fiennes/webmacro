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


package org.webmacro.servlet;

import org.webmacro.*;
import java.io.*;

/**
  * Provide Template variables that implement the CGI standard for 
  * script variable names.
  */
public class CGITool implements ContextTool
{
   public Object init(Context context) 
      throws PropertyException
   {
      try {
         WebContext wc = (WebContext) context;
         CGI_Impersonator cgi = new CGI_Impersonator(wc.getRequest());
         return cgi;
      } catch (ClassCastException ce) {
         throw new PropertyException(
               "CGITool only works with WebContext", ce);
      }
   }

   public void destroy(Object o) { }
}
