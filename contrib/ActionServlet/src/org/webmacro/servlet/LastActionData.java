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

import javax.servlet.http.HttpSession;
import org.webmacro.broker.ResourceUnavailableException;
import org.webmacro.engine.Template;

/**
 * Contains parameters of invoked action. One object per session.
 *
 * Instances are stored using the key 'lastActionData' in {@link 
 * Action#invoke(WebContext,Object[]) invoke()} method:
 * <PRE>
 * context.getSession().putValue("lastActionData", lastActionDataInstance); 
 * </PRE>
 *
 * @see Action
 */
final class LastActionData {
    /** Lastly invoked Action. */
    private Action lastAction;
    
    /** Converted parameters of the action. */
    private Object[] lastParams;
    
    /** 
     * Saves parameters for possible reinvocation of the 'action'. See 
     * {@link #reinvokeLastAction(WebContext) reinvokeLastAction()}.
     *
     * @param action sucessfully finished action
     * @param convertedParams converted parameters from HTTP request
     */
    LastActionData(Action action, Object[] convertedParams) {
        lastAction = action;
        lastParams = convertedParams;
    }
    
    /**
     * Reinvokes last action of session.
     *
     * @param context context
     * @return template to be displayed
     * @exception ActionException propagated exception from action method
     * @exception ResourceUnavailableException propagated exception from action method
     */
    Template reinvokeLastAction(WebContext context) 
    throws ActionException, ResourceUnavailableException {
        System.err.println("lastData.reinvokeLastAction method = " + lastAction.getMethodName());
        return lastAction.reinvoke(context, lastParams);
    }
}