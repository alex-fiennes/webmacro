/*
 * Copyright (C) 1998-2001 Semiotek Inc. All Rights Reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted under the terms of either of the
 * following Open Source licenses: The GNU General Public License, version 2, or any later version,
 * as published by the Free Software Foundation (http://www.fsf.org/copyleft/gpl.html); or The
 * Semiotek Public License (http://webmacro.org/LICENSE.) This software is provided "as is", with NO
 * WARRANTY, not even the implied warranties of fitness to purpose, or merchantability. You assume
 * all risks and liabilities associated with its use. See www.webmacro.org for more information on
 * the WebMacro project.
 */

package org.webmacro.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webmacro.Broker;
import org.webmacro.Context;
import org.webmacro.PropertyException;
import org.webmacro.util.Settings;

/**
 * CrankyEvaluationExceptionHandler An implementation of EvaluationExceptionHandler which throws an
 * exception whenever it is called. This will generally cause the exception to be displayed to the
 * user -- useful for debugging.
 * 
 * @author Brian Goetz
 * @since 0.96
 */

public class CrankyEvaluationExceptionHandler
  implements EvaluationExceptionHandler
{

  static Logger _log = LoggerFactory.getLogger(CrankyEvaluationExceptionHandler.class);

  public CrankyEvaluationExceptionHandler()
  {
  }

  public CrankyEvaluationExceptionHandler(Broker b)
  {
    init(b, b.getSettings());
  }

  public void init(Broker b,
                   Settings config)
  {
  }

  public void evaluate(Variable variable,
                       Context context,
                       Exception problem)
      throws PropertyException
  {

    // if it's a PropertyException set the current context location
    if (problem instanceof PropertyException) {
      ((PropertyException) problem).setContextLocation(context.getCurrentLocation());
    } else {
      // else, wrap it
      problem =
          new PropertyException("Error evaluating $" + variable.getVariableName(),
                                problem,
                                context.getCurrentLocation());
    }

    // log it
    if (_log != null)
      _log.warn("Error evaluating $" + variable.getVariableName(), problem);

    // and rethrow it
    throw (PropertyException) problem;
  }

  public String expand(Variable variable,
                       Context context,
                       Exception problem)
      throws PropertyException
  {

    // if it's a PropertyException set the current context location
    if (problem instanceof PropertyException) {
      ((PropertyException) problem).setContextLocation(context.getCurrentLocation());
    } else {
      // else, wrap it
      problem =
          new PropertyException("Error expanding $" + variable.getVariableName(),
                                problem,
                                context.getCurrentLocation());
    }

    // log it
    if (_log != null)
      _log.warn("Error expanding $" + variable.getVariableName(), problem);

    // and rethrow it
    throw (PropertyException) problem;

  }

  public String warningString(String warningText,
                              Exception exception)
      throws PropertyException
  {
    throw new PropertyException("Evaluation warning: " + warningText, exception);
  }

  public String errorString(String errorText,
                            Exception exception)
      throws PropertyException
  {
    throw new PropertyException("Evaluation error: " + errorText, exception);
  }
}
