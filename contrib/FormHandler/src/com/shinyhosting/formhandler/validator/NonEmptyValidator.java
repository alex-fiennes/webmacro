package com.shinyhosting.formhandler.validator;

/** 
 * Valid when any non-null or empty string is set.
 */
public class NonEmptyValidator implements Validator
	{
	boolean _data;

	public NonEmptyValidator()
		{
		_data = false;
		}

	public NonEmptyValidator(String s)
		{
		_data = false;
		}

	public void setValue(String value) throws IllegalArgumentException {
		if (value == null || value.trim().equals(""))
			_data = false;
		else
			_data = true;

		//if (!this.isValid()) throw new IllegalArgumentException(this.validate());
	}

	public String validate()
		{
		return null;
		}

	public boolean isValid()
		{
		return _data;
		}

	/** Default implementation (shallow copy). */
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
	}

