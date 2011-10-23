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
 * DefaultEvaluationExceptionHandler An implementation of EvaluationExceptionHandler which throws
 * under most error conditions. Users who are generating non-HTML output should replace the
 * ExceptionHandler in their context with one that generates the appropriate comments. This should
 * be the only place in WM where HTML comments are generated into the output.
 * 
 * @author Brian Goetz
 * @since 0.96
 */

public class DefaultEvaluationExceptionHandler
  implements EvaluationExceptionHandler
{

  static Logger _log = LoggerFactory.getLogger(DefaultEvaluationExceptionHandler.class);

  public DefaultEvaluationExceptionHandler()
  {
  }

  public DefaultEvaluationExceptionHandler(Broker b)
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

    // if we were given a ProperyException, record the context location.
    if (problem instanceof PropertyException) {
      ((PropertyException) problem).setContextLocation(context.getCurrentLocation());
    } else {
      // wrap the exception in a PropertyException
      problem =
          new PropertyException("Error evaluating $" + variable.getVariableName(),
                                problem,
                                context.getCurrentLocation());
    }

    // log the warning message
    if (_log != null) {
      _log.error(problem.getMessage());
    }

    // we want to silently ignore these exceptions
    if (problem instanceof PropertyException.NoSuchVariableException
        || problem instanceof PropertyException.NullValueException
        || problem instanceof PropertyException.NullToStringException) {

      return;
    } else {
      // but we need to complain about anything else
      throw (PropertyException) problem;

    }
  }

  public String expand(Variable variable,
                       Context context,
                       Exception problem)
      throws PropertyException
  {

    // if we were given a ProperyException, record the context location.
    if (problem instanceof PropertyException) {
      ((PropertyException) problem).setContextLocation(context.getCurrentLocation());
    } else {
      // wrap the exception in a PropertyException
      problem =
          new PropertyException("Error expanding $" + variable.getVariableName(),
                                problem,
                                context.getCurrentLocation());
    }

    // log the error message
    if (_log != null) {
      _log.error(problem.getMessage());
    }

    // we just want to return an error message for these exceptions
    if (problem instanceof PropertyException.NoSuchVariableException
        || problem instanceof PropertyException.UndefinedVariableException
        || problem instanceof PropertyException.NullValueException
        || problem instanceof PropertyException.NullToStringException) {

      return errorString(problem.getMessage());
    } else {
      // but we need to complain about anything else
      throw (PropertyException) problem;
    }
  }

  public String warningString(String warningText)
  {
    return "<!-- " + warningText + " -->";
  }

  public String errorString(String errorText)
  {
    return "<!-- " + errorText + " -->";
  }
}
