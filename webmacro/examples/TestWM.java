
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


import org.webmacro.servlet.*;
import org.webmacro.*;
import java.util.Date;

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
public class TestWM implements Handler
{
   public Template accept(WebContext c) 
      throws HandlerException
   {

      c.put("Today", new Date());
      c.put("Number", new Long(23));

      // grab form variable named other
      String other = c.getForm("other"); 

      if (other == null) {
         c.put("hello","hello again!"); // put this into the hash
      } else {
         c.put("hello",other);          // else put this in
      }

      // now we have to return a template
      try {
         return (Template) c.getBroker().request("template","test.wm").getValue();
      } catch (Exception e) {
         throw new HandlerException("Something is not right.");
      }
   }

   public void init() { }
   public void destroy() { }
}
