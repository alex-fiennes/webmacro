
package org.webmacro.engine;
import org.webmacro.engine.*;
import java.util.*;
import java.lang.reflect.*;
import org.webmacro.*;

/**
  * Utility class to assist in the creation of directives.
  */
final class DirectiveBuilder implements Cloneable, Builder 
{

   private static final Class[] _nullSig = new Class[0]; // utility
   private static final Object[] _nullArg = new Object[0]; // utility


   // PROTOTYPE PROPERTIES

   private String  _verb = null;
   private String[]  _dependents = null;
   private boolean _isContainer = false;
   private boolean _hasCondition = false;
   private boolean _hasTarget = false;
   private boolean _isSubDirective;
   private boolean _isParser = false;
   private Method _build = null;
   private Method _marker = null;
   private int _buildArgs = 0;
   private final String _name;
   private Class _directiveClass;

   /**
     * Construct a new DirectiveBuilder prototype
     */
   DirectiveBuilder(String name, Class directive)
      throws IntrospectionException
   {

      boolean hasPredicate = false;
      boolean isMulti = false;

      _name = name;
      _directiveClass = directive;

      // analyze the build factory method
  
      if (! Directive.class.isAssignableFrom(directive)) {
         throw new IntrospectionException("Not a Directive: " + directive);
      }

      _isSubDirective = SubDirective.class.isAssignableFrom(directive);
 
      Method[] meths = directive.getMethods();
      for (int i = 0; i < meths.length; i++) {
         int mod = meths[i].getModifiers();
         if (Modifier.isPublic(mod) && Modifier.isStatic(mod)
               && meths[i].getName().equals("build")) 
         {
            if (_build == null) {
               _build = meths[i];
            } else {
               throw new IntrospectionException(
                     "Directives must have only one build() method.");
            }
         }
      }
      if (_build == null) {
         throw new IntrospectionException(
               "Directives must have a public static build() method");
      }

      Class[] params = _build.getParameterTypes();
      _buildArgs = params.length;

      if (params.length < 1) {
         throw new IntrospectionException(
             "Directive.build() method must have at least one arguments"); 
      }
  
      int arg = 0;
      if (params[arg++] != BuildContext.class) {
         throw new IntrospectionException("Directives must have a build "
               + "method whose first argument is a BuildContext");
      }

      if (params[arg] == Condition.class) {
         arg++;
         _hasCondition = true;
      } else if (params[arg] == Object.class) {
         arg++;
         _hasTarget = true;
         if ((arg < params.length) && (params[arg] == Object.class)) {
            arg++;
            hasPredicate = true;
         }
      }

      if ((arg < params.length) && (params[arg] == Macro.class)) {
         arg++;
         _isContainer = true; 
      } else if ((arg < params.length) && (params[arg] == String.class)) {
         arg++;
         _isParser = true;
         try {
            _marker = directive.getMethod("getMarker",_markerArgs);
         } catch (NoSuchMethodException nsm) {
            throw new IntrospectionException("Directive claims to be parser "
                  + " but does not have a getMarker() method.");
         }
      }

      if ((arg < params.length) && (params[arg] == Object.class)) {
         arg++;
         isMulti = true;
      }

      if (arg < params.length) { 
         throw new IntrospectionException("Class " + directive 
             + " build method does not match the directive pattern. "
             + "The parameter " + params[arg] + " was unexpected.");
      }
   

      // extract additional values

      if (hasPredicate) {
         // extract verb
         try {
            Method m = directive.getMethod("getVerb",_nullSig);
            _verb = (String) m.invoke(null,_nullArg);
         } catch (Exception e) {
            throw new IntrospectionException("Directive " + directive +
                  " build() requires a source object, but the getVerb " +
                  " method failed: " + e);
         }
         if (_verb == null) {
            throw new IntrospectionException("getVerb returned a null.");
         }
      }

      if (isMulti) {
         // extract subName
         try {
            Method m = directive.getMethod("getSubDirectives",_nullSig);
            _dependents = (String[]) m.invoke(null,_nullArg);
         } catch (Exception e) {
            throw new IntrospectionException("Directive " + directive +
                  " build() accepts a dependent directive, but the " +
                  " getSubDirectives method failed: " + e);
         }
         if (_dependents == null) {
            throw new IntrospectionException(
                  "getSubDirectives returned a null.");
         }
      }
   }

   public final Object clone() { 
      try {
         return super.clone(); 
      } catch (CloneNotSupportedException cnse) {
         throw new InternalError("Clone not supported on cloneable object!");
      }
   }

   public final Class getDirectiveClass() { return _directiveClass; }
   public final String  getVerb() { return _verb; }
   public final String[] getSubDirectives() { return _dependents; }
   public final boolean hasCondition() { return _hasCondition; }
   public final boolean hasTarget() { return _hasTarget; }
   public final boolean hasPredicate() { return (_verb != null); }
   public final boolean isContainer() { return _isContainer; }
   public final boolean isParser() { return _isParser; }
   public final boolean isMulti() { return (_dependents != null); }
   public final boolean isSubDirective() { return (_isSubDirective); }
   

