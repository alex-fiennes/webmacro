package org.webmacro.directive;

import org.webmacro.*;
import org.webmacro.engine.*;

/**
 * Interface used for setting and retrieving directive arguments.
 * @author Brian Goetz
 * @see ArgsHolder
 * @see DirectiveBuilder
 */

public interface DirectiveArgs { 

  public Object getArg(int id) throws BuildException;

  public Object getArg(int id, BuildContext bc) throws BuildException;

  public void setArg(int id, Object o) throws BuildException;

}
