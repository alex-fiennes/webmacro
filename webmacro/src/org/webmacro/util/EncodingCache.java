
package org.webmacro.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

class Bucket {
   public String string1;
   public String string2;
   public String string3;
   public String string4;
   public String string5;
   public byte[] bytes1;
   public byte[] bytes2;
   public byte[] bytes3;
   public byte[] bytes4;
   public byte[] bytes5;
}


final public class EncodingCache {

   final private String _encoding;
   final private Bucket[] _cache;
   final private int _size;

   final static private Map _ecCache = new HashMap();

   public EncodingCache(String encoding) 
      throws UnsupportedEncodingException
   {
      this(encoding,10001);
   }

   public String getEncodingName() {
      return _encoding;
   }

   /**
     * Create a new EncodingCache with space for buckets * length
     * encoded strings. Buckets is the number of hashtable buckets
     * the cache will be based on, length is the number of objects
     * that can be held in each bucket. 
     */
   public EncodingCache(String encoding, int buckets) 
      throws UnsupportedEncodingException
   {
      _size = buckets;

      _cache = new Bucket[_size];
      for (int i = 0; i < _size; i++) {
         _cache[i] = new Bucket();
      }

      if ((encoding == null) || 
         encoding.equalsIgnoreCase("UNICODE") || 
         encoding.equalsIgnoreCase("UNICODEBIG") || 
         encoding.equalsIgnoreCase("UNICODELITTLE") || 
         encoding.equalsIgnoreCase("UTF16")) 
      {
         throw new UnsupportedEncodingException("The encoding you specified is invalid: " + encoding + ". Note that the UNICODE and UTF16 encodings are not supported by WebMacro because they prefix the stream with a market indicating whether the stream is big endian or little endian. Instead choose the byte ordering yourself by using the UTF-16BE or UTF-16LE encodings.");
      }
      _encoding = encoding;
      byte[] test = "some test string".getBytes(encoding); // throw except.
   }

   public byte[] encode(String s) {
      int hash = System.identityHashCode(s) % _size;
      if (hash < 0) hash = -hash;
      Bucket b = _cache[hash];
      synchronized(b) { 
	      if (b.string1 == s) 
		  return b.bytes1;
	      else if (b.string2 == s) 
		  return b.bytes2;
	      else if (b.string3 == s) 
		  return b.bytes3;
	      else if (b.string4 == s) 
		  return b.bytes4;
	      else if (b.string5 == s) 
		  return b.bytes5;
	      else if (s == null)
                  return null;

         try {
              byte[] buf = s.getBytes(_encoding);

              b.string5 = b.string4;
              b.string4 = b.string3;
              b.string3 = b.string2;
              b.string2 = b.string1;
              b.string1 = s;

              b.bytes5 = b.bytes4;
              b.bytes4 = b.bytes3;
              b.bytes3 = b.bytes2;
              b.bytes2 = b.bytes1;
              b.bytes1 = buf;;

              return buf; 
         } catch (UnsupportedEncodingException e) {
            e.printStackTrace(); // never happen: we check in ctor
            return null;
         }
      }
   }

   public static EncodingCache getInstance(String encoding) 
      throws UnsupportedEncodingException
   {
      synchronized (_ecCache) {
         EncodingCache ec = (EncodingCache) _ecCache.get(encoding);
         if (ec == null) {
            ec = new EncodingCache(encoding);      
            _ecCache.put(encoding,ec);
         }
         return ec;
      }
   }

   public static void main(String arg[]) {

      try {
      /**
         byte[] prefix = getPrefix(arg[0]);
         System.out.println("Prefix for " + arg[0] + " is " + prefix.length + " bytes long");
      */
         EncodingCache ec = new EncodingCache("UTF-16LE", 11);
         BufferedReader in = new 
                  BufferedReader(new InputStreamReader(System.in));
         String s;
         while ((s = in.readLine()) != null) {
            String s1 = s.intern();
            byte b[] = ec.encode(s1);
            String s2 = new String(b, "UTF-16LE");
            System.out.print("Encoding string: " + s1 + " --> [");
            System.out.print( s2 );
            System.out.println("]");
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}

