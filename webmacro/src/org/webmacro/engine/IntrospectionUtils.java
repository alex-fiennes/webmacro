/*
 * IntrospectionUtils.java
 *
 * Created on May 25, 2002, 5:42 PM
 */

package org.webmacro.engine;

/**
 *
 * @author  Keats
 */
public class IntrospectionUtils {
    
    /** Creates a new instance of IntrospectionUtils */
    private IntrospectionUtils() {
    }
    
    final static boolean matches(Class[] sig, Class[] args) {
        if (args.length != sig.length)
            return false;
        
        try {
            for (int i = 0; i < sig.length; i++) {
                Class s = sig[i];
                Class a = args[i];
                if (s.isPrimitive()) {
                    if ((s == Integer.TYPE && a == Integer.class) ||
                    (s == Boolean.TYPE && a == Boolean.class) ||
                    (s == Character.TYPE && a == Character.class) ||
                    (s == Long.TYPE && a == Long.class) ||
                    (s == Short.TYPE && a == Short.class) ||
                    (s == Double.TYPE && a == Double.class) ||
                    (s == Float.TYPE && a == Float.class) ||
                    (s == Void.TYPE && a == Void.class) ||
                    (s == Byte.TYPE && a == Byte.class))
                        continue;
                    else
                        return false;
                }
                else if (a == null || s.isAssignableFrom(a))
                    continue;
                else
                    return false;
            }
        }
        catch (NullPointerException e) {
            return false; // XXX: block nulls, isAssign... throws this
        }
        return true;
    }
    
    final static public Class[] createTypesFromArgs(Object[] args){
        Class[] types = new Class[ args.length ];
        for (int i = 0; i < args.length; i++) {
            try {
                types[i] = args[i].getClass();
            } catch (NullPointerException e) {
                types[i] = null;
            }
        }
        return types;
    }
    
    /** attempt to instantiate a class with the supplied args */
    static public Object instantiate(Class c, Object[] args)
    throws Exception {
        Object o = null;
        if (args == null || args.length == 0){
            o = c.newInstance(); // no arg constructor
        }
        else {
            // try each constructor with the right number of args,
            // untill one works or all have failed
            Exception lastException = null;
            java.lang.reflect.Constructor[] cons = c.getConstructors();
            for (int i=0; i<cons.length; i++){
                if (cons[i].getParameterTypes().length == args.length){
                    // try to instantiate using this constructor
                    try {
                        o = cons[i].newInstance(args);
                        break; // if successful, we're done!
                    } catch (Exception e){
                        lastException = e;
                    }
                }
            }
            if (o == null){
                throw new InstantiationException(
                "Unable to construct object of type " + c.getName()
                + " using the supplied arguments: "
                + java.util.Arrays.asList(args).toString());
            }
        }
        return o;
    }
}
