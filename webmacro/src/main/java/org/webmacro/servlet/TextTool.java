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

package org.webmacro.servlet;

import org.webmacro.Context;
import org.webmacro.ContextTool;
import org.webmacro.PropertyException;
import org.webmacro.WebMacroRuntimeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * A ContextTool for String manipulation.
 *
 * @author Eric B. Ridge (mailto:ebr@tcdi.com)
 */

public class TextTool extends ContextTool
{

    /** our lonely singleton */
    private static final TextTool _instance = new TextTool();


    /**
     * @return the static instance of the TextTool
     */
    public static TextTool getInstance ()
    {
        return _instance;
    }


    //
    // static TextTool methods
    //

    /**
     * Split the <code>input</code> String into a String[] at each
     * instance of <code>delimiter</code>.  The delimiter string is <b>not</b>
     * included in the returned array.<p>
     *
     * If either parameter is <code>null</code>, <code>split()</code> returns
     * a String[] of zero elements.
     */
    public static String[] split (String input, String delimiter)
    {
        if (input == null || delimiter == null)
            return new String[0];

        List<String> l = new ArrayList<String>();
        int delimlen = delimiter.length();
        int idx, lastidx = 0;

        while ((idx = input.indexOf(delimiter, lastidx)) > -1)
        {
            l.add(input.substring(lastidx, idx));
            lastidx = idx + delimlen;
        }
        if (lastidx < input.length()) l.add(input.substring(lastidx));

        return (String[]) l.toArray(new String[0]);
    }

    /**
     * Join the <code>input</code> String[] into a single String, using
     * the specified <code>delimiter</code> between each value of the array.<p>
     *
     * If either parameter is <code>null</code>, <code>join()</code> returns
     * <code>null</code>.
     */
    public static String join (String[] input, String delimiter)
    {
        if (input == null || delimiter == null)
            return null;

        StringBuffer sb = new StringBuffer();
        for (int x = 0; x < input.length; x++)
        {
            if (x > 0)
                sb.append(delimiter);
            sb.append(input[x]);
        }

        return sb.toString();
    }

    /**
     * Replace all occurrences of <code>from</code> to <code>to</code>
     * in <code>src</code>.
     * <P>
     * If any parameter is <code>null</code>, <code>replace()</code> returns
     * the value of <code>src</code>.<p>
     *
     * @return String with all occurrences of <code>from</code> replaced with
     *          <code>to</code> in <code>src</code> or <code>src</code> if
     *          <code>from</code> not found
     */
    public static String replace (String src, String from, String to)
    {
        if (src == null || from == null || to == null)
            return src;

        int fromlen = from.length();
        int idx, lastidx = 0;

        StringBuffer newstr = new StringBuffer();

        while ((idx = src.indexOf(from, lastidx)) > -1)
        {
            newstr.append(src.substring(lastidx, idx));
            newstr.append(to);
            lastidx = idx + fromlen;
        }
        if (lastidx == 0)
            return src;
        else
            newstr.append(src.substring(lastidx));

        return newstr.toString();
    }

    /**
     * URLEncode the specified <code>input</code> String.<p>
     *
     * If <code>input</code> is <code>null</code>, <code>URLEncode()</code>
     * will return <code>null</code>.
     *
     * @see java.net.URLEncoder
     */
    public static String URLEncode (String input)
    {
        if (input == null)
            return null;
        try {
            return java.net.URLEncoder.encode(input, System.getProperty( "file.encoding" ));
        } catch (UnsupportedEncodingException e) {
            throw new WebMacroRuntimeException( "WebMacro bug System.file.encoding not supported", e );
        }
    }

    /**
     * Decode a URLEncoded <code>input</code> String.
     * Assumes that the URL was encoded with URLEncode above.
     * 
     * <p>
     *
     * If <code>input</code> is <code>null</code>, <code>URLEncode()</code>
     * will return <code>null</code>.
     *
     * @see java.net.URLDecoder
     */
    public static String URLDecode (String input)
    {
        if (input == null)
            return  null;
        try {
            return java.net.URLDecoder.decode(input, System.getProperty( "file.encoding" ));
        } catch (UnsupportedEncodingException e) {
            throw new WebMacroRuntimeException( "WebMacro bug System.file.encoding not supported", e );
        }
    }

    /**
     * HTMLEncode will encode the specified <code>input</code> String
     * per the magic of <code>org.webmacro.util.HTMLEscaper</code>.<p>
     *
     * If the <code>input</code> is <code>null</code>, <code>HTMLEncode()</code>
     * will return <b><code>an empty string</code></b>.
     *
     * @see org.webmacro.util.HTMLEscaper
     */
    public static String HTMLEncode (String input)
    {
        return input == null ? "" : org.webmacro.util.HTMLEscaper.escape(input);
    }

    /**
     * Format a <code>java.util.Date</code> object using the specified date
     * format string.
     *
     * @see java.util.Date
     * @see java.text.SimpleDateFormat
     */
    public static String formatDate (Date date, String format)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    /**
     * Write the bytes of the specified InputStream into a String using
     * the default character encoding of your platform.  Upon completion, (or in
     * the event of an Exception) this method will close the input stream.<p>
     *
     * @throws java.io.IOException if the stream cannot be read
     */
    public static String streamToString (InputStream in) throws IOException
    {
        return streamToString(in, null);
    }

