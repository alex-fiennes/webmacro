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

import java.util.*;
import java.io.*;

/**
  * Directives, variables, macro calls, blocks, conditions, text, etc., all 
  * have this as their supertype.
  */
public interface Macro
{

   /**
     * Interpret the directive and write it out, using the values in
     * the supplied context as appropriate.
     * <p>
     * @exception PropertyException if required data was missing from context
     * @exception IOException if we could not successfully write to out
     */
   public void write(FastWriter out, Context context) 
      throws PropertyException, IOException;

   /**
     * same as out but returns a String
     * <p>
     * @exception PropertyException if required data was missing from context
     */
   public Object evaluate(Context context)
      throws PropertyException;

}

