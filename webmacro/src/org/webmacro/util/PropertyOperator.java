
/*
 * Copyright (c) 1998, 1999 Semiotek Inc. All Rights Reserved.
 *
 * This software is the confidential intellectual property of
 * of Semiotek Inc.; it is copyrighted and licensed, not sold.
 * You may use it under the terms of the GNU General Public License,
 * version 2, as published by the Free Software Foundation. If you 
 * do not want to use the GPL, you may still use the software after
 * purchasing a proprietary developers license from Semiotek Inc.
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See the attached License.html file for details, or contact us
 * by e-mail at info@semiotek.com to get a copy.
 */


package org.webmacro.util;
import org.webmacro.util.java2.*;
import java.util.*;
import java.lang.reflect.*;
import org.webmacro.*;


/**
  * This class knows how to extract properties from objects efficiently.
  * <p>
  * A simple property "Bar" can be accessed in a sub-object "Foo" (and
  * in one case, sub-object "Baz") using any of the following method 
  * signatures, listed in the order that they will be tried:
  * <ul>
  * <li>Foo.Bar
  * <li>Foo.getBar() / Foo.setBar(object)
  * <li>Foo.getBar("Baz") / Foo.setBar("Baz", object)
  * <li>Foo.get("Bar") / Foo.put("Bar",object)
  * </ul>
  * <p>
  * The PropertyOperator is capable of extracting an iterator from an object
  * if in any of the following conditions are true, listed in the order that
  * they will be tried:
  * <ul>
  * <li>The object itself is an array
  * <li>The object itself is an Iterator
  * <li>The object itself is an Enumeration
  * <li>The object has an "Iterator iterator()" method
  * <li>The object has an "Enumeration elements()" method
  * </ul>
  * <p>
  * You can specify a long list of property names, and the above methods
  * will be recursively applied. For example, if your list were 
  * Order,Customer,Fred,Name,Last the PropertyOperator might be 
  * able to access it as:<pre>
  *     Order.getCustomer("Fred").getName().Last
  * </pre>
  */
final public class PropertyOperator
{

   // debugging

   static final boolean _debug = Log.debug && false;
   static final Log _log = new Log("prop","Property Introspection");

   // static public interface

   /**
     * Attempt to retrieve a property using the rules of property 
     * introspection described above.
     * @param context outermost container which is introspected
     * @param names property names, one per array entry 
     * @return the property described by the names, inside the instance
     * @exception PropertyException the property we'd like to look at
     * @exception SecurityExeption you are not permitted to try
     * @exception InvalidContextException one of the names is a PropertyReference which could not be evaluated against the supplied context
     */
   static final public Object getProperty(
         final Object context, final Object[] names) 
      throws PropertyException, SecurityException, InvalidContextException
   {
      try {
         return getOperator(context.getClass()).getProperty(
               context,context,names,0,names.length - 1);
      } catch (NoSuchMethodException e) {
         _log.exception(e);
         throw new PropertyException("No method to access property: " + e,e);
      }
   }

   /**
     * Given a property description name, attempt to set the property
     * value to the supplied object.
     * @param context An object containing a property
     * @param names The string names of that property 
     * @param value the new value the property is to be set to
     * @exception PropertyException not possible to set the property
     * @exception SecurityException you are not permitted to try
     * @exception InvalidContextException a PropertyReference in the argument names could not be resolved against the supplied context
     */
   static final public boolean setProperty(
         final Object context, final Object[] names, final Object value) 
      throws PropertyException, SecurityException, InvalidContextException
   {
      try {
         return getOperator(context.getClass()).setProperty(context,context,names,value,0);
      } catch (NoSuchMethodException e) {
         throw new PropertyException("No method to access property: " + e,e);
      }
   }

   /**
     * Evaluate the supplied object and work out a way to return it 
     * as an iterator.
     * @param context an object believed to represent a list
     * @return an Iterator that iterates through that list
     * @exception PropertyException could not extract iterator from instance
     */
   static final public Iterator getIterator(Object instance)
      throws PropertyException
   {
      return getOperator(instance.getClass()).findIterator(instance);
   }

   // operator cache

   /**
     * Private cache of all the property operators constructed so far
     */
   static final private Hashtable _operators = new Hashtable();


