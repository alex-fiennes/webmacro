
package org.webmacro.engine;

import java.util.*;
import java.lang.reflect.*;
import org.webmacro.*;
import org.webmacro.resource.*;
import org.webmacro.util.*;

/**
  * Utility class to assist in the creation of directives.
  */
public final class ParserProvider implements Provider
{


   // BULDER CLASS MANAGEMENT

   private static final Hashtable _parsers  = new Hashtable();

   private final Class[] _brokerParam = { Broker.class };
   private final Object[] _brokerArg = new Object[1];

   /**
     * Register a new parser class,
     * @exception IntrospectionException something wrong with the class
     * @exception InitException duplicate registration
     */
   public final void register(String pClassName) 
      throws IntrospectionException, InitException
   {

      String pname = extractName(pClassName);
      Class pclass = null;
      try {
         pclass = Class.forName(pClassName);
      } catch (Exception e) {
         throw new IntrospectionException("No class " + pClassName);
      }
      try {
         Parser p = (Parser) _parsers.get(pname);
         if (p == null) {
            Constructor ctor = pclass.getConstructor(_brokerParam);
            p = (Parser) ctor.newInstance(_brokerArg);
            _parsers.put(pname, p);
         } else if (! pclass.equals(p.getClass())) {
            throw new InitException(
                  "Attempt to register parser " + pClassName
                  + " failed because " + p.getClass() 
                  + " is already registered for type " + pname);
         }
      } catch (InstantiationException ne) {
        throw new IntrospectionException("Parsers could not be instantiated", 
                                         ne);
      } catch (IllegalAccessException ia) {
        throw new IntrospectionException("Parser class must be public", ia);
      } catch (InvocationTargetException it) {
        throw new InitException("Parser threw an exception", it);
      } catch (NoSuchMethodException nm) {
         throw new IntrospectionException(
               "Parser missing the required constructor", nm);
      }
   }

   public final Parser getParser(String pname) 
      throws NotFoundException
   {
      Parser p = (Parser) _parsers.get(pname);
      if (p == null) {
         throw new NotFoundException("No parser registered for type " 
               + pname);
      }
      return p;
   }

   private static String extractName(String par) 
      throws IntrospectionException
   {
      if (! par.endsWith("Parser") ) {
         throw new IntrospectionException(
            "Malformed classname (" + par + "), must end with Parser");
      }
      int end = par.length() - 6;
      int start = par.lastIndexOf('.',end) + 1;
      String parName = par.substring(start,end);
      if (par.startsWith("org.webmacro.")) {
         parName = parName.toLowerCase();
      }
      return parName;
   }


   public String getType() {
      return "parser";
   }

   public void init(Broker broker, Settings p) throws InitException
   {
      _brokerArg[0] = broker;
      try {
         String parsers = p.getSetting("Parsers");
         Enumeration penum = new StringTokenizer(parsers);
         while (penum.hasMoreElements()) {
            String par = (String) penum.nextElement();
            try {
               broker.getLog("engine", "parsing and template execution").info("Registering parser: " + par); 
               register(par);
            } catch (Exception ce) {
               broker.getLog("engine", "parsing and template execution").error("Could not load parser: " + par);    
            }
         }
      } catch (Exception e) {
         throw new InitException("Could not init ParserProvider",e);
      }
   }

   public void destroy() 
   {
      _parsers.clear();
   }

   public Object get(String name) throws NotFoundException
   {
      try {
         return getParser(name);
      } catch (Exception e) {
        throw new NotFoundException("No such directive: " + name, e);
      }
   }

   public void flush() { }
}


