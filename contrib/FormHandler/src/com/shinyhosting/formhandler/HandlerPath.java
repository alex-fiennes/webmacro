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

import javax.servlet.http.HttpUtils;
import javax.servlet.http.HttpServletRequest;

/**
 * A handler path encapsulates the information to uniquely specify a FormHandler object.
 * <p>In a more dynamic application this object can specify an object to get a FormHandler from.
 */
public class HandlerPath
	{
	/** 
	 * Default name of the form variable for the HandlerPath. Currently = "handlerPath" 
	 * <p>Not final so subclasses can overwrite (and have generated URL's be correct!)
	 */
	static final public String DEFAULT_HANDLERPATH_NAME = "handlerPath";

	/** 
	 * Name of the form variable for the InnerHandlerPath. Currently = "innerHandlerPath" 
	 * <p>Not final so subclasses can overwrite (and have generated URL's be correct!)
	 */
	static final public String DEFAULT_INNERHANDLERPATH_NAME = "innerHandlerPath";


	/** Used to store the HttpServletRequest that created this object. */
	protected HttpServletRequest _request = null;

	/** Used to store the FormHandlerPath. */
	protected String _formHandlerPath = null;
	/** Used to store the FormHandlerPath form parameter name used in the calling URL. */
	protected String _formHandlerPathName = null;

	/** Used to store the InnerFormHandlerPath. */
	protected String _innerFormHandlerPath = null;
	/** Used to store the InnerFormHandlerPath form parameter name used in the calling URL. */
	protected String _innerFormHandlerPathName = null;


	static final private org.apache.log4j.Category _log = org.apache.log4j.Category.getInstance(HandlerPath.class);


	/**
	 * Convenience constructor that calls {@link #HandlerPath(HttpServletRequest, String, String)} with {@link #DEFAULT_HANDLERPATH_NAME} and {@link #DEFAULT_INNERHANDLERPATH_NAME}.
	 * @see #HandlerPath(HttpServletRequest, String, String)
	 */
	public HandlerPath(HttpServletRequest request)
	throws MalformedRequestException
	{
		this(request, DEFAULT_HANDLERPATH_NAME, DEFAULT_INNERHANDLERPATH_NAME);
	}

	/**
	 * Convenience constructor for extracting the given parameter names from provided HttpServletRequest.
	 * @see #HandlerPath(HttpServletRequest, String, String, String, String)
	 */
	public HandlerPath(HttpServletRequest request, String formHandlerPathName, String innerFormHandlerPathName)
	throws MalformedRequestException
	{
		this(request, formHandlerPathName, request.getParameter(formHandlerPathName), innerFormHandlerPathName, request.getParameter(innerFormHandlerPathName));
	}

	/**
	 * Constructs a new HandlerPath object with the provided information.
	 * @param formHandlerPathName must not be null or an empty String!
	 * @param formHandlerPath must not be null or an empty String!
	 * @param innerFormHandlerPathName must not be null or an empty String!
	 */
	public HandlerPath(HttpServletRequest request, String formHandlerPathName, String formHandlerPath, String innerFormHandlerPathName, String innerFormHandlerPath)
	throws MalformedRequestException
	{
		if (formHandlerPath == null || formHandlerPath.equals(""))
			throw new MalformedRequestException("Query string "+ formHandlerPathName + " must exist and must contain a value!");

		_log.assert(formHandlerPath != null && !formHandlerPath.equals(""), "formHandlerPath is null or an empty string!");
		_log.assert(formHandlerPathName != null && !formHandlerPathName.equals(""), "formHandlerPathName is null or an empty string!");
		_log.assert(innerFormHandlerPathName != null && !innerFormHandlerPathName.equals(""), "innerFormHandlerPathName is null or an empty string!");

		_request = request;
		_formHandlerPath = formHandlerPath;
		_formHandlerPathName = formHandlerPathName;
		_innerFormHandlerPath = innerFormHandlerPath;
		_innerFormHandlerPathName = innerFormHandlerPathName;

		if (_log.isDebugEnabled())
			_log.debug("_formHandlerPath=" + _formHandlerPath + " & _innerFormHandlerPath=" + _innerFormHandlerPath);
	}

	/** Returns the FormHandlerPath contained in this HandlerPath object. */
	public String getFormHandlerPath()
		{
		return _formHandlerPath;
		}
	/** Returns the parameter name used to store the FormHandlerPath in an http request. */
	public String getFormHandlerPathName()
		{
		return _formHandlerPathName;
		}
	/** Returns the InnerFormHandlerPath contained in this HandlerPath object. */
	public String getInnerFormHandlerPath()
		{
		return _innerFormHandlerPath;
		}
	/** Returns the parameter name used to store the FormHandlerPath in an http request. */
	public String getInnerFormHandlerPathName()
		{
		return _innerFormHandlerPathName;
		}

	/** 
	 * Returns the HttpServletRequest passed to this HandlerPath at creation.
	 */
	public HttpServletRequest getRequest()
		{
		return _request;
		}

	/** 
	 * Returns the http request URL used to create this HandlerPath. Uses: HttpUtils.getRequestURL(HttpServletRequest)
	 * <p>Note: The returned StringBuffer does not include query parameters.
	 */
	public StringBuffer getRequestedURL()
		{
		return HttpUtils.getRequestURL(this.getRequest());
		}

	/**
	 * Key used to store this HandlerPath in FormManager.
	 * @return formHandlerPath&innerFormHandlerPath or formHandlerPath
	 */
	public String getHandlerPathKey()
		{
		if (getInnerFormHandlerPath() == null)
			return getFormHandlerPath();

		StringBuffer toReturn = new StringBuffer();
		toReturn.append(getFormHandlerPath());
		toReturn.append('&');
		toReturn.append(getInnerFormHandlerPath());


		if (_log.isDebugEnabled())
			_log.debug("getHanderPathKey() = "+toReturn);

		return toReturn.toString();
		}

	/** Returns "HandlerPath: {@link #getHandlerPathKey()}" */
	public String toString()
		{
		return "HandlerPath: " + getHandlerPathKey();
		}


/* --------------------------
 * Start URL tools area.
 * --------------------------
 */

	/**
	 * Constructs an absolute URL to the same Request URL with the same handlerPath and innerHandlerPath form variables.
	 */
	public StringBuffer getURL()
		{
		return returnURL(HttpUtils.getRequestURL(_request), _formHandlerPathName, _formHandlerPath, _innerFormHandlerPathName, _innerFormHandlerPath);
		}

	/**
	 * Constructs an absolute URL to the same Request URL but with the specified handlerPath and no innerHandlerPath.
	 * @param handlerPath - null value will be replaced by the pre-existing value of this HandlerPath object
	 */
	public StringBuffer getURL(String handlerPath)
		{
		handlerPath = (handlerPath == null) ? _formHandlerPath: handlerPath;

		return returnURL(HttpUtils.getRequestURL(_request), _formHandlerPathName, handlerPath, null, null);
		}

	/**
	 * Constructs an absolute URL to the same Request URL but with the specified handlerPath and innerHandlerPath.
	 * @param handlerPath - null value will be replaced by the pre-existing value of this HandlerPath object
	 * @param innerHandlerPath - null value will be replaced by the pre-existing value of this HandlerPath object
	 */
	public StringBuffer getURL(String handlerPath, String innerHandlerPath)
		{
		handlerPath = (handlerPath == null) ? _formHandlerPath: handlerPath;
		innerHandlerPath = (innerHandlerPath == null) ? _innerFormHandlerPath: innerHandlerPath;

		return returnURL(HttpUtils.getRequestURL(_request), _formHandlerPathName, handlerPath, _innerFormHandlerPathName, innerHandlerPath);
		}


	/** @see #returnURL(StringBuffer, String, String, String, String) */
	static public StringBuffer returnURL(String requestedURL, String handlerPathName, String handlerPath, String innerHandlerPathName, String innerHandlerPath)
		{
		return returnURL(new StringBuffer(requestedURL), handlerPathName, handlerPath, innerHandlerPathName, innerHandlerPath);
		}

	/**
	 * Returns an absolute URL given the parameters.
	 * @param <i>handlerPath</i> can not be null.
	 * @param <i>handlerPathName</i> can not be null.
	 * @param <i>innerHandlerPathName</i> can only be null if <i>innerHandlerPath</i> is also null.
	 */
	static public StringBuffer returnURL(StringBuffer requestedURL, String handlerPathName, String handlerPath, String innerHandlerPathName, String innerHandlerPath)
		{
		_log.assert(handlerPath != null, "getCallingURL() parameter handlerPath was null!");
		_log.assert(handlerPathName != null, "getCallingURL() parameter handlerPathName was null!");

		if (innerHandlerPath != null)
			_log.assert(innerHandlerPath != null, "getCallingURL() parameter handlerPathName was null!");

		StringBuffer toReturn = requestedURL;
		toReturn.append('?');

		toReturn.append(handlerPathName);
		toReturn.append('=');
		toReturn.append(handlerPath);

		if (innerHandlerPath != null)
			{
			toReturn.append('&');
			toReturn.append(innerHandlerPathName);
			toReturn.append('=');
			toReturn.append(innerHandlerPath);
			}

		// This added because netscape will drop form parameters passed in the form tag w/o an ending '&'
		toReturn.append('&');

		return toReturn;
		}

	}



