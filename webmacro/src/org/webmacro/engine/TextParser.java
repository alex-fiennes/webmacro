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
      StringBuffer sb = new StringBuffer(512);
      int num;
      while ((num = in.read(buf)) != -1) {
         sb.append(buf,0,num);
      }
      bb.addElement(sb.toString());
      in.close();
      return bb;
   } 
}


