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
import java.io.OutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

final public class ByteBufferOutputStream extends OutputStream 
{
   private byte[] _buf;
   private int _pos;

   /**
     * Create a new ByteBuffer with the specified capacity
     */
   public ByteBufferOutputStream(int size) 
   {
      _buf = new byte[size];   
      _pos = 0;
   }

   /**
     * Clear the contents of the byte buffer
     */
   public void reset() {
      _pos = 0;
   }

   public void write(int i) {
      write((byte) i);
   }

   /**
     * Copy an array of bytes on to the end of the buffer
     */
   public void write(byte[] b) {
      int len = b.length;
      ensureCapacity (len);
      System.arraycopy (b, 0, _buf, _pos, len);
      _pos += len;
   }

   /**
     * Copy an array of bytes on to the end of the buffer
     */
   public void write(byte[] b, int offset, int len) {
      ensureCapacity (len);
      System.arraycopy (b, 0, _buf, _pos, len);
      _pos += len;
   }

   /**
     * Append a single byte
     */
   public void write(byte b) {
      ensureCapacity (1);
      _buf[_pos] = b;
      _pos++;
   }

   /**
     * Make sure the buffer contains space for len more bytes.
     */
   public final void ensureCapacity(int len) {
      if (_buf.length < _pos+len) {
         int blen = _buf.length;

         while (blen < _pos+len) {
            blen *= 2;
         }

         byte[] tmp = new byte[blen];
         System.arraycopy(_buf,0,tmp,0,_pos);
         _buf = tmp;
      }
   }

   /**
     * How many bytes currently in the buffer
     */
   public int size() {
      return _pos;
   }

   /**
     * Get the bytes in the buffer. Note that you are getting the 
     * live buffer. You also need to call size() to find out how 
     * many of these bytes are significant. If you just want a 
     * byte array call getBytes() instead--that will allocate a 
     * new one for you.
     */
   public byte[] getBuffer() {
      return _buf;
   }

   /**
     * Allocate a new byte[] and fill it with the contents of the
     * current byte buffer. If you want the live byte buffer instead
     * of this newly allocated copy call getBuffer() instead.
     */
   public byte[] getBytes() {
      byte[] ret = new byte[_pos];
      System.arraycopy(_buf,0,ret,0,_pos);
      return ret;
   }

   /**
     * Convert the bytes to a String using the default encoding
     */
   public String toString() {
      return new String(_buf, 0, _pos);
   }

   /**
     * Convert the bytes to a String using the specified encodign
     */
   public String toString(String encoding) throws UnsupportedEncodingException
   {
      return new String(_buf, 0, _pos, encoding);
   }

   /**
     * Write the bytes to the specified output stream
     */
   public void writeTo(OutputStream out) throws IOException 
   {
      out.write(_buf,0,_pos);
   }

   public static void main(String arg[]) {
      try {

         ByteBufferOutputStream bb = new ByteBufferOutputStream(5);

         bb.write((byte) 'T');
         bb.write((byte) 'h');
         bb.write((byte) 'i');
         bb.write((byte) 's');
         bb.write((byte) ' ');
         bb.write((byte) 'i');
         bb.write((byte) 's');
         bb.write((byte) ' ');
         bb.write((byte) 'a');
         bb.write((byte) ' ');
         bb.write((byte) 't');
         bb.write((byte) 'e');
         bb.write((byte) 's');
         bb.write((byte) 't');
         bb.write((byte) '\n');
         bb.write("This is the second line of the byte buffer".getBytes());
         bb.write((byte) '\n');
         bb.write("This is the third line\n".getBytes());
         bb.write("This is the fourth line\n".getBytes());
         bb.write((byte) 'E');
         bb.write((byte) 'N');
         bb.write((byte) 'D');
         bb.write((byte) '.');
         bb.write((byte) '\n');

         System.out.println("Byte buffer as a string: [" + bb + "]");
         System.out.print("Byte buffer written to a stream: [");
         bb.writeTo(System.out);
         System.out.print("]");
         System.out.println("DONE");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}

