/*
 * Copyright (C) 1998-2000 Semiotek Inc. All Rights Reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted under the terms of either of the
 * following Open Source licenses: The GNU General Public License, version 2, or any later version,
 * as published by the Free Software Foundation (http://www.fsf.org/copyleft/gpl.html); or The
 * Semiotek Public License (http://webmacro.org/LICENSE.) This software is provided "as is", with NO
 * WARRANTY, not even the implied warranties of fitness to purpose, or merchantability. You assume
 * all risks and liabilities associated with its use. See www.webmacro.org for more information on
 * the WebMacro project.
 */

package org.webmacro;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.webmacro.util.ByteBufferOutputStream;
import org.webmacro.util.Encoder;
import org.webmacro.util.EncoderProvider;

/**
 * FastWriter attempts to optimize output speed in a WebMacro template through several specific
 * optimizations:
 * <ul>
 * <li>FastWriter caches the output in a byte array until you call reset(). You can access the
 * output by one of several methods: toString(), toByteArray(), or writeTo(OutputStream)
 * <li>you can use a unicode conversion cache by calling writeStatic()
 * <li>you can get the contents written to the FastWriter back as an array of bytes INSTEAD of
 * writing to the output stream
 * </ul>
 * <p>
 * <b>Note that the FastWriter requires an explicit flush</b>
 * <p>
 * 
 * @author Marcel Huijkman
 * @version 27-07-2002
 */

