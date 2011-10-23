package org.webmacro.broker;

import org.webmacro.Context;
import org.webmacro.Broker;
import org.webmacro.PropertyException;

/**
 * ContextAutoLoader
 * 
 * @author Brian Goetz
 */
public interface ContextAutoLoader
{

  /**
   * Called once after construction.
   */
  void init(Broker b,
            String name);

  /**
   * Return a new instance of the named automatic variable
   */
  Object get(String name,
             Context context)
      throws PropertyException;
}
