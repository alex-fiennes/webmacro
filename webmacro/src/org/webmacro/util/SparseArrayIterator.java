
package org.webmacro.util;
import java.util.Iterator;

public class SparseArrayIterator implements Iterator 
{

   protected int _last;
   protected int _pos;
   final protected Object[] _values;


   SparseArrayIterator(Object[] values) {
      _pos = -1;
      _last = -1;
      _values = values;
      advance();
   }

   void advance() {
      while (_pos++ < _values.length) {
         if ((_pos == _values.length) || (_values[_pos] != null)) {
            return;    
         }
      }
   }

   public Object next() {
      Object o = _values[_pos];
      _last = _pos;
      advance();
      return o;
   }

   public boolean hasNext() {
      return ((_pos < _values.length) && (_values[_pos] != null));
   }

   public void remove() throws IllegalStateException {
      if (_last < 0) {
         throw new IllegalStateException("Remove called before next()");
      }
      _values[_last] = null;
   }
}