public class FastWriter
  extends Writer
{

  /**
   * This encoding is either UTF16-BE or, if the platform does not support it, UTF8. It is a Unicode
   * encoding which can have encoded strings concatenated together.
   */
  public static final String SAFE_UNICODE_ENCODING;

  // find the safe encoding
  static {
    String encoding = "UTF16-BE";
    try {
      encoding.getBytes(encoding);
    } catch (Exception e) {
      encoding = "UTF8";
    }
    SAFE_UNICODE_ENCODING = encoding;
  }

  private final int __defaultBufferSize;
  private final String __encoding; // what encoding we use
  private final Writer __bwriter;
  private final ByteBufferOutputStream __bstream;
  private final Encoder __encoder;

  private OutputStream _out;

  private char[] _cbuf = new char[512];
  private boolean _buffered;

  /**
   * Create a FastWriter to the target outputstream. You must specify a character encoding. You can
   * also call writeTo(), toString(), and toByteArray() to access any un-flush()ed contents.
   */
  public FastWriter(Broker broker,
                    OutputStream out,
                    String encoding) throws UnsupportedEncodingException
  {
    __defaultBufferSize =
        broker.getSettings().getIntegerSetting("FastWriter.DefaultBufferSize", 4096);
    __encoding = hackEncoding(encoding);
    __bstream = new ByteBufferOutputStream(__defaultBufferSize);
    __bwriter = new OutputStreamWriter(__bstream, __encoding);

    // fetch our encoder from the broker
    try {
      __encoder = (Encoder) broker.get(EncoderProvider.TYPE, __encoding);
    } catch (ResourceException re) {
      throw new UnsupportedEncodingException(re.getMessage());
    }

    _buffered = false;

    _out = out;
  }

  /**
   * Workaround for problems with resin-2.0.3, which gives CPxxxx as a character encoding, but java
   * knows only Cpxxxx. This method converts encoding to a form understood by java. <br>
   * We should remove it some time after resin has been fixed
   */
  private static String hackEncoding(String encoding)
  {
    if (encoding.toLowerCase().startsWith("cp") && !encoding.startsWith("Cp")) {
      encoding = "Cp".concat(encoding.substring(2));
    }
    return encoding;
  }

  /**
   * Create a new FastWriter with no output stream target. You can still call writeTo(), toString(),
   * and toByteArray().
   */
  public FastWriter(Broker broker,
                    String encoding) throws java.io.UnsupportedEncodingException
  {
    this(broker,
         null,
         encoding);
  }

  /**
   * Get the character encoding this FastWriter uses to convert characters to byte[]
   */
  public String getEncoding()
  {
    return __encoding;
  }

  /**
   * Get the encoder used by this FastWriter to transform char[] data into byte[] data.
   */
  public Encoder getEncoder()
  {
    return __encoder;
  }

  /**
   * Get the output stream this FastWriter sends output to. It may be null, in which case output is
   * not sent anywhere.
   */
  public OutputStream getOutputStream()
  {
    return _out;
  }

  /**
   * Write characters to the output stream performing slow unicode conversion unless AsciiHack is
   * on.
   */
  @Override
  public void write(char[] cbuf)
      throws java.io.IOException
  {
    __bwriter.write(cbuf, 0, cbuf.length);
    _buffered = true;
  }

  /**
   * Write characters to to the output stream performing slow unicode conversion.
   */
  @Override
  public void write(char[] cbuf,
                    int offset,
                    int len)
      throws java.io.IOException
  {
    __bwriter.write(cbuf, offset, len);
    _buffered = true;
  }

  /**
   * Write a single character, performing slow unicode conversion
   */
  @Override
  public void write(int c)
      throws java.io.IOException
  {
    __bwriter.write(c);
    _buffered = true;
  }

  /**
   * Write a string to the underlying output stream, performing unicode conversion.
   */
  @Override
  public void write(final String s)
      throws java.io.IOException
  {
    final int len = s.length();
    try {
      s.getChars(0, len, _cbuf, 0);
    } catch (IndexOutOfBoundsException e) {
      _cbuf = new char[len + (len - _cbuf.length)];
      s.getChars(0, len, _cbuf, 0);
    }

    __bwriter.write(_cbuf, 0, len);
    _buffered = true;
  }

  /*
   * Write a string to the underlying output stream, performing unicode conversion.
   */
  @Override
  public void write(final String s,
                    final int off,
                    final int len)
      throws java.io.IOException
  {
    try {
      s.getChars(off, off + len, _cbuf, 0);
    } catch (IndexOutOfBoundsException e) {
      _cbuf = new char[len + (len - _cbuf.length)];
      s.getChars(off, off + len, _cbuf, 0);
    }

    __bwriter.write(_cbuf, 0, len);
    _buffered = true;
  }

  /**
   * Write a string to the underlying output stream, performing unicode conversion if necessary--try
   * and read the encoding from an encoding cache if possible.
   */
  public void writeStatic(final String s)
  {
    if (_buffered) {
      bflush();
    }
    try {
      byte[] b = __encoder.encode(s);
      __bstream.write(b, 0, b.length);
    } catch (UnsupportedEncodingException uee) {
      // this should never happen
      uee.printStackTrace();
    }
  }

  /**
   * Write raw bytes to the underlying stream. These bytes must be properly encoded with the
   * encoding returned by getEncoding().
   */
  public void write(byte[] rawBytes)
  {
    if (_buffered) {
      bflush();
    }
    __bstream.write(rawBytes);
  }

  /**
   * Write raw bytes to the underlying stream. Tehse bytes must be properly encoded witht he
   * encoding returned by getEncoding()
   */
  public void write(byte[] rawBytes,
                    int offset,
                    int len)
  {
    if (_buffered) {
      bflush();
    }
    __bstream.write(rawBytes, offset, len);
  }

  private void bflush()
  {
    try {
      __bwriter.flush();
      _buffered = false;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Flush all data out to the OutputStream, if any, clearing the internal buffers. Note that data
   * is ONLY written to the output stream on a flush() operation, and never at any other time.
   * Consequently this is one of the few places that you may actually encounter an IOException when
   * using the FastWriter class.
   */
  @Override
  public void flush()
      throws IOException
  {
    if (_buffered) {
      bflush();
    }

    if (_out != null) {
      writeTo(_out);
      _out.flush();
    }
    __bstream.reset();
  }

  /**
   * Return the number of bytes that would be written out if flush() is called.
   */
  public int size()
      throws IOException
  {
    if (_buffered) {
      bflush();
    }

    return __bstream.size();
  }

  /**
   * Copy the contents written so far into a byte array.
   */
  public byte[] toByteArray()
  {
    if (_buffered) {
      bflush();
    }
    return __bstream.getBytes();
  }

  /**
   * Copy the contents written so far into a String.
   */
  @Override
  public String toString()
  {
    if (_buffered) {
      bflush();
    }
    try {
      return __bstream.toString(__encoding);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace(); // never happen: we already used it
      return null;
    }
  }

  /**
   * Copy the contents written so far to the suppiled output stream
   */
  public void writeTo(OutputStream out)
      throws IOException
  {
    if (_buffered) {
      bflush();
    }
    __bstream.writeTo(out);
  }

  /**
   * Reset the fastwriter, clearing any contents that have been generated so far.
   */
  public void reset(OutputStream out)
  {
    if (_buffered) {
      bflush();
    }
    __bstream.reset();
    _out = out;
  }

  /**
   * Get a new FastWriter. You must then call writeTo(..) before attempting to write to the
   * FastWriter.
   */
  public static FastWriter getInstance(Broker broker,
                                       OutputStream out,
                                       String encoding)
      throws UnsupportedEncodingException
  {
    return new FastWriter(broker, out, encoding);
  }

  /**
   * Get a new FastWriter. You must then call writeTo(..) before attempting to write to the
   * FastWriter.
   */
  public static FastWriter getInstance(Broker broker,
                                       OutputStream out)
      throws UnsupportedEncodingException
  {
    return getInstance(broker, out, SAFE_UNICODE_ENCODING);
  }

  /**
   * Return a FastWriter with the specified encoding and no output stream.
   */
  public static FastWriter getInstance(Broker broker,
                                       String encoding)
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

  @Override
  public void close()
      throws IOException
  {
    flush();
    if (_out != null) {
      _out.close();
      _out = null;
    }
  }
}
