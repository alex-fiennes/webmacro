
/*
 * Copyright (c) 1998, 1999, 2000 Semiotek Inc. All Rights Reserved.
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
import java.util.Date;

/**
  * This example demonstrates using WebMacro outside the context of a servlet.
  */
public class NoServlet
{

  public static void main(String[] args) throws Exception {
    WM wm = new WM();
    Context c = new Context(wm.getBroker());

    // fill up the context with our data
    c.put("Today", new Date());
    c.put("Number", new Long(23));
    c.put("hello", "Hello there!");

    // get the template we intend to execute
    Template t = wm.getTemplate("noservlet.wm");

    t.write(System.out, "UTF8", c);
  }
}
