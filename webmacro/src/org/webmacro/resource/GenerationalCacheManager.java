/*
  * Copyright (C) 1998-2001 Semiotek Inc.  All Rights Reserved.
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

/**
  * An implementation of CacheManager which uses the "VFC" cache manager
  * from Acctiva and Open Doors Software.
  * <p>
  * This manager provides a number of specific property options to support
  * flexible deployment. Understanding these options is useful if you
  * want behavior to adapt from development to deployment.
  * <p>
  * The default behavior, for the most part, is optimized for production
  * deployment. Therefore, you must explicitly set some options to gain
  * certain kinds of caching behavior appropriate for a development setting,
  * eg, reloadability of a file which has changed on disk.
  * <p>
  * This cache manager supports reloadability
  * of a cache entry provided the cache element knows how
  * to test itself for having become stale.
  * <a href="mailto:lane@opendoors.com">Architecture Support</a>
  * @since 0.96
  */
package org.webmacro.resource;

import java.lang.ref.SoftReference;

import org.opendoors.cache.UpdateableCache;
import org.opendoors.cache.immutable.*;

import org.webmacro.*;
import org.webmacro.util.*;


public class GenerationalCacheManager implements CacheManager {

    private UpdateableCache cache;
    private Log log;
    private CacheFactory cacheFactory;
    private String resourceType;
    private boolean reloadOnChange = false;

    public GenerationalCacheManager() { }

    public void init(Broker b, Settings config, String resourceType)
                        throws InitException {
      Settings s = new SubSettings(config, "GenerationalCacheManager." + resourceType);
      Settings def = new SubSettings(config, "GenerationalCacheManager.*");
      if (s.containsKey("ReloadOnChange")) {
        reloadOnChange = s.getBooleanSetting("ReloadOnChange"); // for this resource type
      }
      else if (def.containsKey("ReloadOnChange")) {
        reloadOnChange = def.getBooleanSetting("ReloadOnChange"); // all resource types
      }
       
      cacheFactory = new CacheFactory(def.getAsProperties()); // uses the union
       
      this.cache = cacheFactory.initialize(null);
      this.log = b.getLog("resource");
      this.resourceType = resourceType;
    }

    public void flush() {
      cache.invalidateAll();
    }

    public void destroy() {
       cacheFactory.destroy(cache);
    }

    /**
     * Get the cached value and load
     * it if it is not present or reloading
     * is required.
     */
    public Object get(final Object query, ResourceLoader helper)
                    throws ResourceException  {
      if (reloadOnChange)
        return getReloadable(query, helper);
      else
        return getUnreloadable(query, helper);
    }

    public Object getUnreloadable(final Object query, ResourceLoader helper)
                    throws ResourceException  {
      Object o = cache.get(query);
      if (o == null) {
         o = helper.load(query, null);
         if (o != null)
            cache.put(query, o);
      }
      return o;
    }


    public Object getReloadable(final Object query, ResourceLoader helper)
                      throws ResourceException  {
      Object o = null;
      ScmCacheElement r = (ScmCacheElement) cache.get(query);
      if (r != null)
        o = r.value;  // if you're using soft refs
      boolean reload = false;
      if (o != null && r.reloadContext != null && reloadOnChange)
        reload = r.reloadContext.shouldReload();
      if (o == null || reload) {
        if (r == null)
          r = new ScmCacheElement();
        o = helper.load(query, r);
        if (o != null) {
          r.value = o;
          cache.put(query, r);
        }
      }
      return o;
    }

    /** This manager supports reloading and so this returns true. */
    public boolean supportsReload() {
       return true;
    }

    /** Returns the wm type of resource it is caching. */
    public String getResourceType() {
      return resourceType;
    }


    /**
     * A caching element with
     * smart enough to reload itself.
     * <p>
     * Note: SoftReference is a huge overhead hit so
     * it has been obsoleted in favor of straight obj refs.
     */
    private static class ScmCacheElement extends CacheElement {
       private Object value;
       private CacheReloadContext reloadContext = null;

       public void setReloadContext(CacheReloadContext rc) {
          this.reloadContext = rc;
       }
    }

}
