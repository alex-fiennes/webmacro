
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

