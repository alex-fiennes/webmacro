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
 * Common empty interface for type handlers. Before an action method is invoked, 
 * each parameter value is passed to an assigned type handler, which performs the 
 * conversion of HTTP parameter value to a value of a Java type.
 *
 * <P><UL>
 * <LI><H3>Example:</H3>
 * Before the servlet action method:<P>
 *
 * <PRE>
 * public Template doSomething(WebContext context, String str, int number) {...}
 * </PRE>
 *
 * is called, two ActionServlet internal type handlers for <TT>java.lang.String</TT>, 
 * which is passed the value of HTTP parameter named 'str', and <TT>int</TT>, which is 
 * passed the value of HTTP parameter named 'number', are invoked.
 *
 * Note: {@link ActionServlet ActionServlet} has defined handlers all primitive Java types.
 *
 * <P><LI><H3>Notifying ActionServlet about a type handler</H3>
 * Handlers are loaded only once at {@link ActionServlet ActionServlet} startup according
 * to the <TT>&lt;servlet&gt;_handlers.properties</TT> file (mandatory name), which has 
 * the format:<P>
 * 
 * <PRE>
 * # comment
 * JavaType = fullyQualifiedTypeHandlerClass
 * </PRE>
 *
 * See {@link SimpleTypeHandler SimpleTypeHandler} and {@link CompositeTypeHandler 
 * CompositeTypeHandler} for examples.
 * </UL>
 *
 * @see ActionServlet
 */
public interface TypeHandler {}


