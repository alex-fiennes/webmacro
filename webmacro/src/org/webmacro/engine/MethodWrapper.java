/*
 * MethodWrapper.java
 *
 * Created on May 24, 2002, 12:01 AM
 */

package org.webmacro.engine;

import org.webmacro.PropertyException;

import java.lang.reflect.Method;

/**
 *
 * @author  Keats
 */
public class MethodWrapper
{

    private Object _instance;
    private Class _class;
    private Method[] _methods;

    /** Creates a new instance of MethodWrapperMacro */
    public MethodWrapper (Object o, String methodName)
    {
        if (o instanceof Class)
        {
            // static methods
            _instance = null;
            _class = (Class) o;
        }
        else
        {
            _instance = o;
            _class = _instance.getClass();
        }

        Method[] mArr = _class.getMethods();
        java.util.ArrayList mList = new java.util.ArrayList(mArr.length);
        int methCnt = 0;
        for (int i = 0; i < mArr.length; i++)
        {
            if (mArr[i].getName().equals(methodName))
            {
                methCnt++;
                if (_instance == null)
                {
                    // no instance, exclude non-static methods
                    int mod = mArr[i].getModifiers();
                    if (java.lang.reflect.Modifier.isStatic(mod)) mList.add(mArr[i]);
                }
                else
                    mList.add(mArr[i]);
            }
        }
        if (mList.size() == 0)
            if (methCnt == 0)
                throw new IllegalArgumentException("No such method as " + methodName
                        + " in class " + _class.getName() + "!");
            else
                throw new IllegalStateException("Cannot invoke non-static method "
                        + methodName + " without an instance of the class " + _class.getName() + "!");
        _methods = new Method[mList.size()];
        mList.toArray(_methods);
    }

    public Object invoke (Object[] args)
            throws PropertyException
    {
        Class[] types = IntrospectionUtils.createTypesFromArgs(args);
        for (int i = 0; i < _methods.length; i++)
        {
            Method m = _methods[i];
            Class[] sig = m.getParameterTypes();
            if (IntrospectionUtils.matches(sig, types))
            {
                try
                {
                    Object obj = m.invoke(_instance, args);
                    if (obj == null
                            && m.getReturnType() == java.lang.Void.TYPE)
                        return org.webmacro.engine.VoidMacro.instance;
                    else
                        return obj;
                }
                catch (Exception e)
                {
                    String argList = java.util.Arrays.asList(args).toString();
                    String errMsg = "Unable to execute " + getDescription()
                            + " on supplied args: " + argList;
                    throw new PropertyException(errMsg, e);
                }
            }
        }
      
        // not found
        String argList = java.util.Arrays.asList(args).toString();
        String errMsg = "Unable to execute " + getDescription()
                + " on supplied args: " + argList;
        throw new PropertyException(errMsg);
    }

    public String getDescription ()
    {
        String methName = _methods[0].getName();
        String className = _methods[0].getDeclaringClass().getName();
        return className + "." + methName + "()]";
    }
}
