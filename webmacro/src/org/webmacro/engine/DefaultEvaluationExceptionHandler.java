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

public class DefaultEvaluationExceptionHandler implements EvaluationExceptionHandler {
    private Log _log;
    
    public DefaultEvaluationExceptionHandler() {
    }
    
    public DefaultEvaluationExceptionHandler(Broker b) {
        init(b, b.getSettings());
    }
    
    public void init(Broker b, Settings config) {
        _log = b.getLog("engine");
    }
    
    public void evaluate(Variable variable, Context context, Exception problem) throws PropertyException {
        
        if (problem instanceof PropertyException.NoSuchVariableException) {
            if (_log != null)
                _log.warning("Cannot evaluate $" + variable.getVariableName()
                           + " at " + context.getCurrentLocation()
                           + ": No such variable");
            return;
        } else if (problem instanceof PropertyException.NoSuchMethodException) {
            PropertyException.NoSuchMethodException ex = (PropertyException.NoSuchMethodException) problem;
            if (_log != null)
                _log.warning("Cannot evaluate $" + variable.getVariableName()
                           + " at " + context.getCurrentLocation()
                           + ": No such method " + ex.methodName);
            // rethrow this exception
            throw ex;
        } else if (problem instanceof PropertyException.NoSuchMethodWithArgumentsException) {
            PropertyException.NoSuchMethodWithArgumentsException ex = (PropertyException.NoSuchMethodWithArgumentsException) problem;
            if (_log != null)
                _log.warning("Cannot evaluate $" + variable.getVariableName()
                           + " at " + context.getCurrentLocation()
                           + ": No such method " + ex.methodName + "(" + ex.arguments + ")");
            // rethrow this exception
            throw ex;
        } else if (problem instanceof PropertyException.NullValueException) {
            if (_log != null)
                _log.warning("Cannot evaluate $" + variable.getVariableName()
                           + " at " + context.getCurrentLocation()
                           + ": Value is null");
            return;
        } else if (problem instanceof PropertyException.NullToStringException) {
            if (_log != null)
                _log.warning("Cannot evaluate $" + variable.getVariableName()
                           + " at " + context.getCurrentLocation()
                           + ": .toSting() returns null");
            return;
        } else if (problem instanceof PropertyException) {
            if (_log != null)
                _log.warning("Cannot evaluate $" + variable.getVariableName()
                           + " at " + context.getCurrentLocation(), problem);
            // rethrow this exception
            throw (PropertyException) problem;
        } else {
            if (_log != null)
                _log.warning("Error evaluating $" + variable.getVariableName()
                           + " at " + context.getCurrentLocation(), problem);
            // rethrow problem wrapped in a PropertyExcpetion
            throw new PropertyException("Error evaluating $"
                                      + variable.getVariableName()
                                      + " at " + context.getCurrentLocation(), 
                                      problem);
        }
    }
    
    public String expand(Variable variable, Context context, Exception problem) throws PropertyException {
        
        if (problem instanceof PropertyException.NoSuchVariableException) {
            String msg = "Cannot expand $"
                           + variable.getVariableName()
                           + " at " + context.getCurrentLocation()
                           + ": No such variable";
            
            if (_log != null)
                _log.warning(msg);
            return errorString(msg);
            
        } else if (problem instanceof PropertyException.NoSuchMethodException) {
            PropertyException.NoSuchMethodException ex = (PropertyException.NoSuchMethodException) problem;
            if (_log != null)
                _log.warning("Cannot expand $" + variable.getVariableName()
                           + " at " + context.getCurrentLocation()
                           + ": No such method " + ex.methodName);
            // rethrow this exception
            throw ex;            
        } else if (problem instanceof PropertyException.NoSuchMethodWithArgumentsException) {
            PropertyException.NoSuchMethodWithArgumentsException ex = (PropertyException.NoSuchMethodWithArgumentsException) problem;
            if (_log != null) {
                _log.warning("Cannot expand $" + variable.getVariableName()
                           + " at " + context.getCurrentLocation()
                           + ": No such method " + ex.methodName + "(" + ex.arguments + ")");
            }
            // rethrow this exception            
            throw ex;
        } else if (problem instanceof PropertyException.NullValueException) {
            String msg = "Cannot expand $"
                           + variable.getVariableName()
                           + " at " + context.getCurrentLocation()
                           + ": Value is null";
            if (_log != null)
                _log.warning(msg);
            return errorString(msg);
        } else if (problem instanceof PropertyException.NullToStringException) {
            String msg = "Cannot expand $" + variable.getVariableName()
                           + " at " + context.getCurrentLocation()           
                           + ": .toString() returns null";
            if (_log != null)
                _log.warning(msg);
            return errorString(msg);
        } else if (problem instanceof PropertyException) {
            if (_log != null)
                _log.error("Cannot expand $" + variable.getVariableName()
                         + " at " + context.getCurrentLocation(), problem);
            // rethrow this exception            
            throw (PropertyException) problem;
        } else {
            String msg = "Error expanding $" + variable.getVariableName()
                           + " at " + context.getCurrentLocation();
            if (_log != null)
                _log.error(msg, problem);
            // rethrow problem wrapped in a PropertyException            
            throw new PropertyException(msg, problem);
        }
    }
    
    
    public String warningString(String warningText) {
        return "<!-- " + warningText + " -->";
    }
    
    
    public String errorString(String errorText) {
        return "<!-- " + errorText + " -->";
    }
}