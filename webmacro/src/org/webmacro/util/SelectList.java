
package org.webmacro.util;

import java.util.*;

public class SelectList implements Enumeration
{

   Object[] _values;
   int _current;
   int _selected;

   public SelectList(Object[] elements, int selected) {
      _current = -1;
      _selected = selected;
      _values = elements;
   }

   public boolean hasMoreElements() {
      return ((_current + 1) < _values.length);
   }

   public Object nextElement() throws NoSuchElementException {
      _current++;
      if (_current >= _values.length) {
         throw new NoSuchElementException("List only has " 
               + _values.length + " elements");
      }
      return this;
   }

   public boolean isSelected() {
      return (_current == _selected);
   }

   public String getSelected() {
      return isSelected() ? "SELECTED" : "";
   }

   public Object getValue() {
      try {
         return _values[_current];
      } catch (Exception e) { 
         return null;
      }
   }

   public String toString() {
      try {
         return _values[_current].toString();
      } catch (Exception e) {
         return null;
      }
   }

}
