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
 * A rethrown exception is used to encapsulate an Exception so that it will be passed out of the FormHandler.
 * <p>This class will pass every Exception method call to the class it is encapsulating.
 * @see ErrorHandler#reportException()
 */
public class RethrownException extends java.lang.Exception {

    private Exception _realException;

    public RethrownException(Exception e)
    {
	_realException = e;
    }

    /** Used to get the original Exception */
    public Exception getRealException() {
	return _realException;
    }

    public Throwable fillInStackTrace() {
	return _realException.fillInStackTrace();
    }
    public void printStackTrace() {
	_realException.printStackTrace();
    }
    public String getMessage() {
	return _realException.getMessage();
    }
    public String getLocalizedMessage() {
	return _realException.getLocalizedMessage();
    }
    public String toString() {
	return _realException.toString();
    }
    public void printStackTrace(java.io.PrintWriter s) {
	_realException.printStackTrace(s);
    }
}



