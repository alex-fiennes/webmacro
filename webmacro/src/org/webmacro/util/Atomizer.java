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

import java.util.HashMap;
import java.util.Iterator;

/**
  * Atomize an object into an atomic number, and provide a means to turn 
  * that atomic number back into the original object.
  */
final public class Atomizer implements Cloneable {

   private HashMap _atoms;
   private Object[] _values;
   private Pool _freeAtoms = new ScalablePool();
   private int _max = 0;

   /**
     * Create an atomizer with space for 100 atoms
     */
   public Atomizer() {
      this(100);
   }

   /**
     * Create an atomizer with space for the specified number
     * of atoms.
     */
   public Atomizer(int size) {
      _atoms = new HashMap( (int) (size/.75 + 1));
      _values = new Object[size];
   }

   /**
     * Clone this Atomizer
     */
   public Object clone() {
      try {
         Atomizer c = (Atomizer) super.clone();
         c._atoms = (HashMap) _atoms.clone();
         c._values = (Object[]) _values.clone();
         c._freeAtoms = new ScalablePool();
         return c;
      } catch (CloneNotSupportedException e) {
         // never gonna happen
         return null;
      }
   }

   /**
     * Get the atomic number for o. If o does not have an atomic 
     * number return -1.
     */
   public int lookup(Object o) {
      Integer atom = (Integer) _atoms.get(o);
      return (atom != null) ? atom.intValue() : -1;
   }

   /**
     * Put an object in the atomizer, return its atomic number.
     * You will need this atomic number later to access the object.
     * If the object is already in the atomizer you will get back
     * the same atomic number as last time.
     */
   public int atomize(Object o) {
      Integer atom = (Integer) _atoms.get(o);
      int i;
      if (atom == null) {
         atom = (Integer) _freeAtoms.get();
         if (atom == null) {
            atom = new Integer(_max++);
         }
         i = atom.intValue();
         if (_max == _values.length) {
            int newM = _max * 2 + 1;
            Object[] newV = new Object[newM];
            System.arraycopy(_values,0,newV,0,_max);
            _values = newV;
         }
         _values[i] = o;
         _atoms.put(o,atom);
      } else {
         i = atom.intValue();
      }
      return i;
   }

   /**
     * Get the Object matching this atom
     */
   public Object get(int atom) {
      try {
         return _values[atom];
      } catch (ArrayIndexOutOfBoundsException e) {
         return null;
      }
   }

   /**
     * Remove an entry by atomic number, returning its former value.
     */
   public Object remove(int atom) {
      try {
         Object o = _values[atom];
         remove(o);      
         return o;
      } catch (ArrayIndexOutOfBoundsException e) { 
         return null; 
      }
   }

   /**
     * Remove an entry by value
     */
   public void remove(Object o) {
      if (o == null) { return; }
      Integer atom = (Integer) _atoms.remove(o);
      if (atom == null) { return; }
      _values[atom.intValue()] = null;
      _freeAtoms.put(atom);
   }

   /**
     * Get an iterator capable of walking through all the values
     * in the atomizer.
     */
   public Iterator iterator() { 
      return new Iter(_values); 
   }

   /**
     * Test based on command line args
     */
   public static void main(String arg[]) {

      Atomizer a = new Atomizer(3);
      int atoms[] = new int[arg.length];
      for (int i = 0; i < arg.length; i++) {
         atoms[i] = a.atomize(arg[i]);
         System.out.println("Atomized " + arg[i] + " to " + atoms[i]);
      }

      for (int i = 0; i < atoms.length; i++) {
         System.out.println("Atom " + atoms[i] + " is " + a.get(atoms[i]));
      }

      Iterator iter = a.iterator();
      while (iter.hasNext()) {
         Object o = iter.next();
         System.out.println("RM: Object " + o + " was atom " + a.atomize(o));
         iter.remove();
      }

      atoms= new int[arg.length];
      for (int i = 0; i < arg.length; i++) {
         atoms[i] = a.atomize(arg[i]);
         System.out.println("Atomized " + arg[i] + " to " + atoms[i]);
      }
   }

   class Iter extends SparseArrayIterator {
      Iter(Object values[]) {
         super(values);
      }

      public void remove() throws IllegalStateException {
         if (_last < 0) {
            throw new IllegalStateException("Remove called before next()");
         }
         Atomizer.this.remove(this._values[this._last]);
      }
   }
}


