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

/**
 * Common empty interface for type handlers. Before an action method is invoked, 
 * each parameter value is passed to the assigned type handler, which performs the 
 * conversion of HTTP parameter value to a value of a Java type.<P>
 *
 * Note: {@link ActionServlet ActionServlet} has defined type handlers for all 
 * primitive types and java.lang.String.
 *
 * <P>
 * <B>Example:</B> Before the action method: 
 *
 * <PRE>
 * public Template doSomething(WebContext context, 
 *                             String str, 
 *                             int number) {
 *    // ...
 * }
 * </PRE>
 *
 * is called, two (internal) type handlers are invoked: one for <TT>java.lang.String</TT> 
 * and one for <TT>int</TT>.
 *
 * <H3>Custom type handlers</H3>
 * A type handler is a class that implements either {@link SimpleTypeHandler 
 * SimpleTypeHandler} or {@link CompositeTypeHandler CompositeTypeHandler}.<P>
 *
 * Bindng between the parameter type and its type handler is done by the 
 * <TT>&lt;type handler&gt;</TT> element in the configuration file:
 *
 * <PRE>
 *  &lt;type-handler type="<B>some.package.Type</B>"
 *                class="<B>some.package.TypeHandler</B>"/&gt;
 * </PRE>
 *
 * <B>Example</B>:
 * <PRE>
 *  &lt;type-handler type="<B>java.util.Date</B>"
 *                class="<B>my.package.DateHandler</B>"/&gt;
 * </PRE>
 *
 * @see ActionServlet
 */
public interface TypeHandler {}


