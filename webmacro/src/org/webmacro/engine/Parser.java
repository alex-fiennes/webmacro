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
  * A parser turns an input stream into a BlockBuilder, using any 
  * parsing mechanism that it chooses. It closes the input stream 
  * after parsing its contents. 
  * <p>
  * A Parser must also have a single argument constructor, where the
  * single argument is an object of type Broker. This allows the 
  * parser to query the broker for any modules or other things it 
  * may need to load (for example, Directives). The constructor 
  * signature must look like this:<pre>
  *    public SomeParser(Broker broker) throws InitException
  * </pre>. A Parser may throw an InitException if it cannot load
  * things it needs using the Broker.
  * <p>
  * At runtime, a Parser may be used many times, simultaneously, to 
  * parse many templates. It must therefore be threadsafe. The recommended
  * way to make a parser threadsafe is to have it not use any class 
  * variables, but rely completely on passing data it needs through
  * arguments--this can be done fairly conveniently in a recursive
  * decent parser.
  * <p>
  * When designing a Parser and its corresponding Builders, bear
  * in mind the following WebMacro design decision:  It is acceptable to
  * sacrifice parsing speed in order to speed up runtime execution
  * of a block, since a template is typically parsed just once, but 
  * executed manyt imes. Thus, do not optimize your parser in ways that 
  * make it difficult to optimize the resulting execution tree.
  */
public interface Parser 
{

   /**
     * Parse the input in ParseTool as far as the grammar for 
     * this parser allows, but no farther.
     */
   abstract public BlockBuilder parseBlock(String name, Reader in) 
      throws ParseException, IOException;


}


