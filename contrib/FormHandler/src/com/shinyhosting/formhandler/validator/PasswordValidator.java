package com.shinyhosting.formhandler.validator;

/**
 * Does not return any value. Use get() to retrieve real value.
 */
public class PasswordValidator extends StringValidator 
   {
   public String get() {
      return _data;
      }
   
   public String toString() {
      return "";
      }
   
   public String validate() {
      return null;
      }

   }
