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

/** 
 * @author Brian Goetz
 */

package org.webmacro.parser;

import java.io.*;
import org.webmacro.engine.*;
import org.webmacro.*;
import org.webmacro.util.ScalablePool;

public class WMParser implements Parser
{
  private final Broker _broker;
  private final ScalablePool _parserCache = new ScalablePool();

  public WMParser(Broker b) {
    _broker = b;
    _broker.getLog("parser").info("parser created");
  }


  // Parser Interface

  /**
   * Return a short name that identifies this parser. This name could,
   * for example, be used as the extension for files which contain 
   * syntax parsable by this parser.
   */
  public final String getParserName() {
    return "wm";
  }

  /**
   * Parse a block that appears on the supplied input Reader. The 
   * name supplied is used in error messages to identify the source
   * being parsed.
   */
  public BlockBuilder parseBlock(String name, Reader in)
    throws org.webmacro.engine.ParseException, IOException
  {
    BlockBuilder bb;
    WMParser_impl parser=null;
    try {
      parser = (WMParser_impl) _parserCache.get();
      if (parser != null) 
        parser.ReInit(in);
      else
        parser = new WMParser_impl(_broker, in);

      try {
        bb = parser.WMDocument();
      }
      catch (TokenMgrError e) {
        throw new ParseException("Lexical error: " + e.toString());
      }
    } 
    catch (ParseException e) {
      throw new org.webmacro.engine.ParseException("Parser Exception", e);
      // _log.exception(e);
      // if (! pin.isAtEOF()) {
      //  int c = pin.nextChar(); // skip a char before continuing
      //  bb.addElement(new Character((char) c).toString());
    }
    finally {
      if (parser != null)
        _parserCache.put(parser);
    }
    
    return bb;
  }
  
}


