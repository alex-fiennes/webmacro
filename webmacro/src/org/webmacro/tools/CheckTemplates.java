package org.webmacro.tools;

import java.util.*;
import java.io.*;

import org.webmacro.*;
import org.webmacro.engine.*;

/**
 * CheckTemplates
 *
 * Program which uses the WebMacro parser to validate templates
 *
 * @author Brian Goetz
 */

public class CheckTemplates {

  private static Parser parser;
  private static Broker broker;

  public static void parseTemplate(String name, Reader in) {
    try {
      Builder bb = parser.parseBlock(name, in);
      Block b = (Block) bb.build(new BuildContext(broker));
    }
    catch (Exception e) {
      System.err.println("Exception parsing template " + name + "\n" + e);
    }
  }

  public static void main(String[] args) throws Exception {
    WM wm = new WM("org/webmacro/tools/CheckTemplates.properties");
    broker = wm.getBroker();
    parser = (Parser) broker.get("parser", "wm");

    if (args.length == 0) 
      parseTemplate("Standard in", new InputStreamReader(System.in));
    else {
      for (int i=0; i<args.length; i++) 
        parseTemplate(args[i], new FileReader(args[i]));
    };
  }
}

