package com.shinyhosting.formhandler.validator;

public class StringLengthValidator extends StringValidator
   {
   int _minLength = 0;
   int _maxLength = Integer.MAX_VALUE;
   
   public StringLengthValidator(int minLength, int maxLength) {
      this("", minLength, maxLength);
      }
   
   public StringLengthValidator(String s, int minLength, int maxLength) {
      super(s);
      _minLength = minLength;
      _maxLength = maxLength;
      }
   
   public String validate() {
      if (_data.length() < _minLength)
         return "Must be at least " + _minLength + " characters long.";
      else if (_data.length() > _maxLength)
         return "Maximum allowable size is " + _maxLength + " characters long.";
      else
         return null;
      }
   
   }

