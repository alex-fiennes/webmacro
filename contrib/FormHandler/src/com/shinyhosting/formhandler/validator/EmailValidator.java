package com.shinyhosting.formhandler.validator;

/*
// header - edit "Data/yourJavaHeader" to customize
// contents - edit "EventHandlers/Java file/onCreate" to customize
//
*/
public class EmailValidator extends StringValidator
   {
   
   public String validate() {
      if (_data.indexOf('@') > _data.lastIndexOf('.') ||	_data.indexOf('@') < 0 || _data.length() < 5)
            return "Email address is invalid.";
            else
               return null;
            }
   }

