package com.shinyhosting.formhandler.validator;


/**
 * Special implementation to support customizable errors.
 * <p>
 * Say for instance you have a field that must have a value. Your NonEmptyValidator 
 * throws new InvalidException("can not be empty!")
 * <p> With this exception class your error handler can add "FieldName can not be empty!" to a list of errors
 * at the top of your page, and it can put "Can not be empty!" right next to the offending field.
 * <p> <b>Note:</b> This interface will probably change to be for customizable later... Two messages w/ a substitutible fieldName parameters...
 */
public class InvalidException extends Exception
	{
	/**
	 * Returns the error message string of this throwable object, making the first letter CAPS.
	 */
	public String getMessage()
		{
		String message = super.getMessage();

		if (Character.isLowerCase(message.charAt(0)))
			message = Character.toUpperCase(message.charAt(0)) + message.substring(1);

		return message;
		}

	/**
	 * Returns a customized error message.
	 * @return The given fieldName is concatenated with this exceptions error message.
	 */
	public String getMessage(String fieldName)
		{
		String message = super.getMessage();

		if (Character.isUpperCase(message.charAt(0)))
			message = Character.toLowerCase(message.charAt(0)) + message.substring(1);

		return fieldName + message;
		}

	/**
	 * This method will trim your message and pass it to the super constructor. Make sure the first letter is lowercase!
	 */
	public InvalidException(String msg)
		{
		super( (msg != null) ? msg.trim() :  msg );
		}

	}
