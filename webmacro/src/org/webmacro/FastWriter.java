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


package org.webmacro;
import java.io.*;
import java.util.Hashtable;
import org.webmacro.util.*;


/**
  * FastWriter attempts to optimize output speed in a WebMacro template
  * through several specific optimizations: 
  * <ul>
  *   <li> FastWriter caches the output in a byte array until you 
  *        call reset(). You can access the output by one of several
  *        methods: toString(), toByteArray(), or writeTo(OutputStream)
  *   <li> you can turn off unicode conversion by calling setAsciiHack()
  *   <li> you can use a unicode conversion cache by calling writeStatic()
  *   <li> you can get the contents written to the FastWriter back
  *        as an array of bytes INSTEAD of writing to the output stream
  * </ul>
  * Note that if you turn on the asciiHack and then write non-ASCII 
  * data the output will be mangled. 
  * <p>
  * <b>Note that the FastWriter requires an explicit flush</b>
  * <p>
  * If you re-use a FastWriter you must re-use it in a context which
  * uses the SAME unicode conversion. The caches and internal data 
  * structures which the FastWriter allocates are tied to the 
  * encoding it was created with. 
  */

public class FastWriter extends Writer
{

   /**
     * This encoding is either UTF16-BE or, if the platform does not
     * support it, UTF8. It is a Unicode encoding which can have 
     * encoded strings concatenated together. 
     */
   final public static String SAFE_UNICODE_ENCODING;
   final public static int DEFAULT_BUFFER_SIZE = 4096;

   // find the safe encoding
   static {
      String encoding = "UTF16-BE";
      try {
         encoding.getBytes(encoding);
      } catch(Exception e) {
         encoding = "UTF8";
      }
      SAFE_UNICODE_ENCODING = encoding;
   }


   final private String _encoding;      // what encoding we use
   final private Writer _bwriter;
   final private ByteBufferOutputStream _bstream;
   final private Encoder _encoder;
   
   private OutputStream _out;

   /** _open is true iff the FW has been dispensed to a user and not 
     * returned to the pool.  */
   private boolean _open = true;

   private byte[] _buf = new byte[512];

   private char[] _cbuf = new char[512];

   private boolean _encodeProperly;  // are we in fast mode?
   private boolean _buffered;

   final private static Hashtable _writerCache = new Hashtable();
   private Pool _myPool = null;

   /**
    * Create a FastWriter to the target outputstream. You must specify
    * a character encoding. You can also call writeTo(), toString(), 
    * and toByteArray() to access any un-flush()ed contents.
    */
   public FastWriter(Broker broker, OutputStream out, String encoding)
      throws UnsupportedEncodingException
   {
      _encoding = encoding;
      _bstream = new ByteBufferOutputStream(DEFAULT_BUFFER_SIZE);
      _bwriter = new OutputStreamWriter(_bstream, encoding);

      // fetch our encoder from the broker
      try {
         _encoder = (Encoder) broker.get(EncoderProvider.TYPE, encoding);
      } catch (ResourceException re) {
          throw new UnsupportedEncodingException(re.getMessage());
      }

      _encodeProperly = true;
      _buffered = false;

      _out = out;
   }

   /**
     * Create a new FastWriter with no output stream target. You can
     * still call writeTo(), toString(), and toByteArray().
     */
   public FastWriter(Broker broker, String encoding) 
      throws java.io.UnsupportedEncodingException
   {
      this(broker, null, encoding);
   }


   /**
     * Get the character encoding this FastWriter uses to convert
     * characters to byte[]
     */
   public String getEncoding() {
      return _encoding;
   }

   /**
     * Get the encoder used by this FastWriter to transform 
     * char[] data into byte[] data.
     */
   public Encoder getEncoder() {
      return _encoder;
   }

   /**
     * Get the output stream this FastWriter sends output to. It 
     * may be null, in which case output is not sent anywhere.
     */
   public OutputStream getOutputStream() {
      return _out;
   }

