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

import java.lang.reflect.Constructor;
import java.util.Hashtable;

/**
 * Holds data of a component.
 */
class ComponentData {
    static final int PERSISTENCE_APPLICATION = 1;
    static final int PERSISTENCE_SESSION = 2;
    static final int PERSISTENCE_REQUEST = 3;

    /**
     * Attibute 'name' of &lt;component&gt; element.
     */
    final String componentName;

    /**
     * Corresponds to <TT>class</TT> of the component from ActionConfig.
     */
    final Class componentClass;

    /**
     * Constructor that will be used to instantiate the component.
     */
    final Constructor constructor;

    /**
     * Indicates number of parameters if one.
     */
    final boolean consOne;

    /**
     * Corresponds to <TT>persistence</TT> attribute of the component from
     * ActionConfig.
     */
    final int persistence;

    /**
     * Table of &lt;on-return&gt; elements:
     * key = field value, value = template name (of template to be displayed)
     */
    final Hashtable onReturns = new Hashtable();

    /**
     * Table of &lt;on-return&gt; elements:
     * key = field value, value = vector of OutputVariables
     */
    final Hashtable onReturnsOutputVars = new Hashtable();

    /**
     * Table &lt;properties&gt;: key = property name, value = property value.
     */
    final Hashtable properties = new Hashtable();

    /**
     * Creates an object holding component data.
     */
    ComponentData(String componentName, Class componentClass,
                  Constructor constructor, boolean consOne, int persistence) {
        this.componentName = componentName;
        this.componentClass = componentClass;
        this.constructor = constructor;
        this.consOne = consOne;
        this.persistence = persistence;
    }
}