    /**
     * Write the bytes of the specified InputStream into a String using
     * the specified character encoding.  Upon completion (or in the event of
     * an Exception), this method will close the input stream.<p>
     *
     * @throws java.io.IOException if the stream cannot be read
     */
    public static String streamToString (InputStream in, final String encoding) throws IOException
    {
        if (in == null)
            return null;
        StringBuffer sb = new StringBuffer();
        try
        {
            int cnt;
            byte[] buff = new byte[4096];
            while ((cnt = in.read(buff)) > -1)
            {
                if (encoding != null)
                    sb.append(new String(buff, 0, cnt, encoding));
                else
                    sb.append(new String(buff, 0, cnt));
            }
        }
        finally
        {
            in.close();
        }

        return sb.toString();
    }

    /**
     * convert a <code>byte[]</code> to a String using the system-default
     * encoding
     */
    public static String bytesToString (byte[] bytes)
    {
        return new String(bytes);
    }

    /**
     * convert a <code>byte[]</code> to a String using the specified
     * encoding
     */
    public static String bytesToString (byte[] bytes, String encoding) throws UnsupportedEncodingException
    {
        return new String(bytes, encoding);
    }


    /** remove any leading and trailing whitespace from a string */
    public static String trim (String s)
    {
        return s.trim();
    }

    /** remove any leading whitespace from a string */
    public static String ltrim (String s)
    {
        if (s == null) return null;
        for (int i = 0; i < s.length(); i++)
        {
            if (!Character.isWhitespace(s.charAt(i))) return s.substring(i);
        }
        // if all WS return empty string
        return "";
    }

    /** remove the trailing whitespace from a string */
    public static String rtrim (String s)
    {
        if (s == null) return null;
        for (int i = s.length() - 1; i > -1; i--)
        {
            if (!Character.isWhitespace(s.charAt(i))) return s.substring(0, i + 1);
        }
        // if all WS return empty string
        return "";
    }

    /** converts a block of text into an array of strings
     * by tokenizing using \r\n as the delimiter
     * If no \r\n, it will look for \n or \r and use them instead.
     */
    public static String[] getLines (String block)
    {
        if (block == null) return null;
        String delim = "\r\n";
        if (block.indexOf("\r\n") == -1)
        {
            if (block.indexOf('\n') > -1)
                delim = "\n";
            else
                delim = "\r";
        }
        return TextTool.split(block, delim);
    }

    /** convert an array of strings into a block of text delimited by \r\n */
    public static String makeBlock (String[] lines)
    {
        if (lines == null || lines.length == 0) return null;
        if (lines.length == 1) return lines[0];
        StringBuffer sb = new StringBuffer(lines[0]);
        for (int i = 1; i < lines.length; i++)
        {
            sb.append('\r').append('\n').append(lines[i]);
        }
        return sb.toString();
    }


    /** remove the leading and trailing whitespace from each line in a block of text */
    public static String trimBlock (String block)
    {
        if (block == null) return null;
        String[] lines = getLines(block);
        for (int i = 0; i < lines.length; i++) lines[i] = trim(lines[i]);
        return makeBlock(lines);
    }

    /** remove the leading whitespace from each line in a block of text */
    public static String ltrimBlock (String block)
    {
        if (block == null) return null;
        String[] lines = getLines(block);
        for (int i = 0; i < lines.length; i++) lines[i] = ltrim(lines[i]);
        return makeBlock(lines);
    }

    /** remove the trailing whitespace from each line in a block of text */
    public static String rtrimBlock (String block)
    {
        if (block == null) return null;
        String[] lines = getLines(block);
        for (int i = 0; i < lines.length; i++) lines[i] = rtrim(lines[i]);
        return makeBlock(lines);
    }

    /** Returns a new  string which is in upper case. */
    public static String toUpperCase (String s)
    {
        return s.toUpperCase();
    }

    /** Returns a new  string which is in lower case. */
    public static String toLowerCase (String s)
    {
        return s.toLowerCase();
    }



    //
    // ContextTool implementation methods
    //

    /** default contsructor.  Does nothing */
    public TextTool ()
    {
    }

    /**
     * public constructor.  Does nothing.  The TextTool doesn't
     * interact with the Context, so it is ignored
     */
    public TextTool (Context context)
    {
    }

    /**
     * Tool initialization method.  The TextTool doesn't
     * interact with the context, so the <code>context</code>
     * parameter is ignored.
     */
    @Override
    public Object init (Context context) throws PropertyException
    {
        return TextTool.getInstance();
    }


    //
    //

    public static void main (String[] args) throws Exception
    {
        String input1 = "This is a test";
        String input2 = "This is a test";
        String[] split = TextTool.split(input2, " ");

        System.err.println(TextTool.replace(input1, "is", "isn't"));

        for (int x = 0; x < split.length; x++)
            System.err.println("/" + split[x] + "/");

        String s1 = "alpha\r\nbeta\r\ndelta\r\ngamma";
        String[] split2 = TextTool.split(s1, "\r\n");
        System.err.println("split2=" + Arrays.asList(split2));
    }
}
