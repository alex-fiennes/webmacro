package org.webmacro.directive;

import org.webmacro.*;
import org.webmacro.engine.*;

public interface DirectiveArgs { 

  public Object getArg(int id) throws BuildException;

  public Object getArg(int id, BuildContext bc) throws BuildException;

  public void setArg(int id, Object o) throws BuildException;

}