   /**
     * Find the PropertyOperator that knows about this type of object
     */
   static private PropertyOperator getOperator(final Class type)
      throws PropertyException
   {
      PropertyOperator o = (PropertyOperator) _operators.get(type);
      if (o == null) {
         o = new PropertyOperator(type);
         _operators.put(type,o);
      }
      return o;
   }

   /**
     * My accessors for fields, and binary methods
     */
   final private Hashtable _unaryAccessors = new Hashtable();

   /**
     * Accessors that require an additional property name
     */
   final private Hashtable _binaryAccessors = new Hashtable();

   /**
     * Accessors for direct method calls
     */
   final private Hashtable _directAccessors = new Hashtable();

   /**
     * Hash table accessor
     */
   private BinaryMethodAccessor hashAccessor;

   /**
     * The iterator method we found
     */
   private Method iteratorMethod = null;

   /**
     * Construct a property operator for the target class
     */
   private PropertyOperator(final Class target)
      throws SecurityException, PropertyException
   {

      if (_debug) {
         _log.debug("new PropertyOperator(" + target + ")");
      }
      
      Accessor acc;

      // introspect fields first

      Field[] fields = target.getFields();
      for (int i = 0; i < fields.length; i++) {
         if (Modifier.isPublic(fields[i].getModifiers())) {
            if (_debug) {
               _log.debug("Adding field: " + fields[i]);
            }
            acc = new FieldAccessor(fields[i]);
            _unaryAccessors.put(acc.getName(),acc);
         } else if (_debug) {
            _log.debug("Skipped non-public field: " + fields[i]);
         }
      }

      // introspect methods second

      Method[] methods = target.getMethods();

      Method meth;
      Class[] params;
      String name,propName;

      for (int i = 0; i < methods.length; i++) 
      {
         meth = methods[i];

         // don't look at non-public methods
         if (! Modifier.isPublic(meth.getModifiers())) {

            if (_debug) {
               _log.debug("Skipped non-public method: " + meth);
            }

            continue;
         }

         /*if (_debug) {
          *  _log.debug("Checking " + meth);
          *}
          */

         name = meth.getName();
         params = meth.getParameterTypes();
         int plength = params.length;

         // add direct accessor
         acc = (Accessor) _directAccessors.get(name);
         if (acc != null) {
            ((DirectAccessor) acc).addMethod(meth,params); 
         } else {
            acc = new DirectAccessor(name,meth,params);
            _directAccessors.put(name,acc);
         }

         // check for get/set/put method
         if ((name.startsWith("get") || 
               name.startsWith("set")) || name.equals("put"))
         {

            propName = name.substring(3);

            if ( ((plength == 0) && name.startsWith("get"))  ||
                 ((plength == 1) && name.startsWith("set")) )
            {

               // unary get/set method
               acc = (Accessor) _unaryAccessors.get(propName);
               if (acc != null) {
                  if (acc instanceof MethodAccessor) {
                     if (_debug) {
                        _log.debug("Updating existing accessor: " + meth);
                     }
                     ((MethodAccessor) acc).addMethod(meth,params);
                  } else if (_debug) {
                     _log.debug("Superceded by a field: " + meth);
                  }
               } else {
                  if (_debug) {
                     _log.debug("Adding new accessor: " + meth);
                  }
                  acc = new UnaryMethodAccessor(propName,meth,params);
                  _unaryAccessors.put(propName,acc);
               }
            } else if ( (plength > 0) && (
                          (params[0].isInstance("string") && 
                           ((plength == 2) && name.equals("put"))) ||
                          ((plength == 1) && name.equals("get"))))
            {
               // hashtable get/put
               if (hashAccessor != null) {
                  if (_debug) {
                     _log.debug("Updating hash accessor: " + meth);
                  }
                  hashAccessor.addMethod(meth,params);
               } else {
                  if (_debug) {
                     _log.debug("Creating a new hash accessor: " + meth);
                  }
                  hashAccessor = new BinaryMethodAccessor(propName,meth,params);
               }
            } else if ((plength > 0) && (params[0].isInstance("string")) &&
                       (((plength == 1) && name.startsWith("get")) ||
                        ((plength == 2) && name.startsWith("set"))))
            {
               // binary get/set method
               acc = (Accessor) _binaryAccessors.get(propName);
               if (acc != null) {
                  if (_debug) {
                     _log.debug("Updating binary accessor: " + meth);
                  }
                  ((MethodAccessor) acc).addMethod(meth,params);
               } else {
                  if (_debug) {
                     _log.debug("Creating a new binary accessor: " + meth);
                  }
                  acc = new BinaryMethodAccessor(propName,meth,params);
                  _binaryAccessors.put(propName,acc);
               }
            }
         } else if (name.equals("elements") || 
                    name.equals("enumeration") ||
                    name.equals("iterator") ||
                    name.equals("toArray")) 
         {
            if (params.length == 0) {
               Class returnType = meth.getReturnType();

               // iterator supercedes enumeration supercedes Object[]
               System.out.println("Return   CLASS: " + returnType);
               System.out.println("Iterator CLASS: " + Iterator.class);
               Class iterClass = Iterator.class;
               boolean iterA = iterClass.isAssignableFrom(returnType);
               if (
                    iterA ||
                    (((iteratorMethod == null) || 
                       iteratorMethod.getName().equals("toArray")) &&
                     Object[].class.isAssignableFrom(returnType) ||
                       Enumeration.class.isAssignableFrom(returnType)))
               {
                  if (_debug) {
                     _log.debug("Setting iterator method: " + meth);
                  }
                  iteratorMethod = meth;
               }

            }
         }
      }
   }


