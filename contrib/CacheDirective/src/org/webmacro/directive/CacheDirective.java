/*
 * Copyright (C) 1998-2001 Semiotek Inc.  All Rights Reserved.  
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
package org.webmacro.directive;

import java.util.*;
import java.io.*;

import org.webmacro.*;
import org.webmacro.engine.*;
import org.webmacro.directive.*;
import org.webmacro.directive.Directive;
import org.webmacro.util.*;

/**
 * A simple directive that caches its block so the block only needs to
 * be evaluated once.<p>
 *
 * Usage:<pre>
 *
 * #cache "KeyName" {
 *   The output of everything in here will be cached (as a String)
 *   using the current encoding.  The next time WM encounters a
 *   #cache "KeyName", this output will be used instead of evaluating
 *   the block.
 * }
 *</pre>
 *<p>
 *
 * Currently, this directive <b>does not</b> expire cached blocks.  It probably
 * needs to be hooked into the CacheManager stuff of WebMacro before it is
 * really useful.<p>
 *
 * A set of static methods are provided for easy access into the cache.  These
 * exist mostly for debugging purposes.
 *
 * @author  ebr@tcdi.com
 */
public class CacheDirective extends Directive
{
    public static final int CACHE_KEY_NAME = 1;
    public static final int CACHE_BLOCK = 2;
    
    /** arg descriptor for this CacheDirective. Takes an RValue which is the
     * cache key name, and a BlockArg which is the output to cache */
    public static final ArgDescriptor[] _args = new ArgDescriptor[] {
        new RValueArg (CACHE_KEY_NAME),
        new BlockArg (CACHE_BLOCK)
    };
    
    public static final DirectiveDescriptor _dd = 
                 new DirectiveDescriptor ("create", null, _args, null);
    
    /**
     * static method required by the WebMacro parser to provide
     * a descriptor about this directive
     */
    public static DirectiveDescriptor getDescriptor () 
    {
        return _dd;
    }    

    /**
     * We provide a few static methods to get access to the cache (mainly 
     * for testing), and this is the instance by which this is made possible
     */
    private static final CacheDirective _instance = new CacheDirective ();
    
    
    /** our cache.  static to all instances of WebMacro.  A synchronized Map.  
        key=cache key name, value=evaluated block as a java.lang.String */
    private static final Map _cache = Collections.synchronizedMap (new HashMap ());

    
    /** Variable of our cache key name */
    private Object _objKeyName;
    
    /** the body we might need to evaluate */
    private Macro _body;
    
    
    /**
     * configure directive
     */
    public Object build (DirectiveBuilder builder,  BuildContext bc) throws BuildException 
    {
        _objKeyName = builder.getArg (CACHE_KEY_NAME, bc);
        _body = (Block) builder.getArg (CACHE_BLOCK, bc);
        
        return this;
    }   
    
    /**
     * evaluate the key arg down to its base object and look it up in our
     * cache.  If it exists, write the data from the cache to <code>fw</code>,
     * otherwise, evaluate and write the block arg to <code>fw</code> <b>and</b>
     * to the cache.
     */
    public void write (FastWriter fw, Context context) throws PropertyException, IOException
    {
        Object key = _objKeyName;
        String bodyOutput;
        
        // evaluate the key name down to its base object
        while (key instanceof Macro)
            key = ((Macro) key).evaluate (context);
        
        // get the cached body string for this key
        bodyOutput = getCachedBody (key);
        
        if (bodyOutput == null)
        {   // body not in cache, so write it to a temporary fast writer
            // and store its encoded .toString() output in our cache
            context.getBroker().getLog ("CacheDirective")
                               .info ("Caching /" + key + "/");
            
            FastWriter fwTmp = FastWriter.getInstance (context.getBroker(), 
                                                      fw.getEncoding ());
            
            // write the body to our temporary FastWriter
            // and store its toString() output
            _body.write (fwTmp, context);
            bodyOutput = fwTmp.toString ();
            fwTmp.close (); // make sure to return this fw to the pool
            
            cacheBody (key, bodyOutput);
        }
        else    // already cached, so brag about it  
            context.getBroker().getLog ("CacheDirective")
                .info ("Cache hit for /" + key + "/");
            
        // at this point the bodyOutput is a String and is in the cache
        fw.write (bodyOutput);
    } 
    
    
    //
    // public static access into the cache
    //
    
    
    /** 
     * get the static instance of the CacheDirective.  Useful only for
     * accessing the cache methods
     */
    public static final CacheDirective getInstance ()
    {
        return _instance;
    }
    
    /** clear the cache */
    public static final void clearCache ()
    {
        _cache.clear ();
    }
    
    /** cache the <code>bodyOutput</code> behind the specified key */
    public static final void cacheBody (Object key, String bodyOutput)
    {
        _cache.put (key, bodyOutput);
    }
    
    /** get the cached body (as a String) for the specified Key */
    public static final String getCachedBody (Object key)
    {
        return (String) _cache.get (key);
    }
    
    /** get an immutable entry set of all entries in the cache */
    public static final Set entrySet ()
    {
        return Collections.unmodifiableSet (_cache.entrySet ());
    }   
}