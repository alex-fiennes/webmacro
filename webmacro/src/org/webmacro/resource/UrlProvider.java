
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
 * * See the attached License.html file for details, or contact us
 * by e-mail at info@semiotek.com to get a copy.
 */


package org.webmacro.resource;

import java.lang.*;
import java.net.*;
import java.io.*;
import org.webmacro.util.*;
import org.webmacro.*;

/**
  * This is the canonical provider for mapping URLs to Handlers. The
  * reactor will request that the "handler" provider return a Handler
  * object when supplied with a URL.
  * <p>
  * You could implement your own version of this class to return 
  * handlers based on whatever criteria you wanted.
  */

final public class UrlProvider extends CachingProvider
{

   /**
     * We serve up "url" type resources
     */
   final public String getType() { return "url"; }

   /**
     * Load a URL from the specified name and return it. The expire
     * time for the SoftReference will be set to the expire time
     * for the loaded URL.
     * <p>
     * Http expires informationwill be obeyed between the minimum and
     * maximum refresh times of 1 second to 2 minutes.
     */
   final public TimedReference load(String name) 
      throws NotFoundException
   {

      try {
         URL u;
         if (name.indexOf(":") < 3) {
            u = new URL("file",null,-1,name); 
         } else {
            u = new URL(name);
         }
         URLConnection uc = u.openConnection();

         String encoding = uc.getContentEncoding();
         if (encoding == null) {
            encoding = "UTF-8";
         }
         Reader in = new InputStreamReader(new BufferedInputStream(uc.getInputStream()),encoding);

         int length = uc.getContentLength();
         if (length == -1) {
            length = 1024;
         }

         long now = System.currentTimeMillis();
         long timeout = uc.getExpiration() - now;
         if (timeout < 1) {
            timeout = (now - uc.getLastModified())/2;
         }
         if (timeout > 120000) {
            timeout = 120000;   
         } else if (timeout < 1000) {
            timeout = 1000;
         }

         char buf[] = new char[1024];
         StringWriter sw = new StringWriter(length);
         int num;
         while ( (num = in.read(buf)) != -1 ) {
            sw.write(buf, 0, num);
         }
         in.close();
         return new TimedReference(sw.toString(), timeout);
      } catch (Exception e) {
         throw new NotFoundException(this + " unable to load " + name + ": " + e.getMessage());
      }
   }

}