   /**
     * Locate the requested property as follows: beginning from instance,
     * look at names[start] and resolve it, and continue resolving names 
     * recursively until names[end] has been resolved. Return that.
     * @param instance object to start from 
     * @param names the property names we are searching for 
     * @param start which name to look for first 
     * @param end which name to look for lst
     * @exception PropertyException error resolving a name 
     * @exception NoSuchMethodException no method available for name
     * @return the property requested
     *
     */
   private Object getProperty(
         final Object context, final Object instance, final Object[] names, 
            int start, int end) 
      throws PropertyException, NoSuchMethodException, InvalidContextException
   {

      if (_debug) {
         _log.debug("getProperty(" + instance + "," + names[start] + "..."
               + names[end] + "," + start + "," + end + ")");
      }

      String prop;
      Object nextProp = null;
      Accessor acc = null;

      if (names[start] instanceof String) {
         prop = (String) names[start];
      } else if (names[start] instanceof PropertyMethod) {
         PropertyMethod pm = (PropertyMethod) names[start];
         prop = pm.getName();
         acc = (Accessor) _directAccessors.get(prop);
         Object[] args = pm.getArguments(context);

         try {
            nextProp = acc.get(instance,args);
            start++;
         } catch (NoSuchMethodException e) {
            throw new NoSuchMethodException("No method " + pm + " on object " + instance);
         }

      } else {
         prop = names[start].toString();
      }

      // unary?
      if (acc == null) {
         acc = (Accessor) _unaryAccessors.get(prop);
         if (acc != null) {

            if (_debug) {
               _log.debug("Trying unary accesor: " + acc);
            }

            try {
               nextProp = acc.get(instance);
               start++;
            } catch (NoSuchMethodException e) { 
               if (_debug) {
                  _log.debug("No suitable unary get in " + acc);
               }
               acc = null;
            }
         }
      } 

      // binary?
      if (acc == null) {
         acc = (Accessor) _binaryAccessors.get(prop);
         if ((acc != null) && ( (start+1) <= end) ) {
            if (_debug) {
               _log.debug("Trying binary accesor: " + acc);
            }
            try {
               nextProp = acc.get(instance, (String) names[start + 1]);
               start += 2; 
            } catch (NoSuchMethodException e) {
               if (_debug) {
                  _log.debug("No suitable binary get in " + acc);
               }
               acc = null;
            } catch (ClassCastException e) {
               // names[start + 1] was not a String, just move on
               // this catch is more efficient than using instanceof
               // since 90% of the time it really will be a string
               acc = null;
            }
         }  else {
            acc = null;
         }
      } 

      // hash?
      if (acc == null) {
         acc = hashAccessor;
         try {
            if (acc != null) {
               if (_debug) {
                  _log.debug("Trying hash accessor=" + acc +
                        " with prop=" + prop);
               }
               nextProp = acc.get(instance,prop);
               start++;
               if (_debug) {
                  _log.debug("Got: " + nextProp);
               }
            }
         } catch (NoSuchMethodException e) {
            if (_debug) {
               _log.debug("No suitable hash get in " + acc);
            }
            acc = null;
         }
      } 
      
      if (acc == null) {
         throw new NoSuchMethodException("No public method on object " + 
               instance + " of " + instance.getClass() + 
               " for property " + names[start] + "--is this the right class?");
      }

      if (_debug) {
         _log.debug("Using accessor: " + acc);
      }
      if (start <= end) {
         try {
           return getOperator(nextProp.getClass()).getProperty(context,nextProp,names,start,end);
         } catch (NullPointerException e) {
            throw new PropertyException("No way to access property " + 
                  names[start] + " on object " + instance + " of " 
                  + instance.getClass() + "--possibly null?",e);
         }
      } else {
         return nextProp;
      }
   }

