
package org.webmacro.engine;

import org.webmacro.*;

/**
  * A dependent directive can only appear as the dependent of 
  * another directive. For example, #else is a dependent of #if.
  */
public interface SubDirective extends Directive
{

}
