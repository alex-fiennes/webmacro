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

/**
 * Not yet implemented
 */
class SequentialHandler
{


}


/**
 * Saved only as an example of how a SequentionHandler could be implemented
 * @deprecated
 
import com.prayerequests.formhandler.*;
import java.util.Hashtable;
import java.lang.reflect.*;

import org.webmacro.servlet.*;

public abstract class FormInfo//_deleteme
	{

	static final public String EMPTY_STRING = "";

	// Error reporting type for default method.
	// Upon an error sets {name}_error to error message.
	static final public int DEFAULT = 0;

	protected int _errorMethod = DEFAULT;
	protected String _errorPrefix = "";
	protected String _errorPostfix = "";

	private int _currentStep = 0;

	protected int getCurrentStep()
		{
		return _currentStep;
		}
	protected void setCurrentStep(int value)
		{
		_currentStep = value;
		}
	protected void stepNext()
		{
		if (listTemplates().length > (_currentStep + 1))
			_currentStep++;
		}
	protected void stepBack()
		{
		if (_currentStep > 0)
			_currentStep--;
		}

	abstract public String[] listTemplates();
	abstract public String validate(WebContext contextData);

	public String actionBack()
		{
		stepBack();
		return listTemplates()[getCurrentStep()];
		}

	public String actionNext(WebContext contextData)
		{
		int currentStep = getCurrentStep();

		validate(contextData);

		setCurrentStep(currentStep);
		stepNext();

		return listTemplates()[getCurrentStep()];
		}


	static public FormHandler getFormHandler()
		{
		return null;
		}

	}


*/
