/*
 * Copyright (C) 1998-2000 Semiotek Inc. All Rights Reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted under the terms of either of the
 * following Open Source licenses: The GNU General Public License, version 2, or any later version,
 * as published by the Free Software Foundation (http://www.fsf.org/copyleft/gpl.html); or The
 * Semiotek Public License (http://webmacro.org/LICENSE.) This software is provided "as is", with NO
 * WARRANTY, not even the implied warranties of fitness to purpose, or merchantability. You assume
 * all risks and liabilities associated with its use. See www.webmacro.org for more information on
 * the WebMacro project.
 */

package org.webmacro;

/**
 * A PropertyException indicates some failure to evaluate a property in a context or against some
 * other object. For example, if you attempted to introspect for a value that does not exist, or
 * access a non-existant value in a context, or access a protected or private field.
 * <p>
 * PropertyExceptions that make their way through one of the core EvaluationExceptionHandlers have
 * their context location set (line and column numbers from template).
 * 
 * @author Marcel Huijkman
 * @version 27-07-2002
 */
public class PropertyException
  extends ContextException
{
  private static final long serialVersionUID = 1L;

  private String _message = null;

  public PropertyException(String reason)
  {
    super(reason);
  }

  public PropertyException(String reason,
                           Throwable e)
  {
    super(reason,
          e);
  }

  public PropertyException(String reason,
                           Throwable e,
                           String contextLocation)
  {
    super(reason,
          e);
    setContextLocation(contextLocation);
  }

  public void setMessage(String message)
  {
    _message = message;
  }

  @Override
  public String getMessage()
  {
    if (_message == null) {
      return super.getMessage();
    } else {
      String msg = _message;
      if (getContextLocation() != null && msg != null) {
        msg += " at " + getContextLocation();
      }
      return msg;
    }
  }

  // Subclasses

  /**
   * NoSuchVariableException indicates that a variable did not exist in the context against which it
   * was being evaluated.
   */

  public static class NoSuchVariableException
    extends PropertyException
  {

    /**
		 * 
		 */
    private static final long serialVersionUID = 1L;
    public String variableName;

    public NoSuchVariableException(String variableName)
    {
      super("No such variable: $" + variableName);

      this.variableName = variableName;
    }
  }

  /**
   * NullStringException indicates that a variable exists but its .toString() method returns null
   */

  public static class NullToStringException
    extends PropertyException
  {

    /**
		 * 
		 */
    private static final long serialVersionUID = 1L;
    public String variableName;

    public NullToStringException(String variableName)
    {
      super(".toString() returns null: $" + variableName);

      this.variableName = variableName;
    }
  }

  /**
   * NullValueException indicates that a variable or property exists, but evaluated to null in the
   * context against which it was being evaluated.
   */
  public static class NullValueException
    extends PropertyException
  {

    /**
		 * 
		 */
    private static final long serialVersionUID = 1L;
    public String variableName;

    public NullValueException(String variableName)
    {
      super("Value is null: $" + variableName);
      this.variableName = variableName;
    }
  }

  /**
   * NoSuchMethodException indicates that the variable did not have the requested method.
   */
  public static class NoSuchMethodException
    extends PropertyException
  {

    /**
		 * 
		 */
    private static final long serialVersionUID = 1L;
    public String methodName, className, variableName;

    public NoSuchMethodException(String methodName,
                                 String variableName,
                                 String className)
    {
      super("No public method " + methodName + " on variable $" + variableName + " of class "
            + className);
      this.variableName = variableName;
      this.className = className;
      this.methodName = methodName;
    }
  }

  /**
   * NoSuchMethodWithArgumentsException indicates that the variable did not have the a method with
   * the request name and argument list
   */
  public static class NoSuchMethodWithArgumentsException
    extends PropertyException
  {

    /**
		 * 
		 */
    private static final long serialVersionUID = 1L;
    public String methodName;
    public String className;
    public String arguments;

    public NoSuchMethodWithArgumentsException(String methodName,
                                              String className,
                                              String arguments)
    {
      super("No public method " + methodName + "(" + arguments + ")" + " in class " + className);
      this.className = className;
      this.methodName = methodName;
      this.arguments = arguments;
    }
  }

  /**
   * NoSuchPropertyException indicates that the variable did not have the requested property.
   */
  public static class NoSuchPropertyException
    extends PropertyException
  {

    /**
		 * 
		 */
    private static final long serialVersionUID = 1L;
    String _propertyName;
    String _className;
    String _variableName;

    public NoSuchPropertyException(String propertyName,
                                   String variableName,
                                   String className)
    {
      super("No public property " + propertyName + " on variable $" + variableName + " of class "
            + className);
      this._variableName = variableName;
      this._className = className;
      this._propertyName = propertyName;
    }
  }

  /**
   * VoidValueException indicates that someone tried to use the return value of a void method
   */
  public static class VoidValueException
    extends PropertyException
  {

    /**
		 * 
		 */
    private static final long serialVersionUID = 1L;
    String _variableName;

    public VoidValueException()
    {
      super("Attempt to use void value");
    }

    public VoidValueException(String variableName)
    {
      super("Variable $" + variableName + " has a void value ");
      this._variableName = variableName;
    }
  }

  /**
   * Exception thrown when a Variable isn't of the specified class type.
   */
  public static class InvalidTypeException
    extends PropertyException
  {

    /**
		 * 
		 */
    private static final long serialVersionUID = 1L;

    public InvalidTypeException(String variableName,
                                Class<?> clazz)
    {
      super("$" + variableName + " is not a " + clazz.getName());
    }
  }

  /**
   * RestrictedPropertyException indicates that the requested property may not be invoked from a
   * template due to security constraints
   */
  public static class RestrictedPropertyException
    extends PropertyException
  {

    /**
		 * 
		 */
    private static final long serialVersionUID = 1L;
    String _propertyName;
    String _className;
    String _variableName;

    public RestrictedPropertyException(String propertyName,
                                       String variableName,
                                       String className)
    {
      super("The property " + propertyName + " on variable $" + variableName + " of class "
            + className + " may not be accessed from a template.");
      this._variableName = variableName;
      this._className = className;
      this._propertyName = propertyName;
    }
  }

  /**
   * RestrictedMethodException indicates that the requested method may not be invoked from a
   * template due to security constraints
   */
  public static class RestrictedMethodException
    extends PropertyException
  {

    /**
		 * 
		 */
    private static final long serialVersionUID = 1L;
    String _propertyName;
    String _className;
    String _variableName;

    public RestrictedMethodException(String propertyName,
                                     String variableName,
                                     String className)
    {
      super("The method " + propertyName + " on variable $" + variableName + " of class "
            + className + " may not be accessed from a template.");
      this._variableName = variableName;
      this._className = className;
      this._propertyName = propertyName;
    }
  }

  /**
   * UndefinedVariableException indicates that the variable did not have the requested method.
   */
  public static class UndefinedVariableException
    extends PropertyException
  {

    /**
		 * 
		 */
    private static final long serialVersionUID = 1L;
    private String _msg = "Attempted to dereference an undefined variable.";

    public UndefinedVariableException()
    {
      super(null);
    }

    /**
     * Overloaded to return the <code>reason</code> specified during construction <b>plus</b> the
     * context location, if any.
     */
    @Override
    public String getMessage()
    {
      String msg = _msg;
      String loc = getContextLocation();
      if (loc != null) {
        msg += " at " + loc;
      }
      return msg;
    }

    @Override
    public void setMessage(String msg)
    {
      _msg = msg;
    }
  }

}
