
package org.webmacro;

/**
  * This interface is used to attach utilities to a context to assist 
  * with the generation of views.
  */
public interface ContextTool {

   /**
     * A new tool object will be instantiated per-request by calling 
     * this method. A ContextTool is effectively a factory used to 
     * create objects for use in templates. Some tools may simply return
     * themselves from this method; others may instantiate new objects
     * to hold the per-request state.
     */
   public Object init(Context c) throws InvalidContextException;
 
}
