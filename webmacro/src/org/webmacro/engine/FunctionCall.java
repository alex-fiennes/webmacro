/*
 * Copyright (C) 1998-2002 Semiotek Inc.  All Rights Reserved.  
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
 * Holder for standalone function calls $a($foo)
 * 
 * @author Brian Goetz
 * @since 1.1
 */

final public class FunctionCall extends PropertyMethod 
{
   /**
     * Create a new FunctionCall
     * @param name the name of the method to call
     * @param args the arguments, including Macro objects
     */
   public FunctionCall(String name, Object[] args)
   {
      super(name, args);
   }

   /**
     * Create a new FunctionCall
     * @param name the name of the method to call
     * @param args the arguments, including Macro objects
     */
   public FunctionCall(String name, Macro args)
   {
      super(name, args);
   }
}
