
package org.webmacro.engine;
import org.webmacro.engine.*;
import java.util.*;
import java.lang.reflect.*;
import org.webmacro.*;
import org.webmacro.util.*;
import org.webmacro.broker.*;

/**
  * Utility class to assist in the creation of directives.
  */
public final class DirectiveProvider implements ResourceProvider
{


   // BULDER CLASS MANAGEMENT

   private final Hashtable _prototypes  = new Hashtable();
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

      String dirName = extractName(dirClassName);
      Class directive = null;
      try {
         directive = Class.forName(dirClassName);
      } catch (Exception e) {
         throw new IntrospectionException("No class " + dirClassName);
      }
      DirectiveBuilder b = (DirectiveBuilder) _prototypes.get(dirName);
      if (b == null) {
         b = new DirectiveBuilder(dirName, directive);
         _prototypes.put(dirName, b);
         _log.info("Registered directive: " + dirName);
      } else if (! directive.equals(b.getDirectiveClass())) {
         throw new ResourceInitException(
               "Attempt to register directive " + directive
               + " failed because " + b.getDirectiveClass() 
               + " is already registered for type " + dirName);
      }
   }

   private static String extractName(String dir) 
      throws IntrospectionException
   {
      if (! dir.endsWith("Directive") ) {
         throw new IntrospectionException(
            "Malformed classname, must end with Directive");
      }
      int end = dir.length() - 9;
      int start = dir.lastIndexOf('.',end) + 1;
      String dirName = dir.substring(start,end);
      if (dir.startsWith("org.webmacro.")) {
         dirName = dirName.toLowerCase();
      }
      return dirName;
   }


   /**
     * Create a builder for the named directive
     */
   public final DirectiveBuilder getBuilder(String directiveName)
      throws ClassNotFoundException
   {
      DirectiveBuilder proto = 
         (DirectiveBuilder) _prototypes.get(directiveName);
      if (null == proto) {
        throw new ClassNotFoundException(
               "No directive matched the name " + directiveName);
      }
      try {
         return (DirectiveBuilder) proto.clone();
      } catch(Exception ignore) {
         return null;
      }
   }



   // RESOURCE PROVIDER API

   static final private String _types[] = { "directive" };

   public String[] getTypes() {
      return _types;
   }

   public void init(ResourceBroker broker) throws ResourceInitException
   {
      try {
         String directives = (String) broker.getValue("config","Directives");
         Enumeration denum = new StringTokenizer(directives);
         while (denum.hasMoreElements()) {
            String dir = (String) denum.nextElement();
            try {
               register(dir);
            } catch (Exception ce) {
               Engine.log.exception(ce);
               Engine.log.error("Could not load directive: " + dir);    
            }
         }
      } catch (Exception e) {
         Engine.log.exception(e);
         throw new ResourceInitException("Could not init DirectiveProvider: "
               + e);
      }
   }

   public void destroy() 
   {
      _prototypes.clear();
   }

   public void resourceRequest(RequestResourceEvent req) 
      throws NotFoundException
   {
      try {
         req.set(getBuilder(req.getName()));
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


