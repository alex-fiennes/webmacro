/*
 *    Action Servlet is an extension of the WebMacro servlet framework, which 
 *    provides an easy mapping of HTTP requests to methods of Java components.
 *
 *    Copyright (C) 1999-2001  Petr Toman
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Library General Public
 *    License as published by the Free Software Foundation; either
 *    version 2 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Library General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this library.  If not, write to the Free Software Foundation, 
 *    Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package org.webmacro.as;

/**
 * Thrown, if parameter conversion to a Java type fails.
 */
public class ConversionException extends Exception {
    public final Throwable detail;
    private String handlerClassName;
    private String parameterName;
    private String parameterValue;
    private String componentName;
    private String componentValue;
    private boolean thrownInComposite;
    
    /** 
     * Creates exception. This constructor is mainly used by
     * {@link SimpleTypeHandler SimpleTypeHandlers}.
     *
     * @param message error description
     * @param e exception that caused the error
     */
    public ConversionException(String message, Exception e) {
        super(message);
        detail = e;
    }

    /** 
     * Creates exception. This constructor is mainly used by
     * {@link SimpleTypeHandler SimpleTypeHandlers}.
     *
     * @param message error description
     */
    public ConversionException(String message) {
        super(message);
        detail = null;
    }

    /** 
     * Creates exception. This constructor is used by
     * {@link CompositeTypeHandler CompositeTypeHandlers}.
     *
     * @param message error description
     * @param componentName name of error component
     * @param componentValue value of error component
     * @param e exception that caused the error
     */
    public ConversionException(String message, 
                               String componentName, 
                               String componentValue, 
                               Exception e) {
        super(message);
        this.componentName = componentName;
        this.componentValue = componentValue;
        detail = e;
    }

    /** 
     * Creates exception. This constructor is used by
     * {@link CompositeTypeHandler CompositeTypeHandlers}.
     *
     * @param message error description
     * @param componentName name of error component
     * @param componentValue value of error component
     */
    public ConversionException(String message, 
                               String componentName, 
                               String componentValue) {
        super(message);
        this.componentName = componentName;
        this.componentValue = componentValue;
        detail = null;
    }

    /**
     * Returns exception message.
     */
    public String getMessage() {
        if (detail == null) return super.getMessage();
            return super.getMessage() + "; nested exception is: " + 
                   detail.getClass().getName() + ": " + detail.getMessage();
    }
 
    /**
     * Sets the class name of the handler, which caused this exception.
     */
    void setExceptionOrigin(TypeHandler object) {
        handlerClassName = object.getClass().getName();
    }
    
    /**
     * Returns the class name of the handler, which caused this exception.
     */
    public String getExceptionOrigin() {
        return handlerClassName;
    }
    
    /**
     * Sets the name of parameter from HTTP request. Is invoked only by
     * {@link ActionServlet#handle(WebContext) handle()} method.
     */
    void setParameterName(String name) {
        parameterName = name;
    }
    
    /**
     * Sets the value of parameter from HTTP request. Is invoked only by
     * {@link ActionServlet#handle(WebContext) handle()} method.
     */
    void setParameterValue(String value) {
        this.parameterValue = value;
    }
    
    /**
     * Returns the name of parameter from HTTP request, which caused the conversion error.
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * Returns the value of parameter from HTTP request, which caused the conversion error.
     */
    public String getParameterValue() {
        return parameterValue;
    }
    
    /**
     * Returns the name of component of composite parameter from HTTP request, which caused 
     * the conversion error.
     *
     * @see CompositeTypeHandler
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     * Returns the value of component of composite parameter from HTTP request, which caused 
     * the conversion error.
     *
     * @see CompositeTypeHandler
     */
    public String getComponentValue() {
        return componentValue;
    }
    
    /**
     * Sets the flag indicating, the exception was thrown in {@link CompositeTypeHandler 
     * composite type handler}.
     */
    void setWasThrownInComposite(boolean value) {
        thrownInComposite = value;
    }

    /**
     * Returns true, if this exception was thrown in {@link CompositeTypeHandler composite 
     * type handler}, which means that methods {@link #getComponentName() getComponentName()} 
     * and {@link #getComponentValue() getComponentValue()} may return sensible information.
     */
    public boolean wasThrownInCompositeHandler() {
        return thrownInComposite;
    }
}