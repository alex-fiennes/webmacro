/*
 * TypeDirective.java
 *
 * Created on June 13, 2001, 12:22 AM
 */

package org.webmacro.directive;

import java.io.*;
import java.util.*;

import org.webmacro.*;
import org.webmacro.engine.*;
import org.webmacro.util.*;

/**
 * TypeDirective allows the programmer (or template designer) to ensure
 * objects placed into the <code>Context</code> are of the required class
 * type.<p>
 *
 * Syntax:<pre>
 *    #type var-reference quoted-string [ required ]
 * </pre>
 *
 * Examples:<pre>
 *    $MyName, if it exists in the context, <b>must</b> be a java.lang.String
 *    #type $MyName "java.lang.String"
 *
 *    $Today <b>must</b> exist in the context and <b>must</b> be a java.util.Date
 *    #type $Today "java.util.Date" required
 *
 *    $Addresses, if it exists in the context, <b>must</b> a org.mycompany.til.Address array
 *    #type $Addresses "org.mycompany.util.Address[]"
 * </pre>
 *
 * TypeDirective simply ensures the class type of the <code>$Variable</code>
 * <code>isAssignableFrom</code> the specified classname.<p>
 *
 * If the variable is not flagged as being <i>required</i>, then its class type
 * is only checked if it exists in the context.  By default, variables are not
 * required to be in the context.<p>
 *
 * Special support for checking that a variable is an object array exists.
 * Simply append matching square brackets to the end of the classname.<p>
 *
 * If a Variable is <b>not</b> of the specified type, TypeDirective will
 * throw a <code>org.webmacro.directive.TypeDirective.InvalidTypeException</code>,
 * which one can catch in their servlet code if necessary.<p>
 *
 * TypeDirective is <b>enabled by default</b>, however, it can be disabled 
 * via your custom <code>WebMacro.properties</code> file:<pre>
 *    TypeDirective.Enabled = true | false
 * </pre>
 * 
 * @author  <a href=mailto:ebr@tcdi.com>Eric B. Ridge</a>
 * @version 1.0
 * @since post 0.97
 */
public class TypeDirective extends Directive {

    public static final int TYPE_OBJECT = 0;
    public static final int TYPE_CLASSNAME = 1;
    public static final int TYPE_REQUIRED = 2;
    
    public static final ArgDescriptor[] _args = new ArgDescriptor[] {
        new RValueArg (TYPE_OBJECT),
        new QuotedStringArg (TYPE_CLASSNAME),
        new OptionalGroup (1),
            new KeywordArg (TYPE_REQUIRED, "required"),
    };
    
    public static final DirectiveDescriptor _dd = 
                 new DirectiveDescriptor ("type", null, _args, null);
    
    /**
     * static method required by the WebMacro parser to provide
     * a descriptor about this directive
     */
    public static DirectiveDescriptor getDescriptor () {
        return _dd;
    }

    /**
     * Exception thrown when a Variable isn't of the specified class type
     */
    public static class InvalidTypeException extends PropertyException {
        public InvalidTypeException (Variable v, Class clazz) { 
            super ("$" + v.getName() + " is not a " + clazz.getName()); 
        }
    }
    
    
    /** cache of classnames and their class instances. */
    private static final Map _classCache = Collections
                                              .synchronizedMap (new HashMap ());
    
    /** has this directive been configured via the .properties file? */
    private static volatile boolean _configured = false;
    
    /** is this directive enabled?  In other words, should it actually perform
     * the type checking? */
    private static volatile boolean _enabled = true;

    
    /** the Context object we need to check the type of */
    private Variable _object;

    /** the Class instance that _object is requried to be */
    private Class _class;
    
    /** is the Variable required to be in the Context? */
    private boolean _required;
    
    /**
     * configure directive for this run and return 'this'
     */
    public Object build (DirectiveBuilder builder,  BuildContext bc) throws BuildException {
        // we're not enabled, so don't do anything
        if (!_enabled)
            return null;

        String classname;

        classname = (String) builder.getArg (TYPE_CLASSNAME, bc);
        _object = (Variable) builder.getArg (TYPE_OBJECT, bc);
        _required = builder.getArg (TYPE_REQUIRED, bc) != null;
        
        try {
            _class = TypeDirective.getClass (classname);
        } catch (ClassNotFoundException cnfe) {
            throw new BuildException ("TypeDirective cannot find the class " 
                                    + "/" + classname + "/");
        }

        return this;
    }   
    
    /**
     * Ensure the class of the specified Variable reference 
     * <code>isAssignableFrom</code> from the classname arg.
     *
     * @return <code>null</code>, always
     * @throws <code>PropertyException.NoSuchVariableException</code> if the
     *         specified variable arg evaluates to null
     * @throws <code>TypeDirective.InvalidTypeException</code> if the
     *         specified Variable's class is not assignable from the
     *         specified classname arg
     */
    public Object evaluate (Context context) throws PropertyException {
        if (!_configured) {
            TypeDirective.configure (context.getBroker ());
            
            if (!_enabled) // configure says we're not enabled
                return null;
        }
        
        Object o = _object;
        
        // evaluate the _object reference down to it's base object
        while (o instanceof Macro) 
            o = ((Macro) o).evaluate (context);
            
        if (o == null) {
            // the Variable to check isn't in the Context.
            if (_required) {
                // but it should be
                 throw new PropertyException
                                   .NoSuchVariableException (_object.getName());
            } else {
                // but it's not required to be there, so get out now
                // can't check the type of a null object
                return null;
            }
        }
        
        // check it and throw if requried class isn't compatible
        // with class of specified object
        if (!_class.isAssignableFrom (o.getClass()))
            throw new InvalidTypeException (_object, _class);

        return null;
    }
    
    /**
     * The #type directive does not produce output
     */
    public void write (FastWriter fw, Context context) throws IOException, PropertyException {
        evaluate (context);
    }
    
    public void accept(TemplateVisitor v) {
        v.beginDirective(_dd.name);
        v.visitDirectiveArg("TypeContextObject", _object);
        v.visitDirectiveArg("TypeClassname", _class.getName());
        v.endDirective();
    }    
    
    //
    // private, static methods
    //
    
    /**
     * configure this directive using the properties available from the
     * specified Broker.  By default, we are <b>enabled</b>, even if
     * the configuration key doesn't exist.
     */
    private static final void configure (Broker broker) {
        Settings s = broker.getSettings ();
        _enabled = s.getBooleanSetting ("TypeDirective.Enabled", true);
        _configured = true;
    }
    
    /** 
     * helper class for keeping our cache of classes current
     */
    private static final Class getClass (final String classname) throws ClassNotFoundException {
        Class clazz = (Class) _classCache.get (classname);
        
        if (clazz == null) {
            // not in cache, so load it and cache it
            // taking into consideration our special syntax for
            // an array of the class
            if (classname.endsWith ("[]")) {
                // wants to check an array
                String newName = classname.substring (0, classname.length()-2);
                clazz = Class.forName (newName);
                clazz = java.lang.reflect.Array
                                         .newInstance (clazz, 0).getClass();
            } else {
                // wants to check a normal object
                clazz = Class.forName (classname);
            }
            
            _classCache.put (classname, clazz);
        }

        return clazz;
    }
    
}
