/*
 *    Action Servlet is an extension of the WebMacro servlet framework, which
 *    provides an easy mapping of HTTP requests to methods of Java components.
 *
 *    Copyright (C) 1999-2001  Petr Toman
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Library General Public
 *    License as published by the Free Software Foundation; either
 *    version 2 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Library General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this library.  If not, write to the Free Software Foundation,
 *    Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package org.webmacro.as;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Parent of ActionServlet exceptions.
 */
abstract class ASException extends Exception {
    /** Nested exception or null. */
    public final Throwable detail;

    private static String NESTED_MSG = "-- nested exception stack trace --";

    /**
     * Creates an exception with the specified message.
     */
    public ASException(String reason) {
        super(reason);
        this.detail = null;
    }

    /**
     * Creates an exception with the specified message and nested exception.
     */
    public ASException(String reason, Throwable detail) {
        super(reason);
        this.detail = detail;
    }

    /**
     * Returns exception message.
     */
    public String getMessage() {
        if (detail == null) return super.getMessage();

        return super.getMessage() + "; nested exception is: " +
               detail.getClass().getName() + ": " +
               detail.getMessage();
    }

    /**
     * Prints this exception and {@ link #detail detail} stack trace.
     */
    public void printStackTrace() {
        super.printStackTrace();

        if (detail != null) {
            System.err.println(NESTED_MSG);
            detail.printStackTrace();
        }
    }

    /**
     * Prints this exception and {@ link #detail detail} stack trace.
     */
    public void printStackTrace(PrintStream stream) {
        super.printStackTrace(stream);

        if (detail != null) {
            stream.println(NESTED_MSG);
            detail.printStackTrace(stream);
        }
    }

    /**
     * Prints this exception and {@ link #detail detail} stack trace.
     */
    public void printStackTrace(PrintWriter writer) {
        super.printStackTrace(writer);

        if (detail != null) {
            writer.println(NESTED_MSG);
            detail.printStackTrace(writer);
        }
    }
}
