/*
 *    Action Servlet, an extension of the WebMacro library, which enables easy 
 *    mapping of user 'actions' to servlet methods.
 *
 *    Copyright (C) 1999-2000  Petr Toman
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
 *    along with this library; see the file COPYING.  If not, write to
 *    the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 *    Boston, MA 02111-1307, USA.
 */
package org.webmacro.servlet;

/**
 * Handler performing conversion of a single parameter value from HTTP request to 
 * the value of a Java type.
 *
 * <P>Example: A simple type handler for primitive type <TT>int</TT> may be defined as:
 *
 * <PRE>
 * public class PrimitiveTypeInt implements SimpleTypeHandler {
 *     public Object convert(WebContext context, String parameterValue) 
 *     throws ConversionException {
 *         try {
 *             return Integer.valueOf(parameterValue);
 *         } catch(NumberFormatException e) {
 *             throw new ConversionException("Cannot convert to int", e);
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