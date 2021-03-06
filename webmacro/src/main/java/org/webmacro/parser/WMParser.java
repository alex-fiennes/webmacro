/*
 * Copyright (C) 1998-2000 Semiotek Inc. All Rights Reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted under the terms of either of the
 * following Open Source licenses: The GNU General Public License, version 2, or any later version,
 * as published by the Free Software Foundation (http://www.fsf.org/copyleft/gpl.html); or The
 * Semiotek Public License (http://webmacro.org/LICENSE.) This software is provided "as is", with NO
 * WARRANTY, not even the implied warranties of fitness to purpose, or merchantability. You assume
 * all risks and liabilities associated with its use. See www.webmacro.org for more information on
 * the WebMacro project.
 */

package org.webmacro.parser;

import java.io.IOException;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webmacro.Broker;
import org.webmacro.engine.BlockBuilder;
import org.webmacro.engine.Parser;

/**
 * @author Brian Goetz
 */
public class WMParser
  implements Parser
{

  static Logger _log = LoggerFactory.getLogger(WMParser.class);
  private final Broker _broker;

  public WMParser(Broker b)
  {
    _broker = b;
    _log.info("parser created");
  }

  // Parser Interface

  /**
   * Return a short name that identifies this parser. This name could, for example, be used as the
   * extension for files which contain syntax parsable by this parser.
   */
  public final String getParserName()
  {
    return "wm";
  }

  /**
   * Parse a block that appears on the supplied input Reader. The name supplied is used in error
   * messages to identify the source being parsed.
   */
  @Override
  public BlockBuilder parseBlock(String name,
                                 Reader in)
      throws org.webmacro.engine.ParseException, IOException
  {
    BlockBuilder bb;
    WMParser_impl parser = null;
    try {
      parser = new WMParser_impl(_broker, name, in);

      try {
        bb = parser.WMDocument();
      } catch (TokenMgrError e) {
        throw new ParseException("Lexical error: " + e.toString());
      }
    } catch (ParseException e) {
      throw new org.webmacro.engine.ParseException("Parser Exception", e);
    } catch (ParserRuntimeException e) {
      throw new org.webmacro.engine.ParseException("Parse Exception", e);
    }

    return bb;
  }

}
