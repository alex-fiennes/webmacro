/*
* Copyright Open Doors Software. 1996-2002.
*
* Software is provided according to the MPL license.
* Open Doors Software provides this
* software on an as-is basis and make no representation as
* to fitness for a specific purpose.
*
* Direct all questions and comments to support@opendoors.com
*/
package org.webmacro.util;
import java.util.Properties;

/**
 * Implement a behavior allowing for undefined properties to return
 * a useful default value such as "" for a string, "0.00" for 
 * numeric properties, and so forth.
 * <p>
 * A specific default can always be used as is done for string properties in the
 * base class.
 */
public class SparseProperties extends Properties {
  
  /** The global default value for all requests is "" */
  protected Object globalDefault = new String("");
  
  public SparseProperties() {super(); }
  
  public SparseProperties(Properties defaults) { super(defaults); }
  
  public SparseProperties(Object globalDefault) {
    super();
    this.globalDefault = globalDefault;
  }

  public SparseProperties(Properties defaults, Object globalDefault) {
    super(defaults);
    this.globalDefault = globalDefault;
  }
  
  /**
   * Gets the object but returns the default value if not present.
   */
  public Object get(Object key) {
    Object o = super.get(key);
    return (o == null) ? globalDefault : o;
  }
  /**
   * Gets the object but returns the default value if not present.
   */
  public Object get(Object key, Object defaultValue) {
    Object o = super.get(key);
    return (o == null) ? defaultValue : o;
  }

}
  