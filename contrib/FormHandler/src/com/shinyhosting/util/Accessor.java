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

import java.util.*;
import java.lang.reflect.*;

/**
 * @author Jason Bowman (jasonb42@mediaone.net)
 */
public class Accessor
   {
   static private Hashtable _classInformation = new Hashtable();
   static final private org.apache.log4j.Category _log = org.apache.log4j.Category.getInstance(Accessor.class.getName());

   
   // TODO: You need to have an encapsulating class around the hashtable.
   static final public Hashtable getClassInfo(Class thisClass) {
      if (!_classInformation.containsKey(thisClass)) {
         _classInformation.put(thisClass, scanClass(thisClass));
         }
      
      return (Hashtable)_classInformation.get(thisClass);
      }
   
   static final private Hashtable scanClass(Class thisClass)
      {
      Hashtable thisClassInfo = new Hashtable();
      
      Field[] fields = thisClass.getFields();
      
      for (int i=0; i < fields.length; i++) {
         if (Modifier.isPublic(fields[i].getModifiers()) && !Modifier.isFinal(fields[i].getModifiers())) {
            _log.debug("Adding Field: " + fields[i].getName());
            thisClassInfo.put(fields[i].getName(), PropertyAccessor.newInstance(fields[i]));
            }
         else
            _log.debug("Not adding non-public Field: " + fields[i].getName());
         }
      
      Method[] methods = thisClass.getMethods();
      
      for (int i=0; i < methods.length; i++) {
         if (
            Modifier.isPublic(methods[i].getModifiers()) &&  // Method is public
               methods[i].getName().startsWith("set") &&	// is a property setter...
               methods[i].getReturnType().equals(Void.TYPE) && // returns nothing
               methods[i].getParameterTypes().length == 1 // and accepts only one input
               ) 
            {
            String propertyName = methods[i].getName().substring(3);
            
            _log.debug("Adding Method: " + methods[i].getName());
            // Blindly overwrites
            //	if (thisClassInfo.containsKey(propertyName)) {}
            thisClassInfo.put(propertyName, PropertyAccessor.newInstance(methods[i]));
            }
         else if (
            Modifier.isPublic(methods[i].getModifiers()) &&  // Method is public
               methods[i].getName().startsWith("do") &&	// is a property setter...
               methods[i].getReturnType().equals(Void.TYPE) && // returns nothing
               methods[i].getParameterTypes().length == 0 // and accepts only one input
               ) 
            {
            String propertyName = methods[i].getName();
            _log.debug("Adding Action Method: " + methods[i].getName() + " w/ the name: " + propertyName);
            thisClassInfo.put(propertyName, PropertyAccessor.newInstance(methods[i]));
            }
         else {
            _log.debug("Not adding Method: " + methods[i].getName());
            }
         
         try { 
         _log.debug("Method: " + methods[i].getName());
            _log.debug("    " + Modifier.isPublic(methods[i].getModifiers()) + " - " + 
               methods[i].getName().startsWith("do") + " - " + 
               methods[i].getReturnType().equals(Void.TYPE) + " (" + methods[i].getReturnType() + ") - " + 
               (methods[i].getParameterTypes().length == 0)
               );
            } 
         catch (Exception e)
            {
            _log.debug("Exception in accessor: " + e);
            }
         }
      
      return thisClassInfo;
      }
   }

