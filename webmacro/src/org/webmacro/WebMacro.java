
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

package org.webmacro;


/**
  * WebMacro Manager Interface
  *
  * This interface provies root access into the WebMacro system. Use it to
  * obtain references to other WebMacro objects which you might need. 
  * <p>
  * Create a new instance of this object in each thread that intends to
  * use WebMacro, and destroy() it when you're done. It probably maintains
  * a static reference count of the number of users of the broker, 
  * and automatically shuts down the underlying broker when the last 
  * instance is destroyed.
  */
public interface WebMacro 
{

   /**
     * Call this method when you are finished with WebMacro. If you don't
     * call this method, the Broker and all of WebMacro's caches may not
     * be properly shut down, potentially resulting in loss of data, and
     * wasted memory. This method is called in the finalizer, but it is
     * best to call it as soon as you know you are done with WebMacro.
     * <p> After a call to destroy() attempts to use this object may yield
     * unpredicatble results.
     */
   public void destroy();

   /**
     * This message returns false until you destroy() this object,
     * subsequently it returns true. Do not attempt to use this object
     * after it has been destroyed.
     */
   public boolean isDestroyed();

   /**
     * This object is used to access components that have been plugged
     * into WebMacro; it is shared between all instances of this class and
     * its subclasses. It is created when the first instance is initialized,
     * and deleted when the last instance is shut down. If you attempt to 
     * access it after the last servlet has been shutdown, it will either 
     * be in a shutdown state or else null.
     */
   public Broker getBroker();

   /**
     * Retrieve a template from the "template" provider. Equivalent to 
     * getBroker().get(TemplateProvider.getType(),key)
     * @exception NotFoundException if the template was not found
     */
   public Template getTemplate(String key) throws NotFoundException;

   /**
     * Retrieve the contents of a URL as a String. The only advantage of
     * using this instead of a regular URL object is that the result may 
     * be cached for repeated use. 
     */
   public String getURL(String url) throws NotFoundException;

   /**
     * Retrieve configuration information from the "config" provider.
     * Equivalent to getBroker().get(Config.geType(),key)
     * @exception NotFoundException could not locate requested information
     */
   public String getConfig(String key) throws NotFoundException;

   /**
     * Create a new Context. You will likely call this method on every 
     * request to create the Context you require for template execution. 
     * Fill the Context up with the data you wish to display on the 
     * template. 
     */
   public Context getContext();

   /**
     * Get a log to write information to. The log messages will 
     * be output to one or more pre-configured log files. The 
     * type you specify will be printed in the log next to 
     * any message you log. See the WebMacro.properties (or other
     * configuration) for information on how to set up and 
     * control logging.
     */
   public Log getLog(String type, String description);

   /**
     * Get a log using the type as the description
     */
   public Log getLog(String type);

   /**
     * Create a new WebContext object. This returns a Context object 
     * with special knowledge of servlets (request and response) 
     * thereby enabling some extra functionality. If you are using 
     * WebMacro under a servlet this is the preferred method, 
     * otherwise you ought to use getContext().
     */
   public org.webmacro.servlet.WebContext getWebContext(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp);


}

