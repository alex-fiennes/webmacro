
package org.webmacro.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

class BucketNode {
   final public String string;
   final public byte[] bytes;
   public BucketNode next;
   public BucketNode(String string, byte[] bytes, BucketNode next) {
      this.string = string;
      this.bytes = bytes;
      this.next = next;
   }
}


final public class EncodingCache {

   final private String _encoding;
   final private BucketNode[] _cache;
   final private int _size;
   final private int _length;

   final static private Map _ecCache = new HashMap();

   public EncodingCache(String encoding) 
      throws UnsupportedEncodingException
   {
      this(encoding,1001,11);
   }

   /**
     * Create a new EncodingCache with space for buckets * length
     * encoded strings. Buckets is the number of hashtable buckets
     * the cache will be based on, length is the number of objects
     * that can be held in each bucket. 
     */
   public EncodingCache(String encoding, int buckets, int length) 
      throws UnsupportedEncodingException
   {
      _size = buckets;
      _length = length;
      _cache = new BucketNode[_size];

      if ((encoding == null) || 
         encoding.equalsIgnoreCase("UNICODE") || 
         encoding.equalsIgnoreCase("UNICODEBIG") || 
         encoding.equalsIgnoreCase("UNICODELITTLE") || 
         encoding.equalsIgnoreCase("UTF16")) 
      {
         throw new UnsupportedEncodingException("The encoding you specified is invalid: " + encoding + ". Note that the UNICODE and UTF16 encodings are not supported by WebMacro because they prefix the stream with a market indicating whether the stream is big endian or little endian. Instead choose the byte ordering yourself by using the UTF-16BE or UTF-16LE encodings.");
      }
      _encoding = encoding;
      for (int i = 0; i < _size; i++) {
         _cache[i] = new BucketNode(null,null,null);
      }

      byte[] test = "some test string".getBytes(encoding); // throw except.

   }

   public byte[] getEncoding(String s) {
      if (s == null) return null;
      int hash = System.identityHashCode(s);
      int bucket =  hash % _size;
      if (bucket < 0) bucket = -bucket;
      BucketNode head = _cache[bucket];
      BucketNode cur = head;
      BucketNode match;
      synchronized(head) {
         int i = 0;
         while ((cur.next != null)) {
            if (cur.next.string == s) {
               if (cur == head) return cur.next.bytes;
               match = cur.next;
               cur.next = match.next;  
               match.next = head.next; 
               head.next = match;
               return match.bytes;
            }
            cur = cur.next;
            if (i++ > _length) {
               cur.next = null;
            }
         }
         try {
            match = new BucketNode(s, s.getBytes(_encoding), head.next);
         } catch (UnsupportedEncodingException e) {
            e.printStackTrace(); // never happen: we check in ctor
            return null;
         }
         head.next = match;
         return match.bytes;
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
         EncodingCache ec = new EncodingCache("UTF-16LE", 11, 3);
         BufferedReader in = new 
                  BufferedReader(new InputStreamReader(System.in));
         String s;
         while ((s = in.readLine()) != null) {
            String s1 = s.intern();
            byte b[] = ec.getEncoding(s1);
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

