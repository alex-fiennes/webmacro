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
import java.util.*;
import java.io.*;
import org.webmacro.*;
import org.webmacro.util.*;
import java.lang.reflect.*;

/**
  * A block represents the text between two {}'s in a template, or else 
  * the text that begins at the start of the template and runs until its
  * end ({}'s around the whole document are not required). It contains 
  * all of the other directives, strings, etc. that can be in a template. 
  */
public class BlockBuilder implements Builder
{
   private static Macro[] mArray = new Macro[0];
   private static String[] sArray = new String[0];

   private ArrayList elements = new ArrayList();

   final public Object build(BuildContext bc) throws BuildException
   {
      ArrayList content = new ArrayList(elements.size());
      {
         Iterator i = elements.iterator();
         while (i.hasNext()) {
            Object o = i.next();
            if (o instanceof Builder) {
               o = ((Builder) o).build(bc);
            }
            if (o instanceof Block) {
               ((Block) o).appendTo(content); 
            } else {
               content.add(o);
            }
         }
     } 
   
     // flatten everything and view the content as being: 
     //        string (macro string)* string
     // store that as an array of strings and an array of 
     // Macro objects and create a block.

     ArrayList strings = new ArrayList((elements.size() + 1) / 2);
     ArrayList macros = new ArrayList((elements.size() + 1) / 2);  
     {
        Iterator i = content.iterator();        
        StringBuffer s = new StringBuffer();
        while (i.hasNext()) {
           Object o = i.next();

           if (o instanceof Macro) {
              strings.add(s.toString());
              s = new StringBuffer(); // do not reuse: othewise all strings will contain char[] of max length!!
              macros.add(o);
           } else if (o != null) {
              s.append(o.toString()); 
           }
        }
        strings.add(s.toString());
     }
   
     Macro m[] = (Macro[]) macros.toArray(mArray);
     String s[] = (String[]) strings.toArray(sArray);
     return new Block(s,m);
   }

   public void addElement(Object o) {
      elements.add(o);
   }

   public int size() {
      return elements.size();
   }

   public void remove(int i) {
      elements.remove(i);
   }
  
   public Object elementAt(int i) {
      return elements.get(i);
   }
  
   public Object setElementAt(Object o, int i) {
      return elements.set(i, o);
   }
  
}

