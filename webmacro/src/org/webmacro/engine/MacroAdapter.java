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


package org.webmacro.engine;
import java.io.*;
import org.webmacro.*;
import org.webmacro.util.*;

/**
  * Looks like a Macro, but really it's not. Wrap any object as
  * a Macro via the createMacro method. You can use this when you
  * have to return a Macro, but what you have is something else.
  */
final public class MacroAdapter implements Macro, Visitable
{

   private Object _self;

   private MacroAdapter(Object wrapMe) 
      throws BuildException
   {
      _self = wrapMe;
      if (_self == null) {
         throw new BuildException("MacroAdapter cannot wrap null");
      }
   }

   public final String toString() {
      return _self.toString(); 
   }

   /**
     * Returns the wrapped object, context is ignored.
     */
   public final Object evaluate(Context context) {
      return _self;
   }

   /**
     * Just calls toString() and writes that, context is ignored.
     */
   public final void write(FastWriter out, Context context) 
      throws IOException
   {
      out.write(_self.toString());
   }

   public void accept(TemplateVisitor v) { v.visitString(_self.toString()); } 

   /**
     * If wrapMe is not a Macro, wrap it and return it. If it
     * is a Macro already, just return it.
     */
   public final static Macro createMacro(Object wrapMe) 
      throws BuildException
   {
      if (wrapMe == null) {
         throw new BuildException("Bug in WM: attempt to write null");
      }
      if (wrapMe instanceof Macro) {
         return (Macro) wrapMe;
      } else {
         return new MacroAdapter(wrapMe);
      }
   }

}

