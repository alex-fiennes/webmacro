
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


import org.webmacro.*;
import org.webmacro.servlet.WebContext;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
  * This is the "hello world" WebMacro example. To get it working, put this
  * class (which has no package) in your CLASSPATH. You could do that by 
  * putting this directory in your classpath.
  *
  * Next, ensure that the WebMacro.properties file has this directory set 
  * as the location for template files (in particular, test.wm).
  *
  * Then set servlet.properites with the following line:
  *
  * servlet.TestWM.code=org.webmacro.servlet.Reactor
  *
  * Start your servletrunner, connect to the port, and request the URL:
  *
  * http://...servletrunner.../servlet/TestWM/
  *
  * You should see "Hello there: hello world!" up on your screen.
  */
public class TestWM extends HttpServlet
{

   private WebMacro _wm = null;

   public void init(ServletConfig sc) throws ServletException {
      try {
         if (_wm == null) {
            _wm = new WM();
         }
      } catch (InitException e) {
         throw new ServletException("Could not initialize WebMacro: " + e);
      }
   }

   public void destroy() {
      if (_wm != null) {
         _wm.destroy();
         _wm = null;
      }
   }


   public void doGet(HttpServletRequest req, HttpServletResponse resp) {

      try {
         java.io.Writer out = null;

         try {

            // get the stream we intend to write to
            out = resp.getWriter();

            // create a context for the current request
            WebContext c = _wm.getWebContext(req,resp);

            // fill up the context with our data
            c.put("Today", new Date());
            c.put("Number", new Long(23));

            // WebContext provides some utilities as well
            String other = c.getForm("other"); 
            if (other == null) {
               c.put("hello","hello again!"); // put this into the hash
            } else {
               c.put("hello",other);          // else put this in
            }

            // get the template we intend to execute
            Template t = _wm.getTemplate("test.wm");

            // write the template to the output, using our context
            t.write(resp.getWriter(), c);

         } catch (org.webmacro.NotFoundException e) {
         out.write("ERROR!  Could not locate template test.wm, check that your template path is set properly in WebMacro.properties");
         } catch (org.webmacro.ContextException e) {
            out.write("ERROR!  Could not locate required data in the Context.");
         }
      } catch (java.io.IOException e) {
         // what else can we do?
         System.out.println("ERROR: IOException while writing to servlet output stream.");
      }
   }

}