   /**
    * Ordinarily an expensive char-to-byte routine is used to convert
    * strings and char[]'s to byte format. If you know that your data
    * is going to be ASCII only for some number of writes, turn on 
    * this AsciiHack and then write the ASCII data. It's much faster. 
    * Remember to turn the AsciiHack off before writing true Unicode
    * characters, otherwise they'll be mangled.
    */
   public void setAsciiHack(boolean on) 
   {
      if (_buffered) bflush();
      _encodeProperly = !on;
   }

   /**
     * Returns true if we are mangling the unicode conversion in an
     * attempt to eek out a bit of extra efficiency.
     */
   public boolean getAsciiHack() 
   {
      return !_encodeProperly;
   }

   /**
     * Write characters to the output stream performing slow unicode
     * conversion unless AsciiHack is on.
     */
   public void write(char[] cbuf) throws java.io.IOException
   {
      if (_encodeProperly) {
         _bwriter.write(cbuf,0,cbuf.length);
         _buffered = true;
      } else {
         int len = cbuf.length;
         if (_buf.length < len) _buf = new byte[len];
         for (int i = 0; i < len; i++) {
            _buf[i] = (byte) cbuf[i];
         }
         _bstream.write(_buf,0,len);
      }
   }

   /**
     * Write characters to to the output stream performing slow unicode
     * conversion unless the AsciiHack is on. 
     */
   public void write(char[] cbuf, int offset, int len) throws java.io.IOException
   {
      if (_encodeProperly) {
         _bwriter.write(cbuf,offset,len);
         _buffered = true;
      } else {
         if (_buf.length < len) _buf = new byte[len];
         int end = offset + len;
         for (int i = offset; i < end; i++) {
            _buf[i] = (byte) cbuf[i];
         }
         _bstream.write(_buf,0,len);
      }
   }

   /**
     * Write a single character, performing slow unicode conversion
     * unless AsciiHack is on.
     */
   public void write(int c) throws java.io.IOException
   {
      if (_encodeProperly ) {
         _bwriter.write(c);
         _buffered = true;
      } else {
         _bstream.write((byte) c);
      }
   }

   /**
     * Write a string to the underlying output stream, performing
     * unicode conversion.
     */
   public void write(final String s) throws java.io.IOException
   {
      final int len = s.length();
      try{
         s.getChars(0,len,_cbuf,0);
      } catch (IndexOutOfBoundsException e) {
         _cbuf = new char[len + (len - _cbuf.length)]; 
         s.getChars(0,len,_cbuf,0);
      }

      if (_encodeProperly) {
         _bwriter.write(_cbuf,0,len);
         _buffered = true;
      } else {
         if (_buf.length < len) _buf = new byte[len];
         for (int i = 0; i < len; i++) {
            _buf[i] = (byte) _cbuf[i];
         }
         _bstream.write(_buf,0,len);
      }
   }

   /*
    * Write a string to the underlying output stream, performing
    * unicode conversion.
    */
   public void write(final String s, final int off, final int len) throws java.io.IOException
   {
      try{
         s.getChars(off,off + len,_cbuf,0);
      } catch (IndexOutOfBoundsException e) {
         _cbuf = new char[len + (len - _cbuf.length)]; 
         s.getChars(off,off + len,_cbuf,0);
      }

      if (_encodeProperly) {
         _bwriter.write(_cbuf,0,len);
         _buffered = true;
      } else {
         if (_buf.length < len) _buf = new byte[len];
         for (int i = 0; i < len; i++) {
            _buf[i] = (byte) _cbuf[i];
         }
         _bstream.write(_buf,0,len);
      }
   }

   /**
     * Write a string to the underlying output stream, performing
     * unicode conversion if necessary--try and read the encoding
     * from an encoding cache if possible.
     */
   public void writeStatic(final String s) 
   {
      if (_buffered) bflush();
      try {
         byte[] b = _encoder.encode(s);
         _bstream.write(b,0,b.length);
      } catch (UnsupportedEncodingException uee) {
         // this should never happen
         uee.printStackTrace();
      }
   }

