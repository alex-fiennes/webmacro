/*
 * StaticClassWrapper.java
 *
 * Created on June 1, 2001, 12:25 PM
 */

package org.webmacro.engine;

/**
 * Simple class for wrapping a Class instance of a static class.  This allows
 * static classes to be placed into the context.
 * @author  keats_kirsch
 * @version 
 */
final public class StaticClassWrapper {

    private Class _wrappedClass;
    
    /** Creates new StaticClassWrapper */
    public StaticClassWrapper(final Class c) {
        _wrappedClass = c;
    }

    final public Class get() {
        return _wrappedClass;
    }
    
}
