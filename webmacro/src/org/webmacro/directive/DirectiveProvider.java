package org.webmacro.directive;

import java.util.*;
import org.webmacro.engine.*;
import org.webmacro.*;
import org.webmacro.util.*;
import org.webmacro.broker.*;

/**
  * Utility class to assist in the creation of directives.
  */
public final class DirectiveProvider implements ResourceProvider
{


   // BULDER CLASS MANAGEMENT

   private final Hashtable _descriptors  = new Hashtable();
   private final static Log _log = new Log("dir", "Directive Provider");

   /**
     * Register a new directive class, so that a builder
     * of this type can be retrieved later.
     * @exception IntrospectionException something wrong with the class
     * @exception ResourceInitException duplicate registration
     */
   public final void register(String dirClassName) 
      throws IntrospectionException, ResourceInitException
   {
      Class directive = null;
      DirectiveDescriptor descriptor, oldDesc;
      try {
        directive = Class.forName(dirClassName);
      } catch (Exception e) {
         throw new IntrospectionException("No class " + dirClassName);
      }
      try {
        descriptor = (DirectiveDescriptor) 
          directive.getMethod("getDescriptor", null).invoke(null, null);
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
         throw new ResourceInitException(
               "Attempt to register directive " + directive
               + " failed because " + oldDesc.dirClass.getName() 
               + " is already registered for type " + descriptor.name);
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

   static final private String _types[] = { "new-directive" };

   public String[] getTypes() {
      return _types;
   }

   public void init(ResourceBroker broker) throws ResourceInitException
   {
      try {
         String directives = (String) broker.getValue("config","NewDirectives");
         Enumeration denum = new StringTokenizer(directives);
         while (denum.hasMoreElements()) {
            String dir = (String) denum.nextElement();
            try {
               register(dir);
            } catch (Exception ce) {
              //@@@Engine.log.exception(ce);
              //@@@Engine.log.error("Could not load directive: " + dir);    
            }
         }
      } catch (Exception e) {
         //@@@Engine.log.exception(e);
         throw new ResourceInitException("Could not init DirectiveProvider: "
               + e);
      }
   }

   public void destroy() 
   {
      _descriptors.clear();
   }

   public void resourceRequest(RequestResourceEvent req) 
      throws NotFoundException
   {
      try {
         req.set(getDescriptor(req.getName()));
      } catch (Exception e) {
         throw new NotFoundException("No such directive: " + req.getName() 
               + ":" + e.getMessage());
      }
   }

   public void resourceCreate(CreateResourceEvent create)
      throws NotFoundException, InterruptedException
   {
      // do nothing == unsupported
   }

   public boolean resourceDelete(ResourceEvent delete) {
      // do nothing == unsupported
      return false;
   }

   public boolean resourceSave(ResourceEvent save) {
      return false;
   }

   public int resourceThreads() { return 0; }
   public int resourceExpireTime() { return NEVER_CACHE; }
}