   /**
     * This method behaves a lot like getProperty, but it's tricker. It 
     * first tries to resolve the property using a direct method, then 
     * it falls back and tries a binary approach last. In order to do 
     * this it has to recurse into the direct approach, then detect if 
     * that failed and try the binary approach. It relies on getProperty
     * for navigation, which is why getProperty takes start/end args.
     * @param instance the object to start from
     * @names path to a property we would like to set 
     * @value the value we'd like to set it to 
     * @pos   we could set names[pos] from here
     * @return true if we succeeded in setting, false otherwise
     */
   private boolean setProperty(
         Object context, Object instance, Object[] names, Object value, int pos) 
      throws PropertyException, NoSuchMethodException, InvalidContextException
   {
      if (_debug) {
         _log.debug("setProperty(" + instance + "," + names[pos] + "..."
               + "," + value + "," + pos + ")");
      }

      // names[pos] is what we could set from here

      int parentPos = names.length - 1;
      int binPos = parentPos - 1;

      // if we're not yet at the binary-settable parent, go there
      if (pos < binPos) {
         Object grandparent = getProperty(context,instance,names,pos,binPos - 1);
         if (_debug) {
            _log.debug("Advanced to " + names[binPos] + "=" + grandparent);
         }
         PropertyOperator po = getOperator(grandparent.getClass());
         return po.setProperty(context,grandparent,names,value,binPos);
      } 

      // if we're at the binary-settable parent, try direct first
      if (pos == binPos) {

         if (_debug) {
            _log.debug("I am " + names[pos] + "(" + instance + ")" +
                  " could be binary, but first recurse to try unary access for " + names[pos]);
         }

         // try direct -- move to direct parent and try from there
         Object parent = null;
         try {
            parent = getProperty(context,instance,names,pos,pos);
            if (parent != null) {
               PropertyOperator po = getOperator(parent.getClass());
               if (po.setProperty(context,parent,names,value,pos+1)) {
                  return true;
               }
            }
         } catch (NoSuchMethodException e) {
            // oh well, keep trying: XXX this makes binOp expensive
         }

         // if direct failed, try binary
         if (_debug) {
            _log.debug("I am " + names[pos] + "(" + instance + ")" +
                   "direct failed, try bin for " + names[pos] + "." + names[pos + 1]);
         }
         Accessor binOp = (Accessor) _binaryAccessors.get(names[pos]);
         if (binOp != null) {
            if (_debug) {
               _log.debug("Found binOp accessor: " + binOp);
            }
            try {
               return binOp.set(instance,(String) names[pos+1],value);
            } catch (ClassCastException e) {
               if (_debug) {
                  _log.debug("Binary Failed because " + names[pos + 1] + " was not a string; it is " + names[pos + 1].getClass());
               }
               // names[pos+1] was not a string, just move on
               return false;
            } catch (NoSuchMethodException e) {
               if (_debug) {
                  _log.debug("Binary Failed: " + e);
               }
               return false;
            }
         } else {
            if (_debug) {
               _log.debug("No binary accessor for " + names[pos]);
            }
         }
         return false;
      }

      // we're the direct parent, use unaryOp or hash method
      if (_debug) {
         _log.debug("I am " + names[pos] + "(" + instance + ")"
               + "trying direct");
      }
      Accessor unaryOp = (Accessor) _unaryAccessors.get(names[pos]);
      try {
         if ((unaryOp != null) && unaryOp.set(instance,value)) {
            return true;
         }
         if (hashAccessor != null) {
            return hashAccessor.set(instance,(String) names[pos],value);
         }
      } catch(NoSuchMethodException e) {
         // fall through
      } catch(ClassCastException e) {
         // names[pos] was not a string, fall through
      }
      return false;
   }

