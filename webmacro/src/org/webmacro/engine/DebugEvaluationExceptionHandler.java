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
 * DebugEvaluationExceptionHandler
 *
 * An implementation of EvaluationExceptionHandler which throws an exception
 * whenever it is called.  The error is stored back into the webcontect and
 * could be handled by the templatewriter.
 *
 * We use this in case of a live-situation to mail the error, or in other
 * situations to display the error rightaway.
 *
 * Example:
 *
 * #if ( $Variable.isDefined("WMERROR") )
 * {
 *	   Error(s):<HR>
 *	   #foreach $item in $WMERROR
 *	   {
 *		   $item<BR>
 *	   }
 * }
 *
 * This will generally cause the exception to be
 * displayed to the user -- useful for debugging.
 *
 * @author Marcel Huijkman (Thanks to Brian Goetz & Keats Kirsch)
 *
 *	@since	03-12-2001
 *	@version	15-07-2002
 */

package org.webmacro.engine;

import java.util.*;

import org.webmacro.Broker;
import org.webmacro.Context;
import org.webmacro.Log;
import org.webmacro.PropertyException;
import org.webmacro.util.Settings;

public class DebugEvaluationExceptionHandler implements EvaluationExceptionHandler {

   private Log _log;

   public DebugEvaluationExceptionHandler() {
   }

   public DebugEvaluationExceptionHandler(Broker b) {
      init(b, b.getSettings());
   }

   public void init(Broker b, Settings config) {
      _log = b.getLog("engine");
   }

   public void evaluate(Variable variable, Context context, Exception problem) throws PropertyException {
      handleError(variable, context, problem);
   }

   public String expand(Variable variable, Context context, Exception problem) throws PropertyException {
      return handleError(variable, context, problem);
   }


   private String handleError(Variable variable, Context context, Exception problem) throws PropertyException {
      String strError;

      ArrayList arlErrors = null;
      PropertyException propEx = null;
      if (problem instanceof PropertyException)
         propEx = (PropertyException) problem;
      else
         propEx = new PropertyException(
               "Error expanding $" + variable.getVariableName());
      propEx.setContextLocation(context.getCurrentLocation());
      strError = propEx.getMessage();

		if ( ( context.containsKey( "WMERROR" ) ) && ( context.get( "WMERROR" ) instanceof ArrayList ) ) {
         arlErrors = (ArrayList) context.get("WMERROR");
      }
      else {
         arlErrors = new ArrayList();
         context.put("WMERROR", arlErrors);
      }

		if ( strError.lastIndexOf( "\r\n" ) >= 0 ) {
			strError = strError.substring( strError.lastIndexOf( "\r\n" ) );
			strError = strError.trim();
		}

		if ( !arlErrors.contains( strError ) ) {
      arlErrors.add(strError);
		}

      if (_log != null) {
         _log.warning(strError, propEx);
      }
      // and rethrow it
      throw (PropertyException) propEx;
   }


   public String warningString(String strText) throws PropertyException {
      throw new PropertyException("Evaluation warning: " + strText)
   }


   public String errorString(String strText) throws PropertyException {
      throw new PropertyException("Evaluation error: " + strText)
   }
}

