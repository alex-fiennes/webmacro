/*
 * RethrowableException.java -- class for wrapping exceptions
 * Copyright (C) 1999 Quiotix Corporation.  
 *        All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *      Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 *      Neither name of Quiotix Corporation nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
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

package org.webmacro;

/**
 * RethrowableException
 *
 * A standard exception, inherited from Exception, which also includes
 * a constructor of the form Exception(String, Exception) which allows
 * one exception to wrap another without throwing away useful
 * debugging information.  The PrintStackTrace routine will print the
 * stack trace for both the original exception and the point at which
 * the exception was rethrown.  
 *
 * @author Brian Goetz (Quiotix Corp) 
 * @since 0.96
 */
public class RethrowableException extends Exception {
  
  private Throwable caught;

  private final static String RETHROW_MESSAGE = "-- secondary stack trace --";

  public RethrowableException() {
    super();
  }

  public RethrowableException(String s) {
    super(s);
  }

  public RethrowableException(String s, Throwable e) {
    super(s + System.getProperty("line.separator") + e);
    caught = e;
    while (caught instanceof RethrowableException) {
      caught = ((RethrowableException) caught).caught;
    }
  }

  public void printStackTrace() {
    super.printStackTrace();
    if (caught != null) {
      System.err.println(RETHROW_MESSAGE);
      caught.printStackTrace();
    }
  }

  public void printStackTrace(java.io.PrintStream ps) {
    super.printStackTrace(ps);
    if (caught != null) {
      ps.println(RETHROW_MESSAGE);
      caught.printStackTrace(ps);
    }
  }

  public void printStackTrace(java.io.PrintWriter pw) {
    super.printStackTrace(pw);
    if (caught != null) {
      pw.println(RETHROW_MESSAGE);
      caught.printStackTrace(pw);
    }
  }
    
  /**
   * allow access to underlying exception
   */
  public Throwable getCaught() {
    return caught;
  }
    
}