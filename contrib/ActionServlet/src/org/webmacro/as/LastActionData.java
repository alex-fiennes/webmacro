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

import javax.servlet.http.HttpSession;
import org.webmacro.Template;
import org.webmacro.servlet.WebContext;

/**
 * Contains parameters of invoked action. One object per session.
 */
final class LastActionData {
    /** Lastly invoked Action. */
    final Action action;

    /** Component object implementing action. */
    final Object component;

    /** Converted parameters of the action. */
    private final Object[] lastParams;

    /**
     * Saves parameters for possible reinvocation of the 'action'. See
     * {@link #reinvokeLastAction(WebContext) reinvokeLastAction()}.
     *
     * @param action sucessfully finished action
     * @param component object implementing action
     * @param convertedParams converted parameters from HTTP request
     */
    LastActionData(Action action, Object component, Object[] convertedParams) {
        this.action = action;
        this.component = component;
        lastParams = convertedParams;
    }

    /**
     * Reinvokes last action of session.
     *
     * @param context context
     * @return template to be displayed
     * @exception ActionException propagated exception from action method
     */
    Template reinvokeLastAction(WebContext context) throws ActionException {
        action.servlet.log.debug("Reinvoking method '" + component.getClass().getName() +
                                 "." + action.method.getName() + "'");
        return action.reinvoke(context, component, lastParams);
    }
}