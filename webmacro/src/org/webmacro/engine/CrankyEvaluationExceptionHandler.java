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


package org.webmacro.engine;

import org.webmacro.*;
import org.webmacro.util.Settings;

/**
 * CrankyEvaluationExceptionHandler
 *
 * An implementation of EvaluationExceptionHandler which throws an exception
 * whenever it is called.  This will generally cause the exception to be
 * displayed to the user -- useful for debugging.
 *
 * @author Brian Goetz
 * @since 0.96
 */

public class CrankyEvaluationExceptionHandler 
  implements EvaluationExceptionHandler {

   private Log _log;

   public CrankyEvaluationExceptionHandler() {
   }

   public CrankyEvaluationExceptionHandler(Broker b) {
      init(b, b.getSettings());
   }

   public void init(Broker b, Settings config) {
      _log = b.getLog("engine");
   }

   public void evaluate(Variable variable, 
                        Context context, 
                        Exception problem) 
   throws PropertyException {
     if (_log != null)
       _log.warning("Error evaluating variable " + variable.getVariableName()
                    + ": " + problem, problem);

     if (problem instanceof PropertyException)
       throw (PropertyException) problem;
     else 
       throw new PropertyException("Error evaluating variable " 
                                   + variable.getVariableName() + ": " 
                                   + problem, problem);
   }

   public String expand(Variable variable, 
                        Context context, 
                        Exception problem) 
   throws PropertyException {
     if (_log != null)
       _log.warning("Error expanding variable " + variable.getVariableName()
                    + ": " + problem, problem);
     if (problem instanceof PropertyException)
       throw (PropertyException) problem;
     else 
       throw new PropertyException("Error evaluating variable " 
                                   + variable.getVariableName() + ": " 
                                   + problem, problem);
   }


   public String warningString(String warningText) throws PropertyException {
      throw new PropertyException("Evaluation warning: " + warningText);
   }


   public String errorString(String errorText) throws PropertyException {
      throw new PropertyException("Evaluation error: " + errorText);
   }
}

