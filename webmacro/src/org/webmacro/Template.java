
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

import java.util.*;
import java.io.*;

public interface Template extends Macro, Visitable
{

   /**
     * Force the template to parse now. Normally the template will not parse
     * the supplied file until the data is actually needed. However if you 
     * want to parse all of your templates at the start of the application 
     * to avoid incurring this call during an interactive session, you can 
     * call the parse() function at an appropriate time. Alternately, you
     * could call this function to reparse a template if you know that it
     * has changed.
     * <p>
     * @exception TemplateException if the sytax was invalid and we could not recover
     * @exception IOException if we could not successfullly read the parseTool
     */
   public void parse() throws IOException, TemplateException;

   /**
     * A template may contain parameters, set by the #param directive.
     * These are statically evaluated during the parse phase of the 
     * template and shared between all users of the template. They 
     * are present so that the template can provide some meta information
     * to its user as to what kind of data it expects to find in the Context,
     * or other information about its use. 
     * <p>
     * If the template has not already been parsed, it will be parsed. Thus
     * this method may throw ParseException or IOException if there is some
     * failure in accessing or parsing the template.
     * @exception IOException if an error occurred reading the template
     * @exception ParseException if an error occurred parsing the template
     */
   public Object getParam(String name) throws IOException, TemplateException;

   /**
     * This method can be used to get  a list of all the parameters 
     * that have been set in the template. getParam(Object) can be used
     * to look up any particular parameter.
     */
   public Map getParameters();

}

