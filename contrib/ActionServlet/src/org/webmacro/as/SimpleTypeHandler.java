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

import org.webmacro.servlet.WebContext;

/**
 * Handler performing conversion of a single parameter value from HTTP request to 
 * the value of a Java type.
 *
 * <P><B>Example:</B> A simple type handler for primitive type <TT>int</TT> is defined as:
 *
 * <PRE>
 * public class PrimitiveTypeInt <B>implements SimpleTypeHandler</B> {
 *     public Object convert(WebContext context, 
 *                           String parameterValue) 
 *     throws ConversionException {
 *         try {
 *             return Integer.valueOf(parameterValue);
 *         } catch(NumberFormatException e) {
 *             throw new ConversionException("Cannot convert '" + 
 *                                            parameterValue + 
 *                                            "' to int", e);
 *         }
 *     }
 * }
 * </PRE>
 *
 * @see CompositeTypeHandler
 */
public interface SimpleTypeHandler extends TypeHandler {
    /**
     * Method called when a parameter conversion is needed.
     *
     * @param context context from {@link ActionServlet#handle(WebContext) handle()} method
     * @param parameterValue parameter value from HTTP request
     * @exception ConversionException on conversion error
     * @return parameter value of the appropriate Java type
     */
    public Object convert(WebContext context, String parameterValue) 
    throws ConversionException;
}