   /**
     * Introspect the current object and return an Iterator representation
     * of it, if possible.
     * @param instance the object we think contains a list
     * @exception PropertyException current object is not any sort of list
     * @return an iterator representing the current object's list
     */
   private Iterator findIterator(Object instance)
      throws PropertyException
   {
      if (instance instanceof Object[]) {
         return new ArrayIterator((Object[])instance);
      } else if (instance instanceof Iterator) {
         return (Iterator) instance;
      } else if (instance instanceof Enumeration) {
         return new EnumIterator((Enumeration)instance);
      } else if (iteratorMethod != null) {
         try {
               Object ret = invoke(iteratorMethod,instance,null);
            if (ret instanceof Iterator) {
               return (Iterator) ret;
            } else if (ret instanceof Enumeration) {
               return new EnumIterator((Enumeration) ret);
            } else if (ret instanceof Object[]) {
               return new ArrayIterator((Object[]) ret);
            }
         } catch (NoSuchMethodException e) {
            throw new PropertyException("Error in PropertyOperator!",e);
         }
      }
      throw new PropertyException(instance + " is not a list",null);
   }

   /**
     * Invoke a method on an instance, with arguments--generate 
     * PropertyException rather than the default Java exceptions.
     * @param meth the method to invoke
     * @param instance the object to invoke it on
     * @param args arguments for the method
     * @return return value of the method
     */
   static Object invoke(Method meth, Object instance, Object[] args)
      throws PropertyException, NoSuchMethodException
   {
      try {
         return meth.invoke(instance,args);
      } catch (IllegalAccessException e) {
         throw new PropertyException(
            "You don't have permission to access the requested method (" +
            meth + " in class " + instance.getClass() + 
            " on object " + instance + "). Private/protected/package access " +
            " values cannot be accessed via property introspection.",e);
      } catch (IllegalArgumentException e) {
         throw new PropertyException(
            "Some kind of error occurred processing your request: this " + 
            "indicates a failure in PropertyOperator.java that should be " +
            "reported: attempt to access method " + meth + " on object " +
            instance + " with " +args.length + " parameters " +
            " threw an exception: " + e,e);
      } catch (InvocationTargetException e) {
         throw new PropertyException(
            "Attempt to invoke method " + meth + " on object " 
            + instance + " of " + instance.getClass() + 
            " raised an exception: " + e.getTargetException(),
            e.getTargetException());
      } catch (NullPointerException e) {
         if (meth == null) {
            throw new NoSuchMethodException("Null method");
         }
         throw new PropertyException(
            "NullPointerException thrown from method " + meth +
            " on object " + instance + " -- most likely you have attempted " + 
            "to use an undefined value, or a failure in that method.",e);
      }
   }
}



// helper classes


/**
  * An accessor represents one particular operation that can be 
  * performed on one particular class: getting/setting a field, or 
  * getting/setting via a method.
  */
abstract class Accessor {

   final String _name;

   Accessor(String name) {
      _name = name;
   }

   final String  getName() {
      return _name;
   }

   public final String toString() {
      return "Accessor:" + _name;
   }
 
   /**
     * Unary get
     */
   Object get(Object instance) 
      throws PropertyException, NoSuchMethodException
   {
      throw new PropertyException("BUG in PropertyOperator.java!",null);
   }

   /**
     * Unary set
     */
   boolean set(Object instance, Object value) 
      throws PropertyException, NoSuchMethodException
   {
      throw new PropertyException("BUG in PropertyOperator.java!",null);
   }

   /**
     * Binary get
     */
   Object get(Object instance, String subName) 
      throws PropertyException, NoSuchMethodException
   {
      throw new PropertyException("BUG in PropertyOperator.java!",null);
   }

