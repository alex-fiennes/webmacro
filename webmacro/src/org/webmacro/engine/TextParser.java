

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
import java.io.*;
import org.webmacro.*;

/**
  * This parser returns all the input as is
  */
public class TextParser implements Parser
{

   public TextParser(Broker broker) { }

   public BlockBuilder parseBlock(String name, Reader in) 
      throws ParseException, IOException
   {
      BlockBuilder bb = new BlockBuilder(); 
      char buf[] = new char[512];
      int num;

      // no good choice here, let's try UTF8 here: we encode and 
      // decode witht he same thing so it should be OK.
      ByteArrayOutputStream os = new ByteArrayOutputStream(256);
      FastWriter fw = new FastWriter(os, "UTF8");
      while ((num = in.read(buf)) != -1) {
         fw.write(buf,0,num);
      }
      fw.flush();
      bb.addElement(os.toString("UTF8"));
      in.close();
      return bb;
   } 
}


