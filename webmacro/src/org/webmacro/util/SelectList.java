/*
 * Copyright (C) 1998-2000 Semiotek Inc.  All Rights Reserved.  
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted under the terms of either of the following
 * Open Source licenses:
 *
 * The GNU General Public License, version 2, or any later version, as
 * published by the Free Software Foundation
 * (http://www.fsf.org/copyleft/gpl.html);
 *
 *  or 
 *
 * The Semiotek Public License (http://webmacro.org/LICENSE.)  
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See www.webmacro.org for more information on the WebMacro project.  
 */


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
