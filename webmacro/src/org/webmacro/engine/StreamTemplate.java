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


package org.webmacro.engine;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;

import org.webmacro.Broker;
import org.webmacro.Context;
import org.webmacro.Template;
import org.webmacro.WM;
import org.webmacro.WMConstants;
import org.webmacro.WebMacro;
import org.webmacro.util.SelectList;

/**
 * StreamTempaltes are constructed with a stream from which they
 * read their data. They can only read the stream once, and after
 * that will throw an exception. Mostly they are useful for testing
 * WebMacro directives on the command line, since a main() is
 * provided which reads the template on standard input.
 */

public class StreamTemplate extends WMTemplate
{

    /**
     * Our stream.
     */
    private Reader _in;
    private String _name = null;

    /**
     * Instantiate a template based on the specified stream
     */
    public StreamTemplate (Broker broker, Reader inStream)
    {
        super(broker);
        _in = inStream;
    }

    /**
     * Instantiate a template based on the specified stream
     * Will use webmacro's default encoding.
     * @param broker broker for this template
     * @param in input stream to read template from
     * @throws IOException if default encoding is unsupported
     */
    public StreamTemplate (Broker broker, InputStream in)
            throws IOException
    {
        this(broker, in, null);
    }

    /**
     * Instantiate a template based on the specified stream
     * If encoding is null, webmacro's default encoding will
     * be used.
     * @param broker broker for this template
     * @param in input stream to read template from
     * @param encoding encoding of input stream
     * @throws IOException if encoding is unsupported
     */
    public StreamTemplate (Broker broker, InputStream in, String encoding)
            throws IOException
    {
        super(broker);
        if (encoding == null)
            encoding = getDefaultEncoding();
        _in = new InputStreamReader(in, encoding);
    }

    /**
     * Get the stream the template should be read from. Parse will
     * call this method in order to locate a stream.
     */
    protected Reader getReader () throws IOException
    {
        if (_in != null)
        {
            Reader ret = _in;
            _in = null;
            return ret;
        }
        else
        {
            throw new IOException("Already read stream.");
        }
    }

    /**
     * Return a name for this template. For example, if the template reads
     * from a file you might want to mention which it is--will be used to
     * produce error messages describing which template had a problem.
     */
    public String toString ()
    {
        String s = null;
        if (_in != null)
            s = _in.toString();
        return (_name != null)
                ? "StreamTemplate:" + _name
                : (s != null) ? "StreamTemplate(" + _in + ")" : "(stream)";
    }

    public String getName ()
    {
        return (_name == null) ? toString() : _name;
    }

    public void setName (String name)
    {
        _name = name;
    }

    /**
     * Simple test.
     */
    public static void main (String arg[])
    {

        // Build a context
        WebMacro wm = null;
        Context context = null;
        String encoding = null;

        try
        {
            wm = new WM();
            context = wm.getContext();
            Object names[] = {"prop"};
            context.setProperty(names, "Example property");
            encoding = wm.getConfig(WMConstants.TEMPLATE_INPUT_ENCODING);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        try
        {
            /*
            HashMap hm = new HashMap();
            hm.put("one", "the first");
            hm.put("two", "the second");
            hm.put("three", "the third");
            context.setBean(hm);
            */
            context.put("helloworld", "Hello World");
            context.put("hello", "Hello");
            context.put("file", "include.txt");
            context.put("today", new Date());
            TestObject[] fruits = {new TestObject("apple", false),
                                   new TestObject("lemon", true),
                                   new TestObject("pear", false),
                                   new TestObject("orange", true),
                                   new TestObject("watermelon", false),
                                   new TestObject("peach", false),
                                   new TestObject("lime", true)};

            SelectList sl = new SelectList(fruits, 3);
            context.put("sl-fruits", sl);

            context.put("fruits", fruits);
            context.put("flipper", new TestObject("flip", false));

            System.out.println("- - - - - - - - - - - - - - - - - - - -");
            System.out.println("Context contains: helloWorld, " + 
                    "hello, file, TestObject[] fruits, " + 
                    "SelectList sl(fruits, 3), TestObject flipper");
            System.out.println("- - - - - - - - - - - - - - - - - - - -");

            Template t1 = new StreamTemplate(wm.getBroker(),
                    new InputStreamReader(System.in));
            t1.parse();

            System.out.println("*** RESULT ***");
            t1.write(System.out, encoding, context);
            System.out.println("*** DONE ***");
            context.clear();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
