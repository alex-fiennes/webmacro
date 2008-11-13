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


import java.util.ArrayList;

import org.webmacro.Template;
import org.webmacro.servlet.HandlerException;
import org.webmacro.servlet.WMServlet;
import org.webmacro.servlet.WebContext;

/**
  * This is the "Guest Book" WebMacro example. To get it working, put this
  * class (which has no package) in your servlet directory. Make
  * sure WebMacro.jar is in your CLASSPATH and that you have edited
  * WebMacro.properties appropriately.
  *
  * Start your servletrunner, connect to the port, and request the URL:
  *
  * http://...servletrunner.../servlet/GuestBook/
  *
  * Your guestbook should now work.
  */
public class GuestBook extends WMServlet 
{
  private static final long serialVersionUID = -7787198191764480269L;

  ArrayList book = new ArrayList();

   GuestEntry myGuestEntry;

   public Template handle(WebContext context) 
      throws HandlerException
   {

      context.getResponse().setContentType("text/html");

      Object output = new Object();
      String templateName;

      // get the form variables
      output = context.getForm("loadFile");
      String name = context.getForm("name");
      String email = context.getForm("email");
      String comment = context.getForm("comment");

      if (output == null) {
         output = "form.wm";
         templateName = "form.wm";
      }
      if (name == null) {
         name = "<!-- form variable 'name' not defined -->";
      }
      if (email == null) {
         email = "<!-- form variable 'email' not defined -->";
      }
      if (comment == null) {
         comment = "<!-- form variable 'comment' not defined -->";
      }

      // verifying for submissions
      if (output.equals("verify")) {
         myGuestEntry = new GuestEntry(name, email, comment);
         book.add(myGuestEntry); 
         context.put("registry", book);
       
         templateName = "verify.wm";

      // for guest book view
      } else if (output.equals("allguest")) {
         context.put("registry",book);
         templateName = "allguest.wm";

      // default
      } else {
         templateName = "form.wm";
      }
    
      // return the appropriate template
      try {
         Template t = getTemplate(templateName);
         return t;
      } catch (Exception e) {
         throw new HandlerException("Could not load template: " 
                 + templateName 
                 + ".<br><br>The system reported the following error:<br>" + e.getMessage());
      }
   }


   public final class GuestEntry 
   {
       final private String name;
       final private String email;
       final private String comment;
       
   
       GuestEntry(String inName, String inEmail, String inComment)
       {
          name = inName;
          this.email = inEmail; 
          this.comment = inComment;
       }
   
       final public String getName()
       {
          return name; 
       }
       
       final public String getEmail()
       {
          return email; 
       }
   
       final public String getComment()
       {
          return comment; 
       }
   }
}

