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
* Simple wrapper for an Enumeration that adds a getCurrentIndex method.
*
* @author Keats Kirsch
* @see java.util.Enumeration
*/
public class EnumUtil implements java.util.Enumeration {
  private Enumeration enum;
  private int currIndex;

  public EnumUtil(Enumeration enum) { 
    currIndex = 0;
    this.enum = enum; 
  }
  
  /**
   * @return Number of times nextElement() has been called on this object.
   */
  public int getCurrentIndex(){ return currIndex; }
  
  public boolean hasMoreElements()  { 
    return enum.hasMoreElements(); 
  }

  public Object nextElement()  { 
    currIndex++;
    return enum.nextElement();
  }

  /** test harness */
  static public void main(String[] args){
    java.util.Vector v = new java.util.Vector(3);
    v.addElement("Element 1");
    v.addElement("Element 2");
    v.addElement("Element 3");
    EnumUtil ie = new EnumUtil(v.elements());
    while (ie.hasMoreElements()){
      String s = (String)ie.nextElement();
      System.out.println(ie.getCurrentIndex() + ": " + s);
    }
  }
}
