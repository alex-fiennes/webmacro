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
import javax.servlet.http.*;

import org.webmacro.*;
import org.webmacro.util.PropertyMethod;
import org.webmacro.engine.VoidMacro;
import org.webmacro.engine.PropertyOperatorCache;
import org.webmacro.servlet.WebContext;

/**
 * A FormHandler takes the WebContext generated from a webmacro servlet request, performs certain services, and returns a String representing which view (Template) to return to the client.
 * <p>
 * At one time the FormHandler class was two separate classes, the FormHandler and the FormInfo. Now the FormHandler class server two functions:
 * <OL>
 *     <LI> The <b>abstract class</b> takes care of all the back-end functionality of the services it performs on the...
 *     <LI> <b>implementation class</b> that holds the data and business logic of the FormHandler.
 * </OL>
 * For every request to the FormHandler class, the following services are performed:
 * <OL>
 *   <LI>All submitted form parameters are set using WebMacros property introspection engine. (form names can be valid WM pathing script).
 *       <UL><LI><b>Note:</b> If an error occurs while setting parameters, an error page variable will be checked and returned. Also error variables will be set.</LI></UL>
 *   <LI>All doActions are performed in no particular order (This will be added later, maybe numbers prefixing the doAction name?)
 *       <UL><LI><b>Note:</b> If any doAction returns a value the FormHandling will immediately return from here. </LI></UL>
 *   <LI>The sub-classed validate method is called. This method may perform any operations it needs and msu return a template to display.
 * </OL>
 * Subclasses of FormHandler may implement different functionality, or they may provide a default validate method, or override the inner validate method to change behavior a little.
 * <p><b>WARNING:</b> Subclasses of FormHandler must be public classes in there own file! The reason is because the dynamic 
 * 	introspection can only access the public fields and methods from a public class. However, if having the FromHandler
 * 	be an inner class is useful (as I do sometimes) you can have the public "interface" of the class extended by an inner class.
 * <p><b>WARNING:</b> WebMacro will complain if you try to display a null String. Use {@link #EMPTY_STRING} for default initialization....
 */
