/*
 * Copyright (C) 1998-2000 Semiotek Inc.  All Rights Reserved.  
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted under the terms of either of the following
 * Open Source licenses:
 *
 * The GNU General Public License, version 2, or any later version, as
 * published by the Free Software Foundation
 * (http://www.fsf.org/copyleft/gpl.html);
 *
 *  or 
 *
 * The Semiotek Public License (http://webmacro.org/LICENSE.)  
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See www.webmacro.org for more information on the WebMacro project.  
 */


package org.webmacro.engine;

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
         throw new BuildException("Parse error in use directive", pe);
      } catch (NotFoundException ne) {
         throw new BuildException("Unable to load requested parser " + pname, 
                                  ne);
      } catch (IOException io) {
         throw new BuildException("StringReader threw an IO exception", io);
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


