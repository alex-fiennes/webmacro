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

package com.shinyhosting.formhandler.validator;

/**
 * Ment to assist in keeping track of the state of a form checkbox. Not revised yet.
 */
public class Checkbox
   {
   Boolean _value;
   
   public Checkbox(boolean value) {
      _value = new Boolean(value);
      }
   
   public Checkbox(String s) {
      _value = new Boolean(getCheckbox(s));
      }
   
   public boolean booleanValue() {
      return _value.booleanValue();
      }
   
   public boolean equals(Object obj) {
      if (obj == null)
         return false;
      else if (obj instanceof Boolean)
         return _value.equals(obj);
      else if (obj instanceof Checkbox)
         return ((Checkbox)obj).booleanValue() == _value.booleanValue();
      else
         return false;
      }
   
   public static boolean getCheckbox(String s) {
      s = s.trim();
      
      if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes") || 
         s.equalsIgnoreCase("check") || s.equalsIgnoreCase("checked")) 
         {
         return true;
         }
      else
         return false;
      }
   
   public static Checkbox valueOf(String s) {
      return new Checkbox(s);
      }
   
   public int hashCode() {
      return _value.hashCode();
      }
   
   public String getChecked() {
      if (_value.booleanValue())
         return " checked ";
      else
         return "";
      }
   
   public String getYesNo() {
      if (_value.booleanValue())
         return "Yes";
      else
         return "No";
      }
   
   public String getTrueFalse() {
      if (_value.booleanValue())
         return "True";
      else
         return "False";
      }
   
   public String toString() {
      return getChecked();
      }
   
   }

