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


import org.webmacro.*;
import org.webmacro.util.*;
import org.webmacro.servlet.*;
import javax.servlet.http.*;

import com.shinyhosting.util.Invalidatable;
import com.shinyhosting.util.ValidHashtable;

/**
 * FormManager is a persistance layer that manages FormHandler instances.
 * <p>
 * There typically exists one FormManager object per user which is stored in the users session. 
 * Use the static getInstance methods 
 * <p>
 * FormManager uses the provided HandlerAccessor object to retrieve instances of requested FormHandlers. 
 * FormManager is backed by a {@link com.shinyhosting.util.ValidHashtable} so that if any FormHandlers
 * implement the {@link com.shinyhosting.util.Invalidatable} interface they can remove themselves from 
 * the FormManager. If this happens the FormManager will request another FormHandler from the HandlerAccessor
 * object.
 */
public class FormManager extends Object
	{
	protected ValidHashtable _handlers = new ValidHashtable();
	protected HandlerAccessor _handlerSource = null;

	static final private org.apache.log4j.Category _log = org.apache.log4j.Category.getInstance(FormManager.class);

	/**
	 * Constructs a default implementation using the specified HandlerAccessor to retrieve FormHandlers. Most users should not manually construct their own FormManagers.
	 * @see #getInstance(HttpSession, HandlerAccessor)
	 */
	public FormManager(HandlerAccessor handlerSource)
		{
		_handlerSource = handlerSource;
		}

	/**
	 * Used to pass a webpage hit to the appropriate FormHandler.<p>
	 * If special parameter "_invalidate" is set and requested FormHandler implements Invalidatable
	 * the FormHandler will be invalidated and another call to {@link #getHandler(HandlerPath)} will be made.
	 *
	 * @see #getHandler(HandlerPath)
	 * @return Template name that the specified FormHandler returned.
	 * @exception TargetNotFoundException If the HandlerAccessor could not locate a FormHandler matching the specified handlerPath and innerHandlerPath
	 */
	public String handle(HandlerPath handlerPath, WebContext c) throws TargetNotFoundException, RethrownException {

		FormHandler formHandler = getHandler(handlerPath);

		if (c.getRequest().getParameter("_invalidate") != null && formHandler instanceof Invalidatable)
			{
			((Invalidatable)formHandler).invalidate();

			// The formHandler retrieved here will be different from the one before if it really invalidated itself.
			formHandler = getHandler(handlerPath);
			}

		//c.put("HandlerPath", new HandlerURLTool(c));  -- we are current now using this Tool...
		c.put("HandlerPath", handlerPath);

		// Return the given templateName
		return(String)formHandler.handle(c);
	}

	/**
	 * Implements the persistance of the FormManager before getting a new FormHandler from {@link #_handlerSource}.
	 */
	protected FormHandler getHandler(HandlerPath handlerPath) throws TargetNotFoundException
	{
		FormHandler toReturn = null;

		if (getStoredHandler(handlerPath.getHandlerPathKey()) != null)
			{
			toReturn = getStoredHandler(handlerPath.getHandlerPathKey());
			}
		else
			{
			toReturn = _handlerSource.retrieveFormHandler(handlerPath);
			putStoredHandler(handlerPath.getHandlerPathKey(), toReturn);
			}

		return toReturn;
	}

	protected FormHandler getStoredHandler(String key)
		{
		return(_handlers.get(key) == null) ? null : (FormHandler)_handlers.get(key);
		}
	protected void putStoredHandler(String key, FormHandler value)
		{
		_handlers.put(key, value);
		}

	static public HandlerPath defaultHandlerPath(javax.servlet.http.HttpServletRequest request) throws MalformedRequestException
	{
		return new HandlerPath(request);
	}

	/**
	 * Calls getInstance(session, "FormManager", handlerAccessor);
	 * @see #getInstance(HttpSession, String, HandlerAccessor)
	 */
	static public FormManager getInstance(HttpSession session, HandlerAccessor handlerSource)
		{
		return getInstance(session, "FormManager", handlerSource);
		}

	/**
	 * Convenience function that returns FormManager, perferably from the users session.
	 * If not FormManager exists in the users session under the sessionAttributeName, a new FormManager will 
	 * be created with the given HandlerAccessor, placed in the users session, and returned.
	 * @param sessionAttributeName The attribute name under which the FormManager will be stored/retrieved under in the users session.
	 * @param handlerAccessor If a new FormManager is needed, this will be passed to the FormManagers constructor.
	 */
	static public FormManager getInstance(HttpSession session, String sessionAttributeName, HandlerAccessor handlerSource)
		{
		FormManager fm = null;

		// Attempt to get previously existing FormManager
		try
			{
			fm = (FormManager)session.getAttribute(sessionAttributeName);
			}
		catch (Exception e) // Note: this catches cast exceptions also...
			{
			fm = null;
			}

		if (fm == null)
			{
			fm = new FormManager(handlerSource);
			session.setAttribute(sessionAttributeName, fm);
			}

		return fm;
		}
	}