   /**
     * Binary
     */
   boolean set(Object instance, String subName, Object value)
      throws PropertyException, NoSuchMethodException
   {
      throw new PropertyException("BUG in PropertyOperator.java!",null);
   }


   /**
     * Direct get
     */
   Object get(Object instance, Object[] args)
      throws PropertyException, NoSuchMethodException
   {
      throw new PropertyException("BUG in PropertyOperator.java!",null);
   }

}


/**
  * An accessor that knows how to get/set from a field
  */
final class FieldAccessor extends Accessor
{
   final Field _field;

   FieldAccessor(final Field f) {
      super(f.getName());
      _field = f;
   }

   final Object get(final Object instance)
      throws PropertyException
   {
      try {
         return _field.get(instance);
      } catch (Exception e) {
         throw new PropertyException("Unable to read field " + _field +
               " on object " + instance + " of " + instance.getClass(),
               e);
      }
   }

   final boolean set(final Object instance, final Object value)
      throws PropertyException
   {
      try {
         _field.set(instance,value);
      } catch (Exception e) {
         throw new PropertyException("Unable to write field " + _field +
               " on object " + instance + " of " + instance.getClass(),
               e);
      }
      return true;
   }
}


/**
  * accessor for direct method calls, rather than property-style
  */
final class DirectAccessor extends Accessor
{

   Vector _methods = new Vector();

   DirectAccessor(final String name, final Method m, final Class[] params)
   {
      super(name);
      addMethod(m,params);
   }

   final void addMethod(final Method m, Class[] params)
   {
      for(int i = 0; i < _methods.size(); i++) {
         Method cur = (Method) _methods.elementAt(i);
         if ( precedes(cur.getParameterTypes(), params) ) {
            _methods.insertElementAt(m,i);
            return;
         }
      }
      _methods.addElement(m);
   }

   final boolean precedes(Class[] lhs, Class[] rhs)
   {
      if (lhs.length == rhs.length) {
         for (int i = 0; i < lhs.length; i++) {
            // lhs precedes rhs if lhs terms are more specific
            if (rhs[i].isAssignableFrom(lhs[i])) 
            {
               return false; // lhs is more general than rhs     
            }
         }
         return true; // all lhs terms same as or more specific than rhs
      } else {
         return (lhs.length < rhs.length);
      }
   }

   final boolean matches(Class[] sig, Class[] args)
   {
      if (args.length != sig.length) {
         return false;
      }

      for (int i = 0; i < sig.length; i++) {
         try {
            if (! sig[i].isAssignableFrom(args[i]))
            {
               if (sig[i].isPrimitive())
               {
                  Class s = sig[i];
                  Class a = args[i];
                  if (
                      (s.equals(Integer.TYPE) && a.equals(Integer.class)) ||
                      (s.equals(Boolean.TYPE) && a.equals(Boolean.class)) || 
                      (s.equals(Character.TYPE) && a.equals(Character.class)) ||  
                      (s.equals(Long.TYPE) && a.equals(Long.class)) ||  
                      (s.equals(Short.TYPE) && a.equals(Short.class)) ||  
                      (s.equals(Double.TYPE) && a.equals(Double.class)) ||  
                      (s.equals(Float.TYPE) && a.equals(Float.class)) ||  
                      (s.equals(Void.TYPE) && a.equals(Void.class)) ||  
                      (s.equals(Byte.TYPE) && a.equals(Byte.class)) 
                      )
                  {
                      continue;
                  }
               }
               if ( PropertyOperator._debug) 
               {
                  PropertyOperator._log.debug("method " + this + " failed to match because " + sig[i] + " required but got " + args[i]);
               }
               return false;
            }
         } catch (NullPointerException e) {
            return false; // XXX: block nulls, isAssign... throws this
         }
      }
      return true;
   }

