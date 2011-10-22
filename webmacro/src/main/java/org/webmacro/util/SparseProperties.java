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
import java.util.Enumeration;

import javax.servlet.ServletRequest;

/**
 * Implement a behavior allowing for undefined properties to return
 * a useful default value such as "" for a string, "0.00" for 
 * numeric properties, and so forth.
 *
 * @author Lane Sharman
 */
public class SparseProperties extends Properties
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The global default value for all requests is "" */
    protected Object globalDefault = new String("");

    public SparseProperties ()
    {
        super();
    }

    public SparseProperties (Properties defaults)
    {
        super(defaults);
    }

    public SparseProperties (Object globalDefault)
    {
        super();
        this.globalDefault = globalDefault;
    }

    public SparseProperties (Properties defaults, Object globalDefault)
    {
        super(defaults);
        this.globalDefault = globalDefault;
    }
    
    public static SparseProperties getTemplate()
    {
      return getTemplate(null);
    }
    
    public static SparseProperties getTemplate(Properties template)
    {
      SparseProperties p = null;
      if (template == null)
      {
        p = new SparseProperties();
      }
      else
      {
        p = new SparseProperties(template);
      }
      Enumeration e = p.keys();
      while (e.hasMoreElements())
      {
        p.put(e.nextElement(), null);
      }
      return p;
    }

    /**
     * Gets the object but returns the default value if not present.
     */
    @Override
    public Object get (Object key)
    {
        Object o = super.get(key);
        return (o == null) ? globalDefault : o;
    }

    /**
     * Gets the object but returns the default value if not present.
     */
    public Object get (Object key, Object defaultValue)
    {
        Object o = super.get(key);
        return (o == null) ? defaultValue : o;
    }
    
    /**
     * Adds all the Request attributes to this property object.
     */
    public void addRequestAttributes(ServletRequest request)
    {
      System.out.println("Setting Request Properties:");
      Enumeration e = request.getParameterNames();
      while (e.hasMoreElements())
      {
        String k = (String) e.nextElement();
        this.setProperty(k, request.getParameter(k));
        System.out.println(k + ":" + request.getParameter(k));
      }
    }
}
  
