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

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.HashMap;

import org.webmacro.*;
import org.webmacro.util.Settings;

/**
 * A provider which dispenses Encoders, which are used for encoding
 * Strings and caching the results. 
 * @since 0.96
 * @author Michael Bayne
 */

public class EncoderProvider implements Provider
{
   private Map _encoders = new HashMap();
   private Broker _broker;
   private Settings _config;

   /**
    * The provider type for this provider. Use this when calling
    * <code>Broker.getProvider()</code>.
    */
   public static final String TYPE = "encoder";

   /**
    * Return an array representing the types this provider serves up
    */
   public String getType ()
   {
      return TYPE;
   }

   /**
    * Initialize this provider based on the specified config. Derived
    * encoder provider implementations may override this method to obtain
    * initialization parameters of their own devising, but they must be
    * sure to call super.init() in their overridden methods.
    */
   public void init (Broker b, Settings config) throws InitException
   {
      _broker = b;
      _config = config;
   }

   // Implementation note: the flush(), destroy() and get() methods are
   // instance synchronized to ensure that if _encoders is cleared via the
   // destroy() method and get() is called subsequently, we properly avoid
   // referencing the null variable. We can't synchronize on _encoders
   // because that becomes null and code that does this:
   //
   // if (_encoders != null) {
   //    synchronized (_encoders) {
   //
   // is not valid because the _encoders reference could become null
   // in between those two statements.
   //
   // Additionally, the EncoderProvider is not invoked frequently enough
   // to merit a more sophisticated synchronization approach (it is only
   // called once per request when creating a FastWriter with which to
   // output the response and not even that often because FastWriter
   // instances are cached). Thus, we choose simplicity and robustness in
   // this situation.

   /**
    * Clear any cache this provider may be maintaining.
    */
   public synchronized void flush ()
   {
      // clean out the encoder cache
      _encoders.clear();
   }

   /**
    * Close down this provider, freeing any allocated resources.
    */
   public synchronized void destroy ()
   {
      // clear out our reference to the encoder cache to allow it to be
      // garbage collected
      _encoders = null;
   }

   /**
    * Get the object associated with the specified query.
    */
   public synchronized Object get(String encoding) throws ResourceException
   {
      Encoder encoder = null;
      
      // make sure we're not inadvertently being called after we've
      // already been destroy()ed
      if (_encoders != null) {
         encoder = (Encoder)_encoders.get(encoding);
         
         if (encoder == null) {
            try {
               // create and cache a new encoder instance for this
               // encoding if one doesn't already exist in the cache
               encoder = new Encoder(encoding);
               encoder.init(_broker, _config);
               _encoders.put(encoding, encoder);
            }
            catch (InitException e) {
               throw new ResourceException("Unable to initialize Encoder for "
                                           + encoding + "; " + e);
            }
            catch (UnsupportedEncodingException uee) {
               throw new NotFoundException("Unsupported encoding: " +
                                           uee.getMessage());
            }
         }
      }
      return encoder;
   }
}
