/*
* Copyright Open Doors Software and Acctiva, 1996-2001.
* All rights reserved.
*
* Software is provided according to the MPL license.
* Open Doors Software and Acctiva provide this
* software on an as-is basis and make no representation as
* to fitness for a specific purpose.
*
* Direct all questions and comments to support@opendoors.com
*/

package org.opendoors.cache.immutable;
import java.util.*;
import org.opendoors.cache.UpdateableCache;

/**
 * CacheFactory provides static and instance-specific
 * methods for the creation of an immutable cache.
 * <p>
 * The preferred method of using this
 * cache is to create an instance of it and
 * specify the property values for the creation
 * of usually several caches over the life-cycle
 * of an application.
 * <p>
 * create(), create(Map) and destroyCache(UpdateableCache)
 * are the static methods available using the default
 * properties.
 * @see org.opendoors.cache
 */
public class CacheFactory {

  /** The optional subclass controlling the cache. */
  Class changeControlSubClass;

  /**
   * The factory provided refresh rate for the cache.
   * <p>
   * If the cache is out of date and idle this
   * amount of time in millis, it will be updated.
   */
  int refreshRate = 1000;

  /**
   * Creates an instance of a cache factory with
   * text property options to control the
   * behavior the factory.
   * <p>
   * Use these options to customize, in other words,
   * the kind of caches the instance this factory
   * produces. The properties names, values and defaults:
   * <pre>
   * ChangeControl=Subclass.impl.of.ChangeControl
   * default: org.opendoors.cache.imple.ChangeControl
   * usage: certain cache implementors will subclass ChangeControl
   * to provide more granular control over the regeneration
   * of the immutable image of the cache.
   * 
   * RefreshRate=NumberOfMilliseconds
   * default: 2500 milliseconds
   * usage: the amount of time between updates provided
   * the cache is idle at least this amount of time.
   * </pre>
   * @param properties If null, the above defaults will be used,
   * else the elements of the properties will be employed.
   */
  public CacheFactory(Properties properties) {
    this(properties, null);
  }

  /**
   * This constructor allows you to
   * present factory defaults different than
   * those provided by VFC.
   */
  public CacheFactory(Properties properties, Properties defaults) {
	  String prop = getValue(properties, defaults, "ChangeControl");
    if (prop != null) {
      try {
        changeControlSubClass = Class.forName(prop);
      }
      catch (Exception e) {
        e.printStackTrace(System.out);
      }
    }
    prop = getValue(properties, defaults, "RefreshRate");
    if (prop != null) {
      refreshRate = Integer.parseInt(prop);
    }

	}

  /**
   * Initializes a cache and returns it according to the factory
   * properties.
   */
	public UpdateableCache initialize(Map preload) {
 	  return new CacheImpl(preload, changeControlSubClass, refreshRate);
  }

  /**
   * A factory static method to provide an
   * empty updateable cache.
   */
	public static UpdateableCache create() {
	  CacheFactory f = new CacheFactory(null, null);
	  return f.initialize(null);
	}

  /**
   * A factory static method to provide an
   * updateable cache preloaded
   */
	public static UpdateableCache create(Map map) {
	  CacheFactory f = new CacheFactory(null, null);
	  return f.initialize(map);
	}

	/** A static factory method to destroy a cache. */
  public static void destroyCache(UpdateableCache cache) {
    ((CacheImpl) cache).destroy();
  }
	  

  /** Destroys the instance of the cache created by the factory. */
  public void destroy(UpdateableCache cache) {
    ((CacheImpl) cache).destroy();
  }

  /** Get property values for the constructor. */
  private String getValue(Properties values, Properties defaults,
                          String name) {
    String value = null;
    if (values != null)
      value = values.getProperty(name);
    if (value == null && defaults != null)
      value = defaults.getProperty(name);
    return value;
  }
}


