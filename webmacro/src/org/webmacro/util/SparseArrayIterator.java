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


