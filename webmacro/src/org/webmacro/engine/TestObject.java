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
import org.webmacro.*;

/**
  * This class exists for testing purposes only
  */
public class TestObject
{

   public String fname = "default fName";
   public Date   date;
   private String pname;
   private boolean valid;

   public TestObject(String n) {
      pname = n;
      date = new Date();
   }
   
   public TestObject(String n, boolean b) {
      this(n);
      valid = b;
   }

   public String show(String arg) {
      return "test(" + pname + ") showing string: " + arg;
   }

   public String show(Object arg) {
      return "test(" + pname + ") showing object: " + arg;
   }

   public String show(long arg) {
      return "test(" + pname + ") showing long: " + arg;
   }

   public String show(int arg) {
      return "test(" + pname + ") showing int: " + arg;
   }

   public String getName() {
      return pname;
   }

   public void setName(String newName)
   {
      pname = newName;
   }

   public String getIsValid() {
      return valid ? "yes" : null;
   }

   public boolean isEven() {
      boolean answer = false;
      if ((count % 2) == 0) {
         answer = true;
      }
      count++;
      return answer;
   }
   int count = 0;

   static public void main(String arg[]) {

      String a = "hi there";
      Object b = a;

      TestObject t = new TestObject("myTest");

      System.out.println(t.show(a));
      System.out.println(t.show(b));

   }

}

