
package org.webmacro.util;

import java.io.*;
import java.util.HashMap;

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

   final static private HashMap _ecCache = new HashMap();

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

      _encoding = encoding;
      for (int i = 0; i < _size; i++) {
         _cache[i] = new BucketNode(null,null,null);
      }

      byte[] test = "some test string".getBytes(encoding); // throw except.

   }

   public byte[] getEncoding(String s) {
      if (s == null) return null;
      int hash = System.identityHashCode(s);
      int bucket =  Math.abs(System.identityHashCode(s) % _size);
      // int bucket =  Math.abs(s.hashCode() % _size);
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
         EncodingCache ec = new EncodingCache("UTF8", 11, 3);
         BufferedReader in = new 
                  BufferedReader(new InputStreamReader(System.in));
         String s;
         while ((s = in.readLine()) != null) {
            s = s.intern();
            System.out.print("Encoding string: " + s + " --> [");
            System.out.write( ec.getEncoding(s) );
            System.out.println("]");
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}

