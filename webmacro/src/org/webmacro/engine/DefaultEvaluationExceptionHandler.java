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
 * DefaultEvaluationExceptionHandler
 *
 * An implementation of EvaluationExceptionHandler which throws under most
 * error conditions.  Users who are generating non-HTML output
 * should replace the ExceptionHandler in their context with one that
 * generates the appropriate comments.  This should be the only place
 * in WM where HTML comments are generated into the output. 
 *
 * @author Brian Goetz
 * @since 0.96
 */

package org.webmacro.engine;

import org.webmacro.*;
import org.webmacro.util.Settings;

public class DefaultEvaluationExceptionHandler 
  implements EvaluationExceptionHandler {
   private Log _log;

   public DefaultEvaluationExceptionHandler() {
   }

   public DefaultEvaluationExceptionHandler(Broker b) {
      init(b, b.getSettings());
   }

   public void init(Broker b, Settings config) {
      _log = b.getLog("engine");
   }

   public void evaluate(Variable variable, 
                        Context context, 
                        Exception problem) 
   throws PropertyException {
      if (problem instanceof PropertyException.NoSuchVariableException
          || problem instanceof PropertyException.NullValueException
          || problem instanceof PropertyException.NullToStringException) {
         if (_log != null)
           _log.warning ("Error evaluating variable " + variable.getVariableName()
                       + ": " + problem);
         return;
      }
      else if (problem instanceof PropertyException) {
         if (_log != null)
            _log.warning("Error evaluating variable " + variable.getVariableName()
                       + ": " + problem, problem);
         throw (PropertyException) problem;
      }
      else {
         if (_log != null)
            _log.warning("Error evaluating variable " + variable.getVariableName()
                       + ": " + problem, problem);
         throw new PropertyException("Error evaluating variable " 
                                     + variable.getVariableName() + ": " 
                                     + problem, problem);
      }
   }

   public String expand(Variable variable, 
                        Context context, 
                        Exception problem) 
   throws PropertyException {
      if (problem instanceof PropertyException.NoSuchVariableException) {
        if (_log != null)
          _log.warning("Error expanding variable " + variable.getVariableName()
                       + ": " + problem);
         return errorString("Attempt to access nonexistent variable $" 
                            + variable.getVariableName());
      }
      else if (problem instanceof PropertyException.NullValueException) {
        if (_log != null)
          _log.warning("Error expanding variable " + variable.getVariableName()
                       + ": " + problem);
         return errorString("Attempt to dereference null value $" 
                            + variable.getVariableName());
      }
      else if (problem instanceof 
                 PropertyException.NullToStringException) {
        if (_log != null)
          _log.warning("Error expanding variable " + variable.getVariableName()
                       + ": " + problem);
         return errorString("Variable $" 
                            + variable.getVariableName()
                            + ".toString() returns null");
      }
      else if (problem instanceof PropertyException) {
        if (_log != null)
          _log.warning("Error expanding variable " + variable.getVariableName()
                       + ": " + problem, problem);
         throw (PropertyException) problem;
      }
      else {
        if (_log != null)
          _log.warning("Error expanding variable " + variable.getVariableName()
                       + ": " + problem, problem);
         throw new PropertyException("Error evaluating variable " 
                                     + variable.getVariableName() + ": " 
                                     + problem, problem);
      }
   }


   public String warningString(String warningText) {
      return "<!-- " + warningText + " -->";
   }


   public String errorString(String errorText) {
      return "<!-- " + errorText + " -->";
   }
}

