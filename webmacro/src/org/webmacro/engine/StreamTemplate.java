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
import org.webmacro.util.*;
import org.webmacro.*;

/**
  * StreamTempaltes are constructed with a stream from which they 
  * read their data. They can only read the stream once, and after
  * that will throw an exception. Mostly they are useful for testing
  * WebMacro directives on the command line, since a main() is 
  * provided which reads the template on standard input.
  */

public class StreamTemplate extends WMTemplate
{

   /**
     * Our stream
     */
   private Reader _in;

   /**
     * Instantiate a template based on the specified stream
     */
   public StreamTemplate(Broker broker, Reader inStream)
   {
      super(broker);
      _in = inStream;
   }


   /**
     * Get the stream the template should be read from. Parse will 
     * call this method in order to locate a stream.
     */
   protected Reader getReader() throws IOException {
      if (_in != null) {
         Reader ret = _in;
         _in = null;
         return ret;
      } else { 
         throw new IOException("Already read stream.");
      }
   }

   /**
     * Return a name for this template. For example, if the template reads
     * from a file you might want to mention which it is--will be used to
     * produce error messages describing which template had a problem.
     */
   public String toString() {
      return "StreamTemplate: (stream " + _in + ")";
   }

   /**
     * Simple test 
     */
   public static void main(String arg[]) 
   {

      // Build a context
      WebMacro wm = null;
      Context context = null;

      try {
         wm = new WM();
         context = wm.getContext();
         Object names[] = { "prop" };
         context.setProperty(names, "Example property");
      } catch (Exception e) {
         e.printStackTrace();
      }

      try {
         /*
         HashMap hm = new HashMap();
         hm.put("one", "the first");
         hm.put("two", "the second");
         hm.put("three", "the third");
         context.setBean(hm);
         */
         context.put("helloworld", "Hello World");
         context.put("hello", "Hello");
         context.put("file", "include.txt");
         context.put("today", new Date());
         TestObject[] fruits = { new TestObject("apple",false),
                          new TestObject("lemon",true),
                          new TestObject("pear",false),
                          new TestObject("orange",true),
                          new TestObject("watermelon",false),
                          new TestObject("peach",false),
                          new TestObject("lime",true) };

         SelectList sl = new SelectList(fruits, 3);
         context.put("sl-fruits", sl);

         context.put("fruits", fruits);
         context.put("flipper", new TestObject("flip",false));

         System.out.println("- - - - - - - - - - - - - - - - - - - -");
         System.out.println("Context contains: helloWorld, hello, file, TestObject[] fruits, SelectList sl(fruits, 3), TestObject flipper"); 
         System.out.println("- - - - - - - - - - - - - - - - - - - -");

         Template t1 = new StreamTemplate(wm.getBroker(), 
               new InputStreamReader(System.in));
         t1.parse();

         FastWriter w = FastWriter.getInstance(wm.getBroker(), "UTF8");

         t1.write(w,context);
         System.out.println("*** RESULT ***");
         w.writeTo(System.out);
         w.close();
         context.clear();
         System.out.println("*** DONE ***");
         //System.out.println(result);
        
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

}
