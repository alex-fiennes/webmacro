/*
* Copyright Open Doors Software and Acctiva, 2000.
*
* Software is provided according to the ___ license.
* Open Doors Software and Acctiva provide this
* software on an as-is basis and make no representation as
* to fitness for a specific purpose.
*
* Direct all questions and comments to support@opendoors.com
*/
package org.opendoors.util;

import java.util.*;

/**
* @author Keats Kirsch
* @see java.util.Enumeration
*/
public class ModuloUtil {

  static private ModuloUtil singleton = null;

  private ModuloUtil(){ singleton = this; }

  static public ModuloUtil getInstance(){
    if (singleton == null) singleton = new ModuloUtil();
    return singleton;
  }

  static public Enumeration createEnumRange(int rangeBegin, int rangeEnd){
    if (rangeBegin >= rangeEnd) throw new IllegalArgumentException();
    Vector v = new Vector(rangeEnd - rangeBegin + 1);
    for (int i=rangeBegin; i<=rangeEnd; i++){
      v.addElement(new Integer(i));
    }
    return v.elements();
  }

  static int mod(int val, int modulus){ return val % modulus; }

  /** test harness */
  static public void main(String[] args){
    ModuloUtil util = ModuloUtil.getInstance();
    Enumeration enum = util.createEnumRange(4, 7);
    System.out.println("Enumeration that returns integers from 4 through 7");
    while (enum.hasMoreElements()){
      Object o = enum.nextElement();
      System.out.println(o);
    }

    System.out.println("mod(17,3): " + util.mod(17, 3));
    System.out.println("mod(1,2): " + util.mod(1, 2));
  }
}
