package com.shinyhosting.formhandler.validator;


abstract public class StringValidator implements Validator
   {
   String _data;
   
   public StringValidator() {
      _data = "";
      }
   
   public StringValidator(String s) throws IllegalArgumentException {
      setValue(s);
      }
   
   /** 
    * Returns error message or null if valid.
    */
   abstract public String validate();
   
   public void setValue(String value) throws IllegalArgumentException {
      _data = value;
      
      if (!this.isValid())
         throw new IllegalArgumentException(this.validate());
      }
   
   public boolean isValid() {
      if (this.validate() == null)
         return true;
      return false;
      }
   
   public String toString() {
      return _data;
      }
   
   /** Default implementation (shallow copy). */
   public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
   }

