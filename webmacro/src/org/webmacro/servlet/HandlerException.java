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

/**
  * This is an exception which a Handler can throw to indicate that it does
  * not want to process a connection request.
  */
public class HandlerException extends WebMacroException 
{

   /**
     * Declare a handler exception with a reason 
     * @param reason why the handler failed
     */
   public HandlerException(String reason) {
      super(reason);
   }

   public HandlerException(String reason, Exception e) { 
      super(reason, e);
   }
}


