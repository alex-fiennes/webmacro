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
  * The context supplied to a macro did not contain information that
  * the macro required in order to write or evaluate itself, or there
  * was some problem with the way the Context was used.
  */
public class ContextException extends WebMacroException
{
   public ContextException() 
   {
      super();
   }

   public ContextException(String reason) 
   {
      super(reason);
   }

   public ContextException(String reason, Throwable e) 
   {
      super(reason, e);
   }

}
