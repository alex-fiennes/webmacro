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

/**
 * Holds data of a component.
 */
class ComponentData {
    static final int PERSISTENCE_APPLICATION = 1;
    static final int PERSISTENCE_SESSION = 2;
    static final int PERSISTENCE_REQUEST = 3;
    
    /** 
     * Corresponds to <TT>class</TT> of the component from the configuration file.
     */
    final Class componentClass;

    /**
     * Constructor that will be used to instantiate the component. 
     */
    final Constructor constructor;
    
    /** 
     * Corresponds to <TT>persistence</TT> attribute of the component from the 
     * configuration file. 
     */
    final int persistence;
    
    /**
     * Creates an object holding component data.
     */
    ComponentData(Class componentClass, Constructor constructor, int persistence) {
        this.componentClass = componentClass;
        this.constructor = constructor;
        this.persistence = persistence;
    }
}