   public final String getMarker() throws BuildException
   {
      if (_marker == null) {
         throw new BuildException("Directive does not implement getMarker()");
      }
      Object arg[] = new Object[2];

      arg[0] = _target;
      arg[1] = _predicate;
      try {
         return (String) _marker.invoke(null, arg);
      } catch (IllegalAccessException ie) {
         throw new BuildException("Your getMarker() method could not be " 
               + "executed due to invalid permissions. Both the class and "  
               + "the method must be public: " + ie);
      } catch (InvocationTargetException it) {
         Throwable te = it.getTargetException();
         Engine.log.exception(te);
         throw new BuildException("Your getMarker() method failed: " + te);
      }
   }

   // INSTANCE PROPERTIES
   
   private Builder  _contents = null;
   private String  _text = null;
   private Object _dependent = null;
   private Object  _target = null;
   private Object _condition = null;
   private Object  _predicate = null;
   static final private Class[] _markerArgs = { Object.class, Object.class };

   public final void setTarget(Object term) 
      throws IllegalStateException
   {
      if ( hasCondition() ) {
         throw new IllegalStateException("Directive should not have a target");
      }
      _target = term;
   }

   public final void setCondition(Object cond) 
      throws IllegalStateException
   {
      if (! hasCondition() ) {
         throw new IllegalStateException("Directive should not have a condition");
      }
      _condition = cond;
   }

   public final void setSubDirective(Object dep) 
      throws IllegalStateException
   {
      if (! isMulti() ) {
         throw new IllegalStateException("Directive should not have a dependent");
      }
      _dependent = dep;
   }

   public final void setPredicate(Object term) 
      throws IllegalStateException
   {
      if (! hasPredicate() ) {
         throw new IllegalStateException("Directive should not have a predicate");
      }
      _predicate = term;
   }

   public final void setContents(Builder contents)
   {
      if (! isContainer() ) {
         throw new IllegalStateException("Directive should not have contents");
      }
      _contents = contents;
   }

   public final void setText(String text)
   {
      if (! isParser() ) {
         throw new IllegalStateException("Directive is not a parser");
      }
      _text  = text;
   }


   // CHECK REQUIRED FIELDS

   public final void check()
      throws BuildException
   {
      check("condition", hasCondition(), _condition != null);
      check("target", hasTarget(), _target != null);
      check("predicate", hasPredicate(), _predicate != null);
      check("contents", isContainer(), _contents != null);
   }

   private void check(String name, boolean wanted, boolean present)
      throws BuildException
   {
      if (wanted && !present) {
         throw new BuildException("Missing " + name + ": " + this);
      }
   }



   // CREATE REAL DIRECTIVE

   public final Object build(BuildContext rc) 
      throws BuildException
   {

      // collect the arguments

      Object args[] = new Object[_buildArgs];
      {
         int i = 0;
         args[i++] = rc;
         if ( hasCondition() ) {
            Object cond = (_condition instanceof Builder) ?
               ((Builder) _condition).build(rc) : _condition;
            if (cond instanceof Condition) {
               args[i++] = cond;
            } else {
               args[i++] = new ConstantCondition(cond);
            }
         }
         if ( hasTarget() ) {
            args[i++] = _target;
         }

         if ( hasPredicate() ) {
            args[i++] = _predicate;
         }
         if ( isContainer() ) {
            args[i++] = _contents;
         } 
         if ( isParser() ) {
            args[i++] = _text;
         }
         if ( isMulti() ) {
            args[i++] = _dependent;
         }
      }

      // build the arguments

      for (int i = 0; i < args.length; i++) {
         Object arg = args[i];
         if ((arg != null) && (arg instanceof Builder)) {
            args[i] = ((Builder) arg).build(rc);
         }
      }

      // build the directive

      Object self;
      try {
        self = _build.invoke(null,args); 
      } catch (InvocationTargetException e) {
         Throwable t = e.getTargetException();
         if (t instanceof BuildException) {
            throw (BuildException) t;
         } else {
            throw new BuildException("dependent: " + e);
         }
      } catch (Exception e) {
         throw new BuildException("dependent: " + e);
      }
      return self;
   }

   public final String toString() {
      String base = "#" + _name + " "
         + (hasCondition() ? "(CONDITION) " : "")
         + (hasTarget() ? "TARGET " : "")
         + (hasPredicate() ? (getVerb() + " SOURCE ") :"")
         + (isContainer() ? "{ ... } " : "");
      if (isMulti()) {
         StringBuffer buf = new StringBuffer();
         for (int i = 0; i < _dependents.length; i++) {
            buf.append(base);
            buf.append("#");
            buf.append(_dependents[i]);
            buf.append(" ...\n");
         }
         return buf.toString();
      } else {
         return base;
      }
   }
}