abstract public class FormHandler
	{
	/** Return this constance when you have already handled the output. Can be used as a return from a doAction or the validate method. */
	//Enough to make the constant unpredictable and very unlikely to be every used for real...
	static final public String OUTPUT_HANDLED_CONST = "The output for this http request has already been handled" + System.currentTimeMillis();

	/** Use this constance to initialize String values so that they are not null. */
	static final public String EMPTY_STRING = "";



	static final private org.apache.log4j.Category _log = org.apache.log4j.Category.getInstance(FormHandler.class);

	boolean _pocInit = false;
	PropertyOperatorCache _poc = new PropertyOperatorCache();

	/**
	 * Called by the requesting Servlet or FormManager.
	 * 
	 * <p>Psudo code for this method follows:
	 * <UL>
	 * <LI>Add this FormHandler to the context of any returned template. (As formInfo and formHandler)</LI>
	 * <LI>Call {@link #prevalidate(WebContext)}</LI>
	 * <LI>Attempt to set all local parameters. (See {@link #setParameters(WebContext)} )</LI>
	 * <UL><LI>If an error occured during the setting of parameters and an '_errorTemplate' was specified, return that now.</LI></UL>
	 * <LI>Perform any _doActions now. If an action returns a non-null value do not continue, return the given value. (See {@link #doActions(WebContext)} )</LI>
	 * <UL><LI>The order of the _doActions is currently unspecified. An extention may be added later.</LI></UL>
	 * <LI>Calls the subclasses {@link #validate(WebContext)} method and returns its value.</LI>
	 * </UL>
	 * @return template to be displayed to the user or null if the output was already handled.
	 */
	public Object handle (WebContext c) throws RethrownException
	{
		// Let templates have access to this object.
		c.put("FormHandler", this);


		Object toReturn = prevalidate(c);
		if (toReturn != null) return(toReturn != OUTPUT_HANDLED_CONST) ? toReturn : null;

		boolean success = setParameters(c);
		_log.info("The setting of parameters was: " + success);

		// If errors occured during the setting of parameters, and if error template is specified by the page, return error template.
		if (!success && c.getRequest().getParameter("_errorTemplate") != null)
			return c.getRequest().getParameter("_errorTemplate");


		// Can you think of a better name other than validate? Ohh well...
		toReturn = this.validate(c);

		// Catch if any of the actions return a template to display
		toReturn = doActions(c);
		if (toReturn != null) return(toReturn != OUTPUT_HANDLED_CONST) ? toReturn : null;


		// Can you think of a better name other than validate? Ohh well...
		toReturn = this.postvalidate(c);

		return(toReturn != OUTPUT_HANDLED_CONST) ? toReturn : null;
	}

	/**
	 * Method the Developer can over-ride. Default implementation does nothing (returns null).
	 * <p>Handler will return immediately if this returns a value.
	 */
	protected Object prevalidate(WebContext c)
		{
		return null;
		}

	/**
	 * Once the parameter validation filter object is made, this will call its validate method.
	 */
	protected Object validate(WebContext c)
		{
		return null;
		}

	/**
	 * Method the Developer is supposed to over-ride.
	 */
	abstract protected Object postvalidate(WebContext c);


	/** Used to get inner FormHandlers. Default implementation always returns null. */
	public FormHandler getInnerHandler(String handlerName)
		{
		return null;
		}

	/** 
	 * Takes submitted form varibles and attempts to set their named variable or set method to the value submitted.
	 * It will attempt to set all values before returning. If any exception is thrown during its operation that exception
	 * will be logged using the {@link #putError(WebContext, String, Throwable)} method. 
	 * <p>No special prefix for form name is needed. The start of the introspection is this FormHandler
	 * <p>This method uses the WebMacro property introspection engine. 
	 * This only supports basic dot notation, no ()'s yet! 
	 * Adding that additional functionality would require a basic parsing step at least.
	 * @return true if all parameters were set without an exception being thrown.
	 */
	//Sets all parameters, no matter if some fail...
	// We get this to the WM engine you can set things like RealBackingObject.RealValue!!!
	// '?' for variable instead of a $???  -- Or that would be the default, 
	//         so we need something to mark a [literalString]??? []?  Or maybe _literalString_
	protected boolean setParameters(WebContext c)
		{
		boolean success = true;

		HttpServletRequest request = c.getRequest();
		Enumeration parameterNames = request.getParameterNames();
		String nextName, nextValue;

		while (parameterNames.hasMoreElements())
			{
			nextName = (String)parameterNames.nextElement();
			nextValue = request.getParameter(nextName);

			if (!nextName.startsWith("_do"))
				{
				// Only supports basic dot notation stuff. No ()'s yet! That would require a basic parsing step at least.
				List propertyArgsList = com.shinyhosting.util.Helper.extractList(nextName);

				try
					{
					PropertyOperatorCache poc = getPropertyOperatorCache();
					Object[] propertyArgs = propertyArgsList.toArray();

					boolean setReturn = false;

					// If field is a validator field...
					Object getObject = poc.getProperty(c, this, propertyArgs, 0);

					if (getObject != null && getObject instanceof com.shinyhosting.formhandler.validator.Validator)
						{
						_log.debug("parameter "+propertyArgsList+" is of the Validator type! ClassType=" + getObject.getClass());
						String fieldName = (String)propertyArgsList.get(propertyArgsList.size()-1);
						PropertyMethod pMethod = new PropertyMethod("setValue", new Object[] {nextValue});
						propertyArgsList.add(propertyArgsList.size(), pMethod);
						propertyArgs = propertyArgsList.toArray();
						Object ignore = poc.getProperty(c, this, propertyArgs, 0);

						setReturn = true;
						}
					else
						{
						setReturn = poc.setProperty(c, this, propertyArgs, 0, nextValue);
						}

					}
				catch (PropertyException ignore)
					{
					this.putError(c, nextName, ignore);
					}
				catch (Throwable allExceptions)
					{
					_log.info("Could not set Parameter " + propertyArgsList + " in object " + this, allExceptions);

					// Something went really wrong...
					success = false;
					this.putError(c, nextName, allExceptions);
					}
				} // End If

			} // End while
		return success;
		} 

	/**
	 * For every _doAction type form parameter committed this calls the associated doAction method w/ the specified form parameters.
	 * <P>There will later be a way to specify what order the doActions are called in. 
	 * The first doAction that returns a non-null non-empty String will have that template returned.
	 * <p><b>Note:</b> If a doAction specifies form parameters and they do not exist, the doAction will receive a null value. So check for nulls!
	 * <p>If an exception is thrown then it is as if the doAction returned null. So catch your own exceptions, unless you want them to be ignored.
	 * <b>THINK ABOUT DOING SOMETHING WITH IT LATER.</b>
	 *    Maybe an exception handling obj? With some exceptions it might make sense to let them go to the calling servlet/or handle
	 *    them w/ a template ment just for that type of exception. (aka: database error page).
	 * <br>ExceptionHandler.fromDoAction(exception) - encapsulates and throws the exception if it wants it passed back?
	 */
	protected Object doActions(WebContext c) throws RethrownException
	{
		HttpServletRequest request = c.getRequest();
		Enumeration parameterNames = request.getParameterNames();
		String nextName, nextValue;

		while (parameterNames.hasMoreElements())
			{
			nextName = (String)parameterNames.nextElement();
			nextValue = request.getParameter(nextName);

			if (nextName.startsWith("_do"))
				{

				try
					{
					PropertyOperatorCache poc = getPropertyOperatorCache();

					/* 
					 * Only supports basic dot notation stuff. 
					 * No ()'s yet! That would require a basic parsing step at least.
					 */
					String doMethodName;
					Object[] doMethodParameters = new String[] {};


					// Get doMethodName
					int endNamePos = (nextName.indexOf('(') != -1) ? nextName.indexOf('(') : nextName.length();
					doMethodName = nextName.substring(1, endNamePos);

					// Make sure that either both or no braces are found!
					_log.assert(!((nextName.indexOf('(') == -1 && nextName.indexOf(')') != -1) 
								  || (nextName.indexOf('(') != -1 && nextName.indexOf(')') == -1)),
								"Only one brace found! This is a parsing error! " + nextName.indexOf('(') +" "+ nextName.indexOf(')'));

					// Get all values specified.
					if (nextName.indexOf('(') != -1 && nextName.indexOf(')') != -1)
						{
						String parameters = nextName.substring(nextName.indexOf('(')+1,nextName.indexOf(')'));
						List doMethodFormParameters = com.shinyhosting.util.Helper.extractList(parameters);

						List doMethodFormValues = new ArrayList();
						Iterator formParameters = doMethodFormParameters.iterator();

						while (formParameters.hasNext())
							{
							doMethodFormValues.add(doMethodFormValues.size(), request.getParameter((String)formParameters.next()));
							}

						doMethodParameters = doMethodFormValues.toArray(doMethodParameters);
						}

					// Call doMethod
					PropertyMethod pMethod = new PropertyMethod(doMethodName, doMethodParameters);
					Object[] propertyArgs = new Object[] { pMethod};
					Object doMethodReturned = poc.getProperty(c, this, propertyArgs);

					// If something was returned...
					if (doMethodReturned != null && doMethodReturned != VoidMacro.instance)
						{
						_log.debug("doAction named "+ doMethodName + " returned the value: " + doMethodReturned);
						return doMethodReturned;
						}
					_log.debug("doAction named "+ doMethodName + " did not return any value");
					}
				catch (Throwable allExceptions)
					{
					// Something went really wrong...
					_log.error("doAction threw an Exception", allExceptions);
					this.putError(c, nextName, allExceptions);
					}
				} // End If

			} // End while
		return null;
	}

	/**
	 * This could be optimized some more... gets a WM PropertyOperatorCache object... 
	 * maybe more into a Singleton obj (where servlet can call init, or default init is just WM())
	 * NOT THREAD SAFE
	 */
	protected PropertyOperatorCache getPropertyOperatorCache() throws InitException
	{

		if (_pocInit == false)
			{
			_pocInit = true;
			WM wm = new WM();
			_poc.init(wm.getBroker(), wm.getBroker().getSettings());
			}

		return _poc;
	}

	protected void putError(WebContext c, String fieldName, Throwable e)
		{
		_log.warn("putError called on field " + fieldName + " with the exception type: " + e.getClass());

		//e.printStackTrace();
		putError(c, fieldName, e.getMessage());
		}   

	protected void putError(WebContext c, String fieldName, String errorMsg)
		{
		// Later we can implement plugable ErrorHandlers
		c.put("errorspace", "An error has been received");

		//c.put("errormessage", fieldName + " has received an error message: " + errorMsg);

		c.put(fieldName + "_error", errorMsg); //fieldName + " has received an error message: " + errorMsg);
		//System.out.println(fieldName + " has received an error message: " + errorMsg);
		c.put(fieldName + "_error_prefix", "<font color=FF0000><b>");
		c.put(fieldName + "_error_postfix", "</b></font>");
		}

	/**
	 * Default implementation.
	 * @throws CloneNotSupportedException If not over-ridded this will ALWAYS be thrown.
	 */
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

	}

