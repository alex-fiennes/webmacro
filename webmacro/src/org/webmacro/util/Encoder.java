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

import org.webmacro.*;
import org.webmacro.resource.*;

/**
 * An encoder is used to encode strings into a particular encoding in
 * preparation for sending the data as part of a response. An encoder is
 * constructed with a particular output encoding and is then expected to
 * properly encode strings with that output encoding for its lifetime.
 *
 * <p><code>Encoder</code> instances are obtained via the
 * <code>EncoderProvider</code> implementation that is configured at
 * initialization time. The encoder provider is responsible communicating
 * the encoding scheme to be used by the encoder at construct time.
 *
 * <p> The mechanism is factored into the encoder interface to allow for
 * caching encoders with differing caching schemes based on the server
 * environment's requirements or no caching scheme at all.
 *
 * @see EncoderProvider
 * @see SimpleEncoder
 * @since 0.96
 * @author Michael Bayne
 */

public class Encoder implements ResourceLoader 
{
   private String _encoding;
   private CacheManager _cache; 
   private Log _log;

   /**
    * Creates an encoder instance with the supplied encoding.
    *
    * @exception UnsupportedEncodingException Thrown when the underlying
    * Java encoding mechanism does not provide support for the requesting
    * encoding.
    */
   public Encoder(String encoding)
      throws UnsupportedEncodingException
   {
      // enforce some specific rules related to choice of encodings
      if (encoding == null || 
          encoding.equalsIgnoreCase("UNICODE") || 
          encoding.equalsIgnoreCase("UNICODEBIG") || 
          encoding.equalsIgnoreCase("UNICODELITTLE") || 
          encoding.equalsIgnoreCase("UTF16")) {
         String err = "The encoding you specified is invalid: " +
            encoding + ". Note that the UNICODE and UTF16 encodings " +
            "are not supported by WebMacro because they prefix the " +
            "stream with a marker indicating whether the stream is " +
            "big endian or little endian. Instead choose the byte " +
            "ordering yourself by using the UTF-16BE or UTF-16LE " +
            "encodings.";
         throw new UnsupportedEncodingException(err);
      }

      // check to be sure that this encoding is supported. this will
      // throw an UnsupportedEncodingException if the JVM doesn't
      // support the requested encoding
      byte[] test = "some test string".getBytes(encoding);

      // keep track of this for later
      _encoding = encoding;
   }

   public void init(Broker b, Settings config) 
   throws InitException {
      String cacheManager;
      _log = b.getLog("resource", "Object loading and caching");

      cacheManager = b.getSetting("Encoder." + _encoding + ".CacheManager");
      if (cacheManager == null) 
         cacheManager = b.getSetting("Encoder.*.CacheManager");
      if (cacheManager == null || cacheManager.equals("")) {
         _log.info("No cache manager specified for encoding " + _encoding
                   + ", using TrivialCacheManager");
         _cache = new TrivialCacheManager();
      }
      else {
         try {
            Class c = b.classForName(cacheManager);
            _cache = (CacheManager) c.newInstance();
         }
         catch (Exception e) {
            _log.warning("Unable to load cache manager " + cacheManager 
                         + " for encoding type " + _encoding
                         + ", using TrivialCacheManager.  Reason:\n" + e);
            _cache = new TrivialCacheManager();
         }
      }
      _cache.init(b, config, _encoding);
   }

   /**
    * Load an object from permanent storage (or construct it) on
    * demand.
    */
   public Object load(Object query, CacheElement ce)
      throws ResourceException
   {
      try {
         if (query instanceof Block) {
            String[] source = ((Block)query).text;
            byte[][] encoded = new byte[source.length][];
            for (int i = 0; i < source.length; i++) {
               encoded[i] = source[i].getBytes(_encoding);
            }
            return encoded;

         } 
         else if (query instanceof String) {
            return ((String) query).getBytes(_encoding);
         }
         else {
            return query.toString().getBytes(_encoding);
         }

      } catch (UnsupportedEncodingException uee) {
         // this should never happen as we check in the constructor to
         // ensure that the encoding is supported
         throw new ResourceException("Unable to encode: " + uee);
      }
   }

   public Object load(String query, CacheElement ce)
      throws ResourceException {
      try {
         return query.getBytes(_encoding);
      } catch (UnsupportedEncodingException uee) {
         // this should never happen as we check in the constructor to
         // ensure that the encoding is supported
         throw new ResourceException("Unable to encode: " + uee);
      }
   }

   /**
    * Encodes the supplied string using the encoding bound to this encoder
    * at construct time.
    *
    * @return The encoded version of the supplied string.
    *
    * @exception UnsupportedEncodingException Thrown when the underlying
    * Java encoding mechanism does not provide support for the encoding
    * used by this encoder instance.
    */
   public final byte[] encode (String source)
      throws UnsupportedEncodingException
   {
      try {
         return (byte[]) _cache.get(source, this);
      }
      catch (ResourceException e) {
         throw new UnsupportedEncodingException("Encoder: Could not encode; " 
                                                + e);
      }
   }

   /**
    * Encodes the supplied block of strings using the encoding bound to
    * this encoder at construct time.
    *
    * @return The encoded version of the supplied block of strings.
    *
    * @exception UnsupportedEncodingException Thrown when the underlying
    * Java encoding mechanism does not provide support for the encoding
    * used by this encoder instance.
    */
   public final byte[][] encode (Block source)
      throws UnsupportedEncodingException
   {
      try {
         return (byte[][]) _cache.get(source, this);
      }
      catch (ResourceException e) {
         throw new UnsupportedEncodingException("Encoder: Could not encode; " 
                                                + e);
      }
   }

   /**
    * The block class provides a means by which encoder users can encode
    * entire blocks of text at once (and have those encoded blocks
    * cached).
    */
   public static class Block
   {
      public String[] text;

      public Block (String[] text)
      {
         this.text = text;

         // we compute the combined hash of our string array so that we
         // can behave as an efficient, stable key while allowing our
         // strings to maintain happy independent existences
         long strhash = 0;
         for (int i = 0; i < text.length; i++) {
            strhash = (strhash + (long)text[i].hashCode()) %
               Integer.MAX_VALUE;
         }
         _hashCode = (int)strhash;
      }

      public int hashCode ()
      {
         return _hashCode;
      }

      public boolean equals (Object other)
      {
         // we try to be as efficient as possible about this, but to be
         // correct, we have to compare every string
         if (!(other instanceof Block)) {
            return false;
         }

         Block ob = (Block)other;

         // check the obvious things
         if (this == ob || text == ob.text) {
            return true;
         }
         if (text == null || ob.text == null) {
            return false;
         }
         if (ob.text.length != text.length) {
            return false;
         }

         // compare each string individually
         for (int i = 0; i < text.length; i++) {
            if (!text[i].equals(ob.text[i])) {
               return false;
            }
         }

         return true;
      }

      protected int _hashCode;
   }
}
