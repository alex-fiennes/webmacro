package org.webmacro.directive;

import java.util.*;
import org.webmacro.engine.*;
import org.webmacro.*;
import org.webmacro.util.*;
import org.webmacro.resource.*;

/**
  * Utility class to assist in the creation of directives.
  */
public final class DirectiveProvider implements Provider
{

   // BULDER CLASS MANAGEMENT

   private final Hashtable _descriptors  = new Hashtable();
   private Log _log;

   /**
     * Register a new directive class, so that a builder
     * of this type can be retrieved later.
     * @exception IntrospectionException something wrong with the class
     * @exception InitException duplicate registration
     */
   public final void register(String dirClassName) 
      throws IntrospectionException, InitException
   {
      Class directive = null;
      DirectiveDescriptor descriptor, oldDesc;
      try {
        directive = Class.forName(dirClassName);
      } catch (Exception e) {
         throw new IntrospectionException("No class " + dirClassName);
      }

      // Make sure this class is an instance of o.w.directive.Directive
      if (Directive.class.isAssignableFrom(directive)) {
        try {
          descriptor = (DirectiveDescriptor) 
            directive.getMethod("getDescriptor", null).invoke(null, null);
          if (descriptor.dirClass == null)
            descriptor.dirClass = directive;
        } 
        catch (Exception e) {
          throw new IntrospectionException("Class " + dirClassName 
            + " does not have a getDescriptor() method");
        }
        oldDesc = (DirectiveDescriptor) 
          _descriptors.get(descriptor.name);
        if (oldDesc == null) {
          _descriptors.put(descriptor.name, descriptor);
          _log.info("Registered directive: " + descriptor.name);
        } else if (descriptor.dirClass != oldDesc.dirClass) {
          throw new InitException("Attempt to register directive " + directive
             + " failed because " + oldDesc.dirClass.getName() 
             + " is already registered for type " + descriptor.name);
        }
      }
   }

   /**
     * Create a builder for the named directive
     */
   public final DirectiveDescriptor getDescriptor(String directiveName)
      throws ClassNotFoundException
   {
      DirectiveDescriptor descriptor = 
         (DirectiveDescriptor) _descriptors.get(directiveName);
      if (descriptor == null) {
        throw new ClassNotFoundException(
               "No directive matched the name " + directiveName);
      }
      return descriptor;
   }



   // RESOURCE PROVIDER API

   public String getType() {
      return "org.webmacro.directive.Directive";
   }

   public void init(Broker broker, Settings config) throws InitException
   {
      _log = broker.getLog("directive");
      try {
         String directives = config.getSetting("Directives");
         Enumeration denum = new StringTokenizer(directives);
         while (denum.hasMoreElements()) {
            String dir = (String) denum.nextElement();
            try {
               register(dir);
            } catch (Exception ce) {
              System.out.println(ce.toString());
              //@@@Engine.log.exception(ce);
              //@@@Engine.log.error("Could not load directive: " + dir);    
            }
         }
      } catch (Exception e) {
         //@@@Engine.log.exception(e);
         throw new InitException("Could not init DirectiveProvider: "
               + e);
      }
   }

   public void destroy() 
   {
      _descriptors.clear();
   }

   public Object get(String name) throws NotFoundException
   {
      try {
         return getDescriptor(name);
      } catch (Exception e) {
         throw new NotFoundException("No such directive: " + name 
               + ":" + e.getMessage());
      }
   }

   public void flush() { }
}


