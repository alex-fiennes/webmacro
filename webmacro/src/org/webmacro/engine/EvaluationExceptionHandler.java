/*
 * Copyright (C) 1998-2001 Semiotek Inc.  All Rights Reserved.  
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
 * EvaluationExceptionHandler
 *
 * An interface for specifying how certain classes of errors will be
 * handled.  The handle method is called when an exception is thrown
 * when trying to expand a variable or property reference.  The error
 * and warning methods are called from directives and context tools to
 * generate output warnings.  Any of these routines may throw exceptions,
 * in which case the enclosing servlet will catch it and generate an error
 * which the user sees (useful for debugging.)  
 *
 * @author Brian Goetz
 * @since 0.96 */

package org.webmacro.engine;

import org.webmacro.*;
import org.webmacro.util.Settings;
import org.webmacro.PropertyException;

public interface EvaluationExceptionHandler {
  
   /** 
    * Initialize the EEH 
    */
   public void init(Broker b, Settings config);

   /**
    * When an exception is detected in the process of expanding (writing)
    * a variable reference, this method is consulted.  It either throws
    * an exception, or it returns a String which can be written to the
    * output in place of the property expansion.  
    */
   public String expand(Variable variable, 
                        Context context, 
                        Exception problem)
      throws PropertyException;

   /**
    * When an exception is detected in the process of evaluating
    * a variable reference, this method is consulted.  It either returns,
    * in which case the caller is supposed to supply a default value (like
    * null), or will throw.
    */
   public void evaluate(Variable variable, 
                        Context context, 
                        Exception problem)
      throws PropertyException;

   public String warningString(String warningText)
     throws PropertyException;

   public String errorString(String errorText)
     throws PropertyException;

}
