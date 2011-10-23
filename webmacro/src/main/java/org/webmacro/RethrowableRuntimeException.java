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
 * RethrowableRuntimeException
 *
 * A standard exception, inherited from RuntimeException, which also
 * includes a constructor of the form Exception(String, Exception)
 * which allows one exception to wrap another without throwing away
 * useful debugging information.  The PrintStackTrace routine will
 * print the stack trace for both the original exception and the point
 * at which the exception was rethrown.
 *
 * @author Brian Goetz (Quiotix Corp)
 * @since 0.96 */
public class RethrowableRuntimeException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	private Throwable cause;

    private final static String RETHROW_MESSAGE = "-- secondary stack trace --";

    public RethrowableRuntimeException ()
    {
        super();
    }

    public RethrowableRuntimeException (String s)
    {
        super(s);
    }

    public RethrowableRuntimeException (String s, Throwable e)
    {
        super(s + System.getProperty("line.separator") + e);
        cause = e;
        while (cause instanceof RethrowableRuntimeException)
        {
            cause = ((RethrowableRuntimeException) cause).cause;
        }
    }

    @Override
    public void printStackTrace ()
    {
        super.printStackTrace();
        if (cause != null)
        {
            System.err.println(RETHROW_MESSAGE);
            cause.printStackTrace();
        }
    }

    @Override
    public void printStackTrace (java.io.PrintStream ps)
    {
        super.printStackTrace(ps);
        if (cause != null)
        {
            ps.println(RETHROW_MESSAGE);
            cause.printStackTrace(ps);
        }
    }

    @Override
    public void printStackTrace (java.io.PrintWriter pw)
    {
        super.printStackTrace(pw);
        if (cause != null)
        {
            pw.println(RETHROW_MESSAGE);
            cause.printStackTrace(pw);
        }
    }

    /**
     * Return the underlying exception provided at construction time
     * or null if none was provided.
     * @return underlying cause
     * @since 1.1
     */
    @Override
    public Throwable getCause ()
    {
        return cause;
    }

    /**
     * Return the original exception cause. This will recursively
     * extract the cause if cause is a subclass of
     * <code>RethrowableException</code>.
     * @return underlying root cause
     * @since 1.1
     */
    public Throwable getRootCause ()
    {
        Throwable t = cause;
        while (t != null && t instanceof RethrowableRuntimeException)
        {
            t = ((RethrowableRuntimeException) t).cause;
        }
        return t;
    }
}
