import org.webmacro.*;
import org.webmacro.servlet.*;

/**
  * SandBox - where to play
  */
public class SandBox extends WMServlet
{

   public Template handle(WebContext context) throws HandlerException
   {

      // Put whatever data you like into the context. In
      // this example we just put in a simple string

      context.put("Hello", "Hello, brave new world!");


      // Select a template. You could choose the template based 
      // on any criteria you like: user preference, current 
      // action, different templates for different results, etc.

      // We need to acquire a template somehow
      Template view;

      try {

         // The following line asks WebMacro's resource broker to
         // find the "helloWorld.wm" template, cache it, and return a 
         // parsed copy to us.

         view = getTemplate("sandbox.wm");

      } catch (ResourceException e) {

         // If this happens you have not set WebMacro up correctly:
         // either WebMacro isn't up and running well enough to be able 
         // to locate any template, or else we asked for a template which
         // doesn't exist. However, the HelloWorld.wm example should exist
         // if you have everything set up properly!

         throw new HandlerException(
              "SandBox was unable to load \n"
            + "a template. \n\n"
            + "Here is the actual exception that was raised:\n" + e);

      }

      return view;

   }

}

