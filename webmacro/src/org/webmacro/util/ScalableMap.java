
package org.webmacro.util;

/**
  * Reduce locking overhead for a map with few writers and many 
  * readers. Writes are five times more expensive than a SimpleMap,
  * reads cost only slightly more. However, five readers can access
  * the same value simultaneously, without blocking.
  */
final public class ScalableMap {

   final int factor = 5;
   final SimpleMap[] _maps = new SimpleMap[factor];
   int pos = 0;

   public ScalableMap(final int size) {
      for (int i = 0; i < factor; i++) {
         _maps[i] = new SimpleMap(size);
      }
   }

   public void put(final Object key, final Object value) {
      for (int i = 0; i < factor; i++) {
         _maps[i].put(key,value); 
      }
   }

   public Object get(final Object key) {
      pos = (pos + 1) % factor;
      return _maps[pos].get(key);
   }

   public void remove(final Object key) {
      for (int i = 0; i < _maps.length; i++) {
         _maps[i].remove(key);
      }
   }
}
