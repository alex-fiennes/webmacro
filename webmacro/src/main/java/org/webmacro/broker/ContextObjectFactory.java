package org.webmacro.broker;

import org.webmacro.Context;
import org.webmacro.PropertyException;

/**
 * ContextObjectFactory This interface replaces ContextTool.
 * 
 * @author Brian Goetz
 */
public interface ContextObjectFactory
{
  /**
   * Return an object which is suitable for placement in the context. This might be a new
   * context-specific object, or a shared instance of an immutable object.
   */
  public Object get(Context c)
      throws PropertyException;
}
