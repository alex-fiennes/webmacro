package com.shinyhosting.formhandler.validator;

import java.lang.reflect.*;
import com.shinyhosting.util.*;

public class DirectValidator implements Validator
   {
   Method _getMethod;
   PropertyAccessor _setAccessor;
   Object _target;
   
   public DirectValidator(Object target, String beanPropertyName) throws IllegalArgumentException {
      _target = target;
      
      Method[] methods = target.getClass().getMethods();
      Method setMethod = null;
      
      for (int i = 0; i < methods.length; i++) {
         if (methods[i].getParameterTypes().length <= 1) {
            if (methods[i].getName().equals("get" + beanPropertyName)) 
               _getMethod = methods[i];
            else if (methods[i].getName().equals("set" + beanPropertyName)) 
               setMethod = methods[i];
            }
         }
      
      if (_getMethod == null || setMethod == null)
         throw new IllegalArgumentException("Could not find get/set methods for given beanPropertyName.");
      
      _setAccessor = PropertyAccessor.newInstance(setMethod);
      
      }
   
   public boolean isValid() {
      return true;
      }
   
   public void setValue(String value) throws Throwable {
      _setAccessor.set(_target, value);
      }
   
   public Object getValue() throws IllegalAccessException, InvocationTargetException {
      Object value = _getMethod.invoke(_target, null);
      
      return (value == null) ? "" : value;
      }
   
   public String toString() {
      try {
         return getValue().toString();
         }
      catch (Exception e) {
         // Can't throw an exception here...
         return "";
         }
      }

   /** Default implementation (shallow copy). */
   public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
   
   }

