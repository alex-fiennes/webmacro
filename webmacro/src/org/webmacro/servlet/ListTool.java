package org.webmacro.servlet;
import org.webmacro.*;
import java.util.*;

/**
 * Provide templates a uniform way of dealing with arrays and Lists
 * 
 * @author Keats Kirsch
 * @version 1.0
 * @since Oct. 2000
 * @see ListUtil
 */
public class ListTool implements ContextTool
{
  public Object init(Context context) 
  {
    return ListUtil.getInstance();
  }
}
