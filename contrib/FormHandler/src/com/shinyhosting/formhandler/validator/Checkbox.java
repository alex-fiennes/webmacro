package com.shinyhosting.formhandler.validator;

/*
// header - edit "Data/yourJavaHeader" to customize
// contents - edit "EventHandlers/Java file/onCreate" to customize
//
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

