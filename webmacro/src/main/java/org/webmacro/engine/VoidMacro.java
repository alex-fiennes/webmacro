/*
 * VoidMacro.java Created on March 26, 2001, 12:33 AM
 */

package org.webmacro.engine;

import org.webmacro.Context;
import org.webmacro.FastWriter;
import org.webmacro.Macro;
import org.webmacro.PropertyException;

import java.io.IOException;

/**
 * VoidMacro doesn't output data to the output stream, but will log a debug message (if debugging is
 * turned on) when either of it's methods are called.
 * <p>
 * In addition, since this is a special-case Macro, and really only used by the PropertyOperator and
 * Variable classes, we have a public static field called <code>instance</code> that will return an
 * already created instance of this guy. Since it doesn't do anything, we really only need 1 of them
 * around.
 * 
 * @author e_ridge
 * @since 0.96
 */
public final class VoidMacro
  implements Macro
{

  public static final VoidMacro instance = new VoidMacro();

  @Override
  public void write(FastWriter out,
                    Context context)
      throws PropertyException, IOException
  {
    // Don't do anything.
  }

  /**
   * Always throws a new <code>PropertyException.VoidValueException</code>
   */
  @Override
  public Object evaluate(Context context)
      throws PropertyException
  {
    // We throw VoidValueException
    throw new PropertyException.VoidValueException();
  }

}
