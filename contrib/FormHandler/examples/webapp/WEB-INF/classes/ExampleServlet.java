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

import java.io.*;
import java.util.*;
import org.webmacro.*;
import org.webmacro.servlet.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.shinyhosting.formhandler.*;

/**
 * @author Jason Bowman (jasonb42@mediaone.net)
 */
public class ExampleServlet extends org.webmacro.servlet.WMServlet
	{
	private static HandlerAccessor handlerAccessor = null;


	public void start() throws ServletException {
		com.shinyhosting.util.Helper.configureLog4J();

		handlerAccessor = new ExampleHandlerAccessor();
	}

	public void stop()
		{
		System.gc();
		}


	public Template handle(WebContext c) throws HandlerException {
		c.put("title", "default title");

		HandlerPath hPath = null;
		try
			{
			hPath = new HandlerPath(c.getRequest());
			}
		catch (MalformedRequestException e)
			{
			return error(c, e.getMessage());
			}


		Template body = null;

		try
			{
			FormManager fm = FormManager.getInstance(c.getSession(), handlerAccessor);

			String templateName = (String)fm.handle(hPath, c);
			body = getTemplate(templateName);

			}
		catch (NotFoundException e)
			{
			body = error(c, "fm.handle() returned a NotFoundException: " + e);
			}
		catch (NullPointerException e)
			{
			e.printStackTrace();
			body = error(c, "NullPointerException: " + e);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			body = error(c, "Unknown Exception: " + e);
			}

		return body;
	}   

	}




class ExampleHandlerAccessor implements HandlerAccessor
	{
	public FormHandler retrieveFormHandler(HandlerPath handlerPath) throws TargetNotFoundException
	{
    	String hPath = handlerPath.getFormHandlerPath();

		if (hPath.equalsIgnoreCase("myformhandler"))
			return new MyFormHandler();
		else if (hPath.equalsIgnoreCase("example1"))
			return new ExampleHandler1();

		throw new TargetNotFoundException("You must specify a handlerPath of either myformhandler or example1.");
	}

	}
    



