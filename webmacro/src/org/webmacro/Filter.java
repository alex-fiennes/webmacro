

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

    /*
     * Variables in a template can be processed through a "filter". A filter
     * is a Macro which can be used to render a template variable. For example,
     * a filter could be another template, used to render specific types of
     * objects wherever they appear. Alternatly a filter could be a hand 
     * written processor to perform operations such as HTML escaping. When a 
     * variable is filtered, first the template extracts the value of the 
     * property corresponding to the variable; second it supplies that 
     * resulting object to the filter as the context for evaluation. So 
     * if "$customer" was being filtered through a special customer 
     * template, first WebMacro extracts the object corresponding to 
     * $customer from the current context, as usual. Second, that object
     * is passed to the customer template as the top level context, so 
     * that everything in the customer template is releative to the 
     * value of $customer. 
     */
 
public interface Filter extends Macro
{

   /**
     * Filters are recursively constructed. For example, the variable 
     * "foo" might be filtered through "fooFilter", and within fooFilter
     * the varaible "bar" might be filtered through "barFilter". Thus 
     * at the top level, foo.bar is filtered through "barFilter". For
     * <p>
     * This method returns the filter, if any, associated with the supplied
     * name. Since the result of the query is a Filter, you can query that 
     * filter (if it is non-null) to find out what sub-filters might be 
     * present.
     */
   public Filter getFilter(String name);

}

