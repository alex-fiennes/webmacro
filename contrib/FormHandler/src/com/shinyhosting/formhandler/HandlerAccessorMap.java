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
 * Simple implementation of HandlerAccessor backed my a HashMap.
 * <p>Keys are the handlerPath, values are the FormHandlers associated with the specified key.
 * When a FormHandler is requested, a clone of the one in the Map is returned.
 * <p><b>Warning:</b> Because clone() is used to replicate FormHandlers make sure that all provided
 * FormHandlers are {@link Cloneable} and that if they use mutable object (such as Validators) they will 
 * have to implement clone manually to produce a deep copy.
 */
public class HandlerAccessorMap extends AbstractHandlerAccessor
	{
	// Using a Hashtable to avoid synchronization issues.
	private Map _backingMap = new Hashtable();

	private boolean _ignoreCase = false;

	/**
	 * @param ignoreCase If true all keys will be turned to lowercase. Remember this for your import map!
	 */
	public HandlerAccessorMap(Map importMap, boolean ignoreCase)
		{
		_backingMap.putAll(importMap);
		_ignoreCase = ignoreCase;
		}

	/**
	 *
	 */
	protected FormHandler internalRetrieveFormHandler(String handlerPath) throws TargetNotFoundException {
		handlerPath = (_ignoreCase) ? handlerPath.toLowerCase() : handlerPath;

		FormHandler toReturn = (FormHandler)_backingMap.get(handlerPath);

		if (toReturn == null)
			throw new TargetNotFoundException("Could not locate FormHandler for handlerPath: " + handlerPath);

		try
			{
			return(FormHandler)((FormHandler)toReturn).clone();
			}
		catch (CloneNotSupportedException e)
			{
			System.out.println("HandlerAccessorMap.retrieveFormHandler() --- CLONE NOT SUPPORTED!!!!!!!! " + e);
			e.printStackTrace();
			// TODO: Add _log.exception
			}

		return null;
	}

	public Map getMap()
		{
		return _backingMap;
		}

	public boolean getIgnoreCase()
		{
		return _ignoreCase;
		}

	}