// These methods are so specilized that they should really be put in a separate tool, 
// or in a sub-class of the class.

/** Returns true is _innerFormHandlerPath is not null and, when trimmed, not an empty string. */
/*
public boolean existsSuperInnerHandlerPath()
	{
	return(_innerFormHandlerPath != null && !_innerFormHandlerPath.trim().equals(""));
	}
*/

/**
 * Constructs a new absolute URL to the same Request URL and handlerPath but with different innerHandlerPath form variable.
 * innerHandlerPath will be stripped of its last value (separated by ','), or if non exist will be an empty string.
 */
/*
public StringBuffer getSuperInnerHandlerPathURL()
	{
	_log.assert(existsSuperInnerHandlerPath(), "No SuperInnerHandlerPath exists!");

	// Take after last '.' off, or if no '.' return empty string
	String innerHandlerPath = (_innerFormHandlerPath.indexOf('.') == -1) ? "": _innerFormHandlerPath.substring(0, _innerFormHandlerPath.lastIndexOf('.'));

	return getCallingURL(HttpUtils.getRequestURL(_request), _formHandlerPath, innerHandlerPath);
	}
*/

/**
 * Constructs a new absolute URL to the same Request URL and handlerPath but with different innerHandlerPath form variable.
 * innerHandlerPath will have the specified value added to the list.
 */
/*
public StringBuffer getAppendedInnerHandlerPathURL(String toAppend)
	{
	_log.assert(existsSuperInnerHandlerPath(), "No SuperInnerHandlerPath exists!");

	String innerHandlerPath = null;

	// If innerHandlerPath is empty, no need to add a separator character.
	if (_innerFormHandlerPath == null || _innerFormHandlerPath.trim().equals(""))
		{
		innerHandlerPath = toAppend;
		}
	else
		{
		StringBuffer newInnerHandlerPath = new StringBuffer();
		newInnerHandlerPath.append(_innerFormHandlerPath);
		newInnerHandlerPath.append('.');
		newInnerHandlerPath.append(toAppend);

		innerHandlerPath = newInnerHandlerPath.toString();
		}

	return getCallingURL(HttpUtils.getRequestURL(_request), _formHandlerPath, innerHandlerPath);
	}
*/