   final Object get(Object instance, Object[] args)
      throws PropertyException, NoSuchMethodException
   {
      Class[] types = new Class[ args.length ];
      for (int i = 0; i < args.length; i++) {
         try {
            types[i] = args[i].getClass();
         } catch (NullPointerException e) {
            types[i] = null;
         }
      }

      for (int i = 0; i < _methods.size(); i++) {
         Method m = (Method) _methods.elementAt(i);
         Class[] sig = m.getParameterTypes();
         if (matches(sig,types)) {
            return PropertyOperator.invoke(m,instance,args);
         }
      }

      // not found

      StringBuffer msg = new StringBuffer();
      msg.append("No method ");
      msg.append(getName());
      msg.append("(");
      for (int i = 0; i < args.length; i++) {
         if (i > 0) {
            msg.append(",");
         }
         msg.append((args[i] == null) ? "null" : args[i].getClass().toString());
      }
      msg.append(") on object ");
      msg.append(instance);

      throw new PropertyException(msg.toString(),null);
   }
   
}


abstract class MethodAccessor extends Accessor
{
   Method _getMethod;           // only one get method allowed
   Method[] _setMethods = null; // may be multiple set methods
   Class[]  _setParams = null;  // variable arg type for set meth N
   int setCount = 0;            // how many set methods do we have

   abstract int numArgsGet();
   abstract int numArgsSet();

   MethodAccessor(final String name, final Method m, final Class[] params) 
      throws PropertyException
   {
      super(name);
      addMethod(m,params);
   }

   final void addMethod(final Method m, Class[] params) 
      throws PropertyException
   {

      final int setArgsLength = numArgsSet();
      final int getArgsLength = numArgsGet();

      if (params.length == getArgsLength) {
         _getMethod = m;
      } else if (params.length == setArgsLength) {
         setCount++;
         if (_setMethods == null) {
            _setMethods = new Method[1];
            _setParams = new Class[1];
         } else if (_setMethods.length <= setCount) {
            Method[] tmpMethods = new Method[ (setCount + 1) * 2 ];
            Class[] tmpParams  = new Class[(setCount + 1) * 2 ];
            System.arraycopy(_setMethods,0,tmpMethods,0,_setMethods.length);
            System.arraycopy(_setParams,0,tmpParams,0,_setParams.length);
            _setMethods = tmpMethods;
            _setParams = tmpParams;
         }

         // record the method, and the type of the variable parameter
         _setMethods[setCount - 1] = m;
         _setParams[setCount - 1] = params[setArgsLength - 1];

      } else {
         throw new PropertyException("PropertyOperator FAILED for method " 
               + m + "--please report this bug!",null);
      }
   }

   final Object getImpl(final Object instance, final Object[] args) 
      throws PropertyException, NoSuchMethodException
   {
      return PropertyOperator.invoke(_getMethod,instance,args);      
   }

   final boolean setImpl(final Object inst, final Object[] args) 
      throws PropertyException, NoSuchMethodException
   {
      //which method to use? check params for first match
      for (int i = 0; i < setCount; i++) {
         Object arg = args[args.length - 1];
         // XXX: null values are blocked by the next line
         if (_setParams[i].isInstance(args[args.length - 1])) {
            PropertyOperator.invoke(_setMethods[i],inst,args);
            return true;
         }
      }
      return false;
   }

}

final class UnaryMethodAccessor extends MethodAccessor
{

   UnaryMethodAccessor(final String name, final Method m, final Class[] params) 
      throws PropertyException
   {
      super(name,m,params);
   }

   final int numArgsGet() {
      return 0;
   }

   final int numArgsSet() {
      return 1;
   }

   final Object get(final Object instance)
      throws PropertyException, NoSuchMethodException
   {
      return getImpl(instance,null);
   }

   final boolean set(final Object instance, final Object value) 
      throws PropertyException, NoSuchMethodException
   {
      Object[] args = new Object[1];
      args[0] = value;
      return setImpl(instance,args);
   }

}

final class BinaryMethodAccessor extends MethodAccessor
{
   
   BinaryMethodAccessor(String name, Method m, Class[] params) 
      throws PropertyException
   {      
      super( name,m,params);
   }

   final int numArgsGet() {
      return 1;
   }

   final int numArgsSet() {
      return 2;
   }

   final Object get(final Object instance, String prop)
      throws PropertyException, NoSuchMethodException
   {
      Object[] args = new Object[1];
      args[0] = prop;
      return getImpl(instance,args);
   }

   final boolean set(final Object instance, String prop, Object value)
      throws PropertyException, NoSuchMethodException
   {
      Object[] args = new Object[2];
      args[0] = prop;
      args[1] = value;
      return setImpl(instance,args);
   }
}


