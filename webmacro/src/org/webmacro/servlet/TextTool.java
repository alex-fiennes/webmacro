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

import java.util.*;
import java.text.*;
import java.io.*;

import org.webmacro.Context;
import org.webmacro.ContextTool;
import org.webmacro.PropertyException;

/**
 * A ContextTool for String manipulation.
 *
 * @author Eric B. Ridge (mailto:ebr@tcdi.com)
 */

public class TextTool implements ContextTool 
{
    /** our lonely singleton */
    private static final TextTool _instance = new TextTool ();

    
    /** 
     * @return the static instance of the MathTool
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
    public static final String[] split (String input, String delimiter)
    {
        if (input == null || delimiter == null)
            return new String[0];
        
        List l = new ArrayList ();
        int delimlen = delimiter.length();
        int idx, lastidx=0;
        
        while ( (idx = input.indexOf(delimiter, lastidx)) > -1)
        {
            l.add (input.substring (lastidx, idx));
            lastidx = idx+delimlen;
        }
        if (lastidx <input.length()) l.add (input.substring(lastidx));
        
        return (String[]) l.toArray (new String[0]);
    }
   
    /**
     * Join the <code>input</code> String[] into a single String, using
     * the specified <code>delimiter</code> between each value of the array.<p>
     *
     * If either parameter is <code>null</code>, <code>join()</code> returns
     * <code>null</code>.
     */
    public static final String join (String[] input, String delimiter)
    {
        if (input == null || delimiter == null)
            return null;
        
        StringBuffer sb = new StringBuffer ();
        for (int x=0; x<input.length; x++)
        {
            if (x>0)
                sb.append (delimiter);
            sb.append (input[x]);
        }
        
        return sb.toString ();
    }

   /**
    * replace all occurrences of <code>from</code> to <code>to</code> 
    * in <code>src</code>.<P>
    *
    * If any paramer is <code>null</code>, <code>replace()</code> returns
    * the value of <code>src</code>.<p>
    *
    * @returns String with all occurrences of <code>from</code> replaced with 
    *          <code>to</code> in <code>src</code> or <code>src</code> if 
    *          <code>from</code> not found
    */
    public static String replace (String src, String from, String to)
    {
        if (src == null || from == null || to == null) 
            return src;

        int fromlen = from.length ();
        int idx, lastidx=0;

        StringBuffer newstr = new StringBuffer ();

        while ( (idx = src.indexOf (from, lastidx)) > -1)
        {
            newstr.append (src.substring (lastidx, idx));
            newstr.append (to);
            lastidx = idx+fromlen;
        }
        if (lastidx == 0) return src;
        else newstr.append (src.substring (lastidx));

        return newstr.toString ();
    }
    
    /**
     * URLEncode the specified <code>input</code> String.<p>
     *
     * If <code>input</code> is <code>null</code>, <code>URLEncode()</code>
     * will return <code>null</code>.
     *
     * @see java.net.URLEncoder
     */
    public static final String URLEncode (String input)
    {
        return input == null ? null : java.net.URLEncoder.encode (input);
    }
    
    /**
     * Decode a URLEncoded <code>input</code> String.<p>
     *
     * If <code>input</code> is <code>null</code>, <code>URLEncode()</code>
     * will return <code>null</code>.
     *
     * @see java.net.URLDecoder
     */
    public static final String URLDecode (String input)
    {
      try { 
        return input == null ? null : java.net.URLDecoder.decode (input);
      }
      catch (Exception e) {
        return null;
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
    public static final String HTMLEncode (String input)
    {
        return input == null ? "" : org.webmacro.util.HTMLEscaper.escape (input);
    }
    
    /**
     * Format a <code>java.util.Date</code> object using the specified date 
     * format string.
     *
     * @see java.util.Date
     * @see java.text.SimpleDateFormat
     */
    public static final String formatDate (Date date, String format)
    {
        SimpleDateFormat formatter = new SimpleDateFormat (format);
        return formatter.format (date);
    }

    /**
     * Write the bytes of the specified InputStream into a String using
     * the default character encoding of your platform.  Upon completion, (or in 
     * the event of an Exception) this method will close the input stream.<p>
     *
     * @throws java.io.IOException if the stream cannot be read
     */
    public static final String streamToString (InputStream in) throws IOException
    {
        return streamToString (in, null);
    }

    /**
     * Write the bytes of the specified InputStream into a String using
     * the specified character encoding.  Upon completion (or in the event of
     * an Exception), this method will close the input stream.<p>
     *
     * @throws java.io.IOException if the stream cannot be read
     */
    public static final String streamToString (InputStream in, final String encoding) throws IOException
    {
        if (in == null)
            return null;
        StringBuffer sb = new StringBuffer ();
        try
        {
            int cnt;
            byte[] buff = new byte[4096];
            while ( (cnt = in.read (buff)) > -1)
            {
                if (encoding != null)
                    sb.append (new String (buff, 0, cnt, encoding));
                else
                    sb.append (new String (buff, 0, cnt));
            }
        }
        finally
        {
            in.close ();
        }
        
        return sb.toString ();
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
    public Object init(Context context) throws PropertyException 
    {
        return TextTool.getInstance ();
    }

    /**
     * Perform necessary cleanup work
     */
    public void destroy(Object o) 
    {
    }
    
    
    //
    // 
    
    public static void main (String[] args) throws Exception
    {
        TextTool tt = TextTool.getInstance ();
        String input1 = "This is a test";
        String input2 = "This is a test";
        String[] split = tt.split (input2, " ");
        
        System.err.println (tt.replace (input1, "is", "isn't"));
        
        for (int x=0; x<split.length; x++)
            System.err.println ("/" + split[x] + "/");
    }
}
