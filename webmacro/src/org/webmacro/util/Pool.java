
package org.webmacro.util;

/**
  * A pool is a stack-like container which you can add and remove 
  * objects from. It is useful for recycling objects rather than 
  * re-creating them. See ScalablePool and UPool.
  */
public interface Pool
{

   /**
     * Add an item to the pool for later re-use
     */
   public void put(final Object o);

   /**
     * Get an item from the pool
     */
   public Object get();

}
