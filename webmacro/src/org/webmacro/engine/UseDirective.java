
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


package org.webmacro.engine;

import org.webmacro.util.java2.*;
import org.webmacro.*;
import java.util.*;
import java.io.*;
import org.webmacro.util.*;

/**
  * This directive is used to iterate through the members of a list. 
  * <p>
  * It takes an argument of type Variable and iterates through 
  * each of its members. Each time through the loop a scalar with the 
  * same name of the list is defined, and takes on the value of the list 
  * for that element. The scalar will have the same type as the value of
  * the list element, so if the list contains hashtables, referencing the
  * members of the hashtable will work as expected.
  */
abstract class UseDirective implements Directive
{


   /**
     * Builder method
     */
   public static final Object build(
         BuildContext rc, Object parser, Argument args[], String text) 
      throws BuildException
   {
      String pname = (parser != null) ? parser.toString() : null;

      try {
         Parser p = rc.getParser(pname);
         Reader in = new StringReader(text);
         BlockBuilder bb = p.parseBlock("use:" + pname,in);
         in.close();
         return bb.build(rc); 
      } catch (ParseException pe) {
         throw new BuildException("Parse error in use directive: " + pe);
      } catch (NotFoundException ne) {
         throw new BuildException("Unable to load requested parser: " + pname);
      } catch (IOException io) {
         throw new BuildException("StringReader threw an IO exception: " + io);
      }
   }

   public static final String[] getArgumentNames() { return _verbs; }
   private static final String[] _verbs = { "until" };

   public static final String getMarker(Object target, Argument args[]) {
      Object source = null;
      if ((args.length == 1) && (args[0].getName().equals("until"))) {
         source = args[0].getValue();
      }
      return (source != null) ? source.toString() : "__END__";
   }


}


