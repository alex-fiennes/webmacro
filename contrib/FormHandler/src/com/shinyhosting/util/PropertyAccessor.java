/*
 * Copyright (C) 2001 Jason Bowman.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *      Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *      Neither name of Jason Bowman nor the names of any contributors 
 * may be used to endorse or promote products derived from this software 
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */  

package com.shinyhosting.util;

/**
 * @author Jason Bowman (jasonb42@mediaone.net)
 */
import java.lang.reflect.*;

abstract public class PropertyAccessor extends Accessor {
   static final private org.apache.log4j.Category _log = org.apache.log4j.Category.getInstance(PropertyAccessor.class.getName());

   static public PropertyAccessor newInstance(java.lang.reflect.AccessibleObject accessibleObject) 
      {
      _log.debug("The PropertyAccessor -newInstance() method has been called.");
         
      if (accessibleObject instanceof java.lang.reflect.Field) {
         // If the field is an object that implements the validator interface...
         _log.debug(" ------ Name: " + ((Field)accessibleObject).getName() + "  Type: " + ((Field)accessibleObject).getType());
         
         if ( com.shinyhosting.formhandler.validator.Validator.class.isAssignableFrom(((Field)accessibleObject).getType()) )
            {
            _log.debug("Validator found! Name: " + ((Field)accessibleObject).getName() + "  Type: " + ((Field)accessibleObject).getType());
            return new ValidatorAccessor((Field)accessibleObject);
            }
         else
            return new FieldAccessor((Field)accessibleObject);
         }
      else if (accessibleObject instanceof java.lang.reflect.Method) {
         
         if (((Method)accessibleObject).getParameterTypes().length == 1)
            return new UnaryMethodAccessor((Method)accessibleObject);
         else if (((Method)accessibleObject).getParameterTypes().length == 0)
            return new ActionAccessor((Method)accessibleObject);
         else 
            throw new IllegalArgumentException("Method passed to PropertyAccessor.newInstance() has more than one Parameter. Currently Unary Methods are supported only. ");
         }
      else
         throw new IllegalArgumentException("PropertyAccessor.newInstance() requires either a java.lang.reflect.Method or java.lang.reflect.Field. The object you passed was neither.");
      
      }
   
   abstract public boolean set(Object target, String value) throws Throwable;
   }


class ValidatorAccessor extends PropertyAccessor
   {
   PropertyAccessor _accessor;
   Field _field;
   
   protected ValidatorAccessor(Field field) throws IllegalArgumentException {
      _field = field;
      
      try {
         Method setMethod = field.getType().getMethod("setValue", new Class[] { String.class } );
         _accessor = PropertyAccessor.newInstance(setMethod);
         }
      catch (NoSuchMethodException e) {
         throw new IllegalArgumentException("setValue method not found. Means that Field passed was not of type Validator. Should never happen.");
         }
      }
   
   public boolean set(Object target, String value) throws Throwable {
      try {
         return this.set(target, value, true);
         }
      catch (InvocationTargetException e) {
         throw e.getTargetException();
         }
      }
   
   public boolean set(Object target, String value, boolean firstEntry) throws Throwable {
      try {
         _accessor.set(_field.get(target), value);
         }
      catch (NullPointerException e) {
         if (firstEntry)
            this.set(target, value, false);
         }
      
      return true;
      }
   }


class FieldAccessor extends PropertyAccessor
   {
   int _type = 0;
   Field _field;
   
   protected FieldAccessor(Field field) throws IllegalArgumentException {
      _field = field;
      }
   
   public boolean set(Object target, String value) throws IllegalArgumentException, IllegalAccessException {
      
      _field.set(target, convert(_field.getType(), value));
      
      return true;
      }
   
   static public Object convert(Class classType, String value) throws IllegalArgumentException, IllegalAccessException {
      
      if (classType.equals(String.class)) {
         return value;
         }
      else if (classType.equals(Integer.TYPE)) {
         return new Integer(value);
         }
      else if (classType.equals(Boolean.TYPE)) {
         return new Boolean(value);
         }
      else if (classType.equals(Character.TYPE)) {
         //throw new IllegalArgumentException("Characters not supported at this time.");
         
         if (value.length() > 1) 
            throw new IllegalArgumentException("More than one character was given. Can not convert to type Character.");
         
         return new Character(value.charAt(0));
         }
      else if (classType.equals(Long.TYPE)) {
         return new Long(value);
         }
      else if (classType.equals(Short.TYPE)) {
         return new Short(value);
         }
      else if (classType.equals(Double.TYPE)) {
         return new Double(value);
         }
      else if (classType.equals(Float.TYPE)) {
         return new Float(value);
         }
      else if (classType.equals(Byte.TYPE)) {
         return new Byte(value);
         }
      else {
         //throw new IllegalArgumentException("Primative type not found! Should never happen.");
         
         return constructInstance(classType, value);
         }
      
      }
   
   static public Object constructInstance(Class classType, String value) throws IllegalArgumentException, IllegalAccessException
      {
      Constructor construct = null;
      
      try {
         construct = classType.getConstructor( new Class[] { String.class } );
         } catch (NoSuchMethodException ignore) {}
      
      try {
         if (construct == null)
            construct = classType.getConstructor( new Class[] { Object.class } );
         } catch (NoSuchMethodException ignore) {}
      
      if (construct == null) 
         throw new IllegalArgumentException("Can not find valid constructor of type const(String) or const(Object) on class: " + classType.getName());
      else {
         try {
            return construct.newInstance( new String[] { value });
            }
         catch (InstantiationException e) {
            throw new IllegalArgumentException("Can not initialize. Constructor threw InstantiationException: " + e);
            }
         catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Can not initialize. Constructor threw InvocationTargetException: " + e);
            }
         }
      
      }
   }

class UnaryMethodAccessor extends PropertyAccessor
   {
   Method _method;
   Class _methodParameter;
   
   protected UnaryMethodAccessor(Method method) throws IllegalArgumentException {
      _method = method;
      _methodParameter = method.getParameterTypes()[0];
      }
   
   public boolean set(Object target, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      
      _method.invoke(target, new Object[] { FieldAccessor.convert(_methodParameter, value) } );
      
      return true;
      }
   }

class ActionAccessor extends PropertyAccessor
   {
   Method _method;
   
   protected ActionAccessor(Method method) throws IllegalArgumentException {
      _method = method;
      }
   
   public boolean set(Object target, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      
      _method.invoke(target, new Object[0] );
      
      return true;
      }
   }




