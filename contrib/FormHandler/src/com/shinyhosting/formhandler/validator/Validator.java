package com.shinyhosting.formhandler.validator;

import java.lang.reflect.InvocationTargetException;

public interface Validator extends Cloneable
	{
	//public Validator(Object value) throws IllegalArgumentException;
	public void setValue(String value) throws Throwable;
	public boolean isValid();
	//public void setValid(boolean valid);
	public String toString() throws IllegalAccessException;

	public Object clone() throws CloneNotSupportedException;
	}

