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

package com.shinyhosting.formhandler;

import java.util.*;

/**
 * Convenience class for most cases where people will want to implement {@link HandlerAccessor}
 */
abstract public class AbstractHandlerAccessor implements HandlerAccessor
	{
	/** Log4J logging object. */
	static final private org.apache.log4j.Category _log = org.apache.log4j.Category.getInstance(HandlerPath.class);

	/**
	 * Returns FormHandler from calling methods internalRetrieveForHandler and internalRetrieveInnerFormHandler.
	 * <p>Only calls internalRetrieveInnerFormHandler if handlerPath.getInnerFormHandlerPath() is not null and not an empty string.
	 * @param handlerPath.getFormHandlerPath must not return null!
	 */
	public FormHandler retrieveFormHandler(HandlerPath handlerPath) throws TargetNotFoundException
	{
		_log.assert(handlerPath != null, "Passed handlerPath is null!");
		_log.assert(handlerPath.getFormHandlerPath() != null, "handlerPath.getFormHandlerPath returned null!");

		FormHandler toReturn = internalRetrieveFormHandler(handlerPath.getFormHandlerPath());

		// If handlerPath.getInnerFormHandlerPath() is equal to some value
		if (handlerPath.getInnerFormHandlerPath() != null && !handlerPath.getInnerFormHandlerPath().trim().equals(""))
			toReturn = internalRetrieveInnerFormHandler(toReturn, handlerPath.getInnerFormHandlerPath());

		return toReturn;
	}

	/**
	 * This method is to return a correct FormHandler given the handlerPath parameter.
	 */
	protected abstract FormHandler internalRetrieveFormHandler(String handlerPath) throws TargetNotFoundException;

	/**
	 * Parses innerFormHandlerPath into array and calls {@link #internalRetrieveInnerFormHandler(FormHandler, String[], int)} to recurse in the destination FormHandler.
	 */
	protected FormHandler internalRetrieveInnerFormHandler(FormHandler formHandler, String innerFormHandlerPath) throws TargetNotFoundException
	{
		// Parse innerFormHandlerPath string and extract list of values
		List innerPathList = com.shinyhosting.util.Helper.extractList(innerFormHandlerPath);
		String[] innerFormHandlerPathArray = (String[])innerPathList.toArray(new String[]{} );

		return internalRetrieveInnerFormHandler(formHandler, innerFormHandlerPathArray, 0);
	}

	/**
	 * Recursive function that loops through every inner path value, calling {@link FormHandler#getInnerHandler(String)} on each iteration.
	 */
	protected FormHandler internalRetrieveInnerFormHandler(FormHandler formHandler, String[] innerFormHandlerPathArray, int position) throws TargetNotFoundException
	{
		FormHandler target = formHandler.getInnerHandler(innerFormHandlerPathArray[position]);

		if (formHandler == null)
			{
			TargetNotFoundException targetNotFoundException = new TargetNotFoundException("InnerFormHandlerPath was not found!");

			_log.info("Call to FormHandler.getInnerHandler() returned null! Position="+position+" InnerHandlerPath=" + Arrays.asList(innerFormHandlerPathArray).toString(), targetNotFoundException);

			throw targetNotFoundException;
			}

		// If end of array, target found!
		if (innerFormHandlerPathArray.length == position + 1)
			return target;
		else // Loop again...
			return internalRetrieveInnerFormHandler(formHandler, innerFormHandlerPathArray, position + 1);
	}


	}



