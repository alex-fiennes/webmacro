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
 * Interface implemented by a component that wants to be informed 
 * when it is no longer used by ActionServlet. This occurs:
 *
 * <UL>
 * <LI>when ActionServlet is <TT>destroy()</TT>ed - for components with 
 *     <TT>"application"</TT> persistence,
 * <LI>when its HTTP session is found invalid - for components with <TT>"session"</TT>
 *     persistence)
 * <LI>after *different* HTTP request is processed - for components with <TT>"request"</TT>
 *     persistence)
 * </UL>
 *
 */
public interface Destroyed {
    /**
     * Method called when ActionServlet knows, it will not use this 
     * component any longer.
     */
    public void destroy();
}