   /**
     * Write raw bytes to the underlying stream. These bytes must be
     * properly encoded with the encoding returned by getEncoding().
     */
  public void write(byte[] rawBytes) {
     if (_buffered) bflush();
     _bstream.write(rawBytes);
  }

  /**
    * Write raw bytes to the underlying stream. Tehse bytes must be 
    * properly encoded witht he encoding returned by getEncoding()
    */
  public void write(byte[] rawBytes, int offset, int len) {
     if (_buffered) bflush();
     _bstream.write(rawBytes, offset, len);
  }

  public void bflush() {
     try { 
        _bwriter.flush(); 
        _buffered = false; 
     } catch (IOException e) { 
        e.printStackTrace(); 
     }
   }


   /**
     * Flush all data out to the OutputStream, if any, clearing 
     * the internal buffers. Note that data is ONLY written to 
     * the output stream on a flush() operation, and never at 
     * any other time. Consequently this is one of the few places
     * that you may actually encounter an IOException when using
     * the FastWriter class.
     */
   public void flush() throws IOException
   {
      if (_buffered) bflush();

      if (_out != null) {
         writeTo(_out);
         _out.flush();
      }
      _bstream.reset();
   }

   /**
     * Return the number of bytes that would be written out if flush()
     * is called.
     */
   public int size() throws IOException
   {
      if (_buffered) bflush();

      return _bstream.size();
   }


   /**
     * Copy the contents written so far into a byte array.
     */
   public byte[] toByteArray() 
   {
      if (_buffered) bflush();
      return _bstream.getBytes();
   }

   /**
     * Copy the contents written so far into a String. 
     */
   public String toString()  {
      if (_buffered) bflush();
      try {
         return _bstream.toString(_encoding); 
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace(); // never happen: we already used it
         return null;
      }
   }

   /**
     * Copy the contents written so far to the suppiled output stream
     */
   public void writeTo(OutputStream out) throws IOException
   {
      if (_buffered) bflush();
      _bstream.writeTo(out);
   }

   /**
     * Reset the fastwriter, clearing any contents that have 
     * been generated so far.
     */
   public void reset(OutputStream out) {
      if (_buffered) bflush();
      _bstream.reset();
      _out = out;
      _open = true;
   }

   /**
     * Get a new FastWriter. You must then call writeTo(..) before
     * attempting to write to the FastWriter.
     */
   public static FastWriter getInstance(Broker broker, OutputStream out,
                                        String encoding) 
      throws UnsupportedEncodingException
   {
      FastWriter fw = null;
      Pool p = (Pool) _writerCache.get(encoding);
      if (p != null) {
         fw = (FastWriter) p.get();
         if (fw != null) {
            fw.reset(out);
            return fw;
         }
      }
      return new FastWriter(broker, out, encoding);
   }

   /**
     * Return a FastWriter with the specified encoding and no output stream.
     */
   public static FastWriter getInstance(Broker broker, String encoding) 
      throws UnsupportedEncodingException
   {
      return getInstance(broker, null, encoding);
   }

   /**
     * Return a FastWriter with default encoding and no output stream.
     */
   public static FastWriter getInstance(Broker broker) 
   {
      try {
         return getInstance(broker, null, SAFE_UNICODE_ENCODING);
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace(); // never gonna happen
         return null;
      }
   }

   /**
     * Return the FastWriter to the queue for later re-use. You must
     * not use the FastWriter after this call. Calling close() 
     * returns the FastWriter to the pool. If you don't want to
     * return it to the pool just discard it without a close().
     */
   public void close() throws IOException
   {
      flush();
      if (_out != null) {
         _out.close();
         _out = null;
      }
      if (_open) {
        _open = false;
        if (_myPool == null) {
          // get/create the pool this FW should be using
          _myPool = (Pool) _writerCache.get (_encoding);
          if (_myPool == null) {
            _myPool = new UPool (7);
            _writerCache.put(_encoding, _myPool);
          }
        }
        _myPool.put (this);
      }
   }
}
