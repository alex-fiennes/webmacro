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

package org.webmacro.directive;

import java.io.*;
import java.util.StringTokenizer;

import org.webmacro.*;
import org.webmacro.engine.*;

/**
 * IncludeDirective allows you to include other text files or Templates into
 * the current Template.<p>
 * 
 * Syntax:<pre>
 *    #include [as text|template] quoted-string-variable
 *</pre>
 *
 * <code>IncludeDirective</code> has 3 modes of operation which are detailed 
 * below.<p>
 *
 * Please note that #include is now the preferred way to include files or
 * templates.  Although you can still use #parse (in the same manner as 
 * described below), its use has been deprecated.<p>
 *
 * <b>Including as text</b><br>
 * Including a file as text causes the raw bytes of the file to be included
 * in the current template.  The file is <b>not</b> considered to be a Template,
 * and is not parsed by WebMacro.  This allows you to include external files
 * that contain javascript or stylesheets without fear of mangling by WebMacro's
 * parser.<p>
 *
 * Files included as text are found using the default 
 * <code>URLProvider</code>.  If the URLProvider cannot find the file, the 
 * Broker is asked to find it.  This allows you to include files from any
 * of the following places:
 * <li>other websites
 * <li>local filesystem
 * <li>active classpath<p>
 *
 * Examples:<pre>
 * 
 *   // include your system password file
 *   #include as text "/etc/password"
 *
 *   // include the WebMacro homepage
 *   #include as text "http://www.webmacro.org"
 *
 *   // inlude a file that is in your classpath
 *   #include as text "somefile.txt"
 *
 * </pre><br>
 * 
 * <b>Including as template</b><br>
 * Including a file as template causes the file to be parsed and evaluated
 * by WebMacro using the current <code>Context</code>.  The evaluated output of 
 * the Template is included in your main template.<p>
 *
 * Files included as template are found in your <code>TemplatePath</code>.
 * This path, if not explicitly set in <code>WebMacro.properties</code>, defaults
 * to the system classpath (standalone and JSDK 1.0), or the "web-app" directory
 * (JSDK 2.x).<p>
 *
 * Examples:<pre>
 *
 *   // include a global header template
 *   #include as template "header.wm"
 *
 *   // include a header template from a subdirectory of your TemplatePath
 *   #include as template "common/header.wm"
 *
 *   // include a header template using an absolute path in your TemplatePath
 *   #include as template "/mysite/common/header.wm"
 *
 * </pre>
 *
 * <a name="noqual"><b>Including without qualification</b><br></a>
 * Including a file without first qualifing it as a <i>text</i> file or 
 * <i>template</i> causes <code>IncludeDirective</code> to make some educated
 * guesses about what type of file it is.<p>
 *
 * If the filename contains <code>://</code>, it is assumed to be a URL and is 
 * treated as if it were a <code>text</code> file.<p>
 *
 * Else if the filename ends with any of the configured template file extensions
 * (see below), it is assumed to be a template.<p>
 *
 * Else it is assumed to be <code>text</code>.<p>
 *
 * Examples:<pre>
 *
 *    // include homepage of WebMacro -- assumed to be "as text"
 *    #include "http://www.webmacro.org/"
 *
 *    // include a template -- assumed to be "as template"
 *    #include "header.wm"
 *
 *    // include a text file from local filesystem -- assumed to be "as text"
 *    #include "somefile.txt"
 *
 * </pre>
 *
 * 
 * <b><code>WebMacro.properties</code> Configuration Options</b><br>
 * The <code>IncludeDirective</code> has a set of configuration options that
 * make its syntax easier to use and enforce strict backwards compatibility
 * with earlier versions of WebMacro.  These settings apply to both
 * #include and the <b>deprecated</b> #parse directives.  Simply substitute the 
 * property prefix <code>include</code> with <code>parse</code> to configure 
 * settings differently per directive.<p>
 *
 * <b><code>include.Lazy</code> = true | false  (default = true)</b><br>
 * This is an optimization feature that specifies when files should be 
 * included.<p>
 *
 * If <code>include.Lazy</code> is <code>true</code>, files are included 
 * during runtime evaluation of the main template.  This allows you to take
 * advantage of the <i>ReloadOnChange</i> features of the 
 * <code>TemplateProvider</code>.  Most useful during development.<p>
 *
 * If set to false, files are included during the parsing step of the main
 * template.  Although changes in the included files will not be seen, the 
 * advantage to setting <code>include.Lazy</code> to <code>false</code> is
 * performance.  Included files are found and/or parsed only once.  Very
 * useful for production settings that don't need to recognize changed
 * templates.<p>
 *
 * <b><code>include.StrictCompatibility</code> = true | false (default = false)</b><br>
 * This forces <code>IncludeDirective</code>, when being used as #include, 
 * to be 100% compatible with previous versions of WebMacro.<p>
 *
 * If set to <code>true</code> unqualified #include'd files will always be 
 * assumed to be <code><b>text</b></code>.<p>
 *
 * If set to <code>false</code> (the <b>default</b>), unqualified #include'd
 * files will have their type (text or template) determined using the rules
 * outlined above in <b>Including without qualification</b>
 *
 * <b><code>parse.StrictCompatibility</code> = true | false (default = true)</b><br>
 * This forces <code>IncludeDirective</code>, when being used as #parse, 
 * to be 100% compatible with previous versions of WebMacro.<p>
 *
 * If set to <code>true</code> (the <b>default</b>), unqualified #parse'd files 
 * will always be assumed to be <code><b>Templates</b></code>.<p>
 *
 * If set to <code>false</code> (the <b>default</b>), unqualified #include'd
 * files will have their type (text or template) determined using the rules
 * outlined above in <a href="#noqual">Including without qualification</a>.<p>
 *
 * Please remember that #parse has been <b>deprecated</b>.  You should use
 * #include in <b>all cases</b> when including files.<p>
 *
 * <b><code>include.TemplateExtensions</code> = list of file extensions</b><br>
 * This list of file extensions is used to determine the file type of an
 * unqualified #include'd file.<p>
 *
 * The default set of extensions are: <code>wm, wmt, tml</code>
 *
 * @author <a href=mailto:ebr@tcdi.com>Eric B. Ridge</a>
 * @since the beginning, but consolidated with #parse post 0.97
 * 
 */
public class IncludeDirective extends Directive
{

   private static final int PARSE_AS_K = 1;
   private static final int PARSE_TEMPLATE_K = 2;
   private static final int PARSE_TEXT_K = 3;
   private static final int PARSE_FILENAME = 4;
   
   private static final ArgDescriptor[] _args = new ArgDescriptor[] {
      new OptionalGroup (3),
         new KeywordArg (PARSE_AS_K, "as"),
         new OptionalGroup(1),
            new KeywordArg (PARSE_TEMPLATE_K, "template"),
         new OptionalGroup(1),
            new KeywordArg (PARSE_TEXT_K, "text"),
      new QuotedStringArg (PARSE_FILENAME),
   };
   
   private static final DirectiveDescriptor _desc = 
                           new DirectiveDescriptor ("parse", null, _args, null);
   
   public static DirectiveDescriptor getDescriptor () {
      return _desc;
   }

   //
   // these values are customized for this directive during build()
   //
   
   /** place hodler for the Lazy configuration key name */
   private String LAZY_NAME = ".Lazy";
   /** place holder for the StrictCompatibility configuration key name */
   private String COMPATIBILITY_NAME = ".StrictCompatibility";
   /** place holder for the TemplateExtensions configuration key name */
   private String TEMPLATE_EXTENSIONS_NAME = ".TemplateExtensions";
   

   /** the file to include is a Template */
   public static final int TYPE_TEMPLATE = 0;
  /** the file to include is a static file */
   public static final int TYPE_TEXT = 1;
  /** the file to include is unknown, so we'll need to figure it out
   * based on filename */
   public static final int TYPE_DYNAMIC = 2;
  
   /** the included file type.  one of TYPE_TEMPLATE, TEXT, or DYNAMIC */
   protected int _type;
   /** the filename as a Macro, if the filename arg is a Macro */
   protected Macro _macFilename;
   /** the filename as a String, if it is something we can determine during 
       build() */
   protected String _strFilename;
   /* the name given to the directive by webmacro configuration.  Used in 
      conjuction with the <code>StrictCompatibility</code>  configuration */
   protected String _directiveName;
   
   /**
    * Build this use of the directive.<p>
    *
    * We perform some build-time optimizations if the <code>Lazy</code> 
    * configuration parameter is <code>false</code> and if we are able to 
    * determine the filename during build().  In this case, the included
    * file (or template) is found (and/or parsed) and returned from this method.
    * Note that this eliminates the ability for #include'd files/template
    * to "reload on change", but this is configurable and documented here 
    * and above.
    */
   public final Object build (DirectiveBuilder builder,  BuildContext bc) throws BuildException {
      Broker broker = bc.getBroker ();
      
      // build configuration key names, since they're based
      // on the configured name of this directive
      _directiveName = builder.getName ();
      LAZY_NAME = _directiveName + LAZY_NAME;
      COMPATIBILITY_NAME = _directiveName + COMPATIBILITY_NAME;
      TEMPLATE_EXTENSIONS_NAME = _directiveName + TEMPLATE_EXTENSIONS_NAME;

      // warn if used as #parse.  eventually, this should be removed.
      if (_directiveName.equals ("parse"))
         broker.getLog ("IncludeDirective")
               .warning ("#parse has been deprecated.  "
                    + "Use #include as template \"filename.wm\"");
                                         
      
      // determine what type of file we need to deal with
      if (builder.getArg (PARSE_AS_K, bc) != null) {
         String typeKeyword = (String) builder.getArg (PARSE_TEXT_K, bc);
         if (typeKeyword != null) {
            _type = TYPE_TEXT;
         } else { // not a file
            typeKeyword = (String) builder.getArg (PARSE_TEMPLATE_K, bc);
            if (typeKeyword != null) { // must be a template
               _type = TYPE_TEMPLATE;
            } else { // included "as" keyword, but no other keyword after it
                     // maybe should throw exception here?
                     // maybe should write the arg descriptor differently?
               _type = TYPE_DYNAMIC;
            }
         }
      } else { // didn't specify. default to dynamic
         _type = TYPE_DYNAMIC;
      }

      // if the file type wasn't specified _and_ we're configured
      // to be strictly compatible with legacy #parse/#include directives
      // adjust the file type accordingly
      if (_type == TYPE_DYNAMIC && isStrictlyCompatible (broker)) {
         if (_directiveName.equals ("parse"))
            _type = TYPE_TEMPLATE;
         else if (_directiveName.equals ("include"))
            _type = TYPE_TEXT;
      }
      
      // if the filename passed to us was a Macro (needs to be evaluated later)
      // then store it as _macFilename.  Otherwise, assume it's a String
      // and we'll just use that string as the filename
      Object o = builder.getArg (PARSE_FILENAME, bc);
      if (o instanceof Macro) {
         _macFilename = (Macro) o;
      } else {
         _strFilename = o.toString ();
         if (_strFilename == null || _strFilename.length() == 0)
            throw new BuildException ("#" + _directiveName + ": " 
                                    + "Filename cannot be null or empty");
         if (_type == TYPE_DYNAMIC)
            _type = guessType (broker, _strFilename);

         // if we're not configured to be lazy
         // get whatever the user specified and return it now
         // it's a build time optimization to avoid getting/parsing the file
         // during runtime.  Also allows for parsing of all included templates
         // at build time... which can give you parse excptions upfront.
         // Note: not being lazy doesn't allow for auto reloading of
         // included files
         if (!isLazy (broker)) {
            try {
               return getThingToInclude (broker, _type, _strFilename);
            } catch (Exception e) {
               throw new BuildException ("#" + _directiveName + ": " 
                                       + "Exception including " + _strFilename,
                                         e);
            }
         }
      }
      
      // we are configured to be lazy, or we couldn't determine the filename
      // during the build() process (b/c it is a Macro)
      return this;
   }
   
   /**
    * Write out the included file to the specified FastWriter.  If the 
    * included file is actually a template, it is evaluated against the 
    * <code>context</code> parameter before being written to the FastWriter
    */
   public void write (FastWriter out, Context context) throws PropertyException, IOException {
      Broker broker = context.getBroker ();
      
      // the filename arg passed to us was a Macro, so 
      // evaluate and check it now
      if (_macFilename != null) {
         _strFilename = _macFilename.evaluate(context).toString();
         if (_strFilename == null || _strFilename.length() == 0) {
            throw new PropertyException ("#" + _directiveName + ": "
                                       + _strFilename 
                                       + " is an invalid filename");
         }
      }

      // this should only be true if StrictCompatibility is set to false
      // and "as text|template" wasn't specified in the arg list
      if (_type == TYPE_DYNAMIC)
         _type = guessType (broker, _strFilename);
      
      Object toInclude = getThingToInclude (broker, _type, _strFilename);
      switch (_type) {
         case TYPE_TEMPLATE:
            ((Template) toInclude).write (out, context);
            break;
            
         case TYPE_TEXT:
            out.write (toInclude.toString());
            break;

         // should never happen
         default:
            throw new PropertyException ("#" + _directiveName + ": " + _type 
                                      + " is an unrecognized file type");
      }
   }

   /**
    * are we configured to lazily include the file/template (meaning during 
    * expansion)?  If not, we end up inserting the template/file contents
    * during build().  Default is <b>yes</b>, be lazy.
    */
   protected boolean isLazy (Broker b) {
      return b.getSettings().getBooleanSetting (LAZY_NAME, true);
   }
   
   /**
    * are we strictly compatible with previous versions of both #parse
    * and #include?  Default is <b>no</b>, we are not strictly compatible
    * b/c we can get pretty close by dynamically figuring out the file type
    * and b/c #parse has been deprecated.
    */
   protected boolean isStrictlyCompatible (Broker b) {
      return b.getSettings().getBooleanSetting (COMPATIBILITY_NAME, false);
   }
   
   /**
    * get an array of Template file extensions we should use, if type==dynamic,
    * to decide if the specified file is a template or not
    */
   protected String[] getTemplateExtensions (Broker b) {
      String[] ret = (String[]) b.getBrokerLocal (TEMPLATE_EXTENSIONS_NAME);
      
      if (ret == null) {
         String prop = b.getSettings()
                          .getSetting (TEMPLATE_EXTENSIONS_NAME, "wm");
         StringTokenizer st = new StringTokenizer (prop, ",; ");
         ret = new String[st.countTokens ()];
         int x=0;
         while (st.hasMoreElements ()) {
            ret[x] = st.nextToken ();
            x++;
         }
         
         b.setBrokerLocal (TEMPLATE_EXTENSIONS_NAME, ret);
      }
      
      return ret;
   }

   /**
    * if the filename contains <i>://</i> assume it's a file b/c it's probably
    * a url.<p>
    *
    * If the filename ends with any of the configured 
    * <code>ParseDirective.TemplateExtensions</code>, assume it's a template.<p>
    *
    * Otherwise, it must be a file
    */
   protected int guessType (Broker b, String filename) {
      if (filename.indexOf ("://") > -1) {
         return TYPE_TEXT;
      } else {
         String[] extensions = getTemplateExtensions (b);
         for (int x=0; x<extensions.length; x++) {
            if (filename.endsWith (extensions[x]))
               return TYPE_TEMPLATE;
         }
      }
      
      return TYPE_TEXT;
   }
   
   /**
    * get the template or file that the user wants to include, based on the
    * specified type
    */
   protected Object getThingToInclude (Broker b, int type, String filename) throws PropertyException {
      switch (type) {
         // wants to include a template, so get it from the broker
         // and write it out
         case TYPE_TEMPLATE:
            return getTemplate (b, filename);
         
         // wants to include a static file, so get it from the "url" provider
         case TYPE_TEXT:
            return getFile (b, filename);

         // this should have been changed into one of the above
         case TYPE_DYNAMIC:
            throw new PropertyException ("#" + _directiveName + ": " 
                                       + "Internal Error. "
                                       + "Never guessed file type");
            
         // default case should never happen b/c we take care of this
         // during build()
         default:
            throw new PropertyException ("#" + _directiveName + ": " + _type 
                                      + " is an unrecognized file type");
      }
   }
   
   /**
    * get a Template via the "template" provider known by the specified broker
    */
   protected Template getTemplate (Broker b, String name) throws PropertyException {
      try {
         return (Template) b.get("template", name);
      } catch (NotFoundException nfe) {
         throw new PropertyException ("#" + _directiveName + ": " + name 
                                   + " was not found by the template provider");
      } catch (ResourceException re) {
         throw new PropertyException ("#" + _directiveName + ": " 
                                    + "Unable to get template " + name, re);
      } catch (Exception e) {
         throw new PropertyException ("#" + _directiveName + ": " 
                                    + "Unexpected exception while getting "
                                    + "the template " + name, e);
      }
   }
   
   /**
    * get the contents of a file (local file or url) via the "url" provider
    * known by the specified broker.  If the url provider can't find it
    * we check the Broker (Broker.getResource).
    */
   protected String getFile (Broker b, String name) throws PropertyException {
      try {
         return b.get("url", name).toString();
      } catch (ResourceException re) {
         try {
            java.net.URL url = b.getResource (name);
            try {
               return url.getContent ().toString();
            } catch (NullPointerException npe) {
               throw npe;
            } catch (Exception e) {
               throw new PropertyException ("#" + _directiveName + ": " 
                                         +  name + " was found by the Broker "
                                         + "but could not be retrieved", e);
            }
         } catch (NullPointerException npe) {
            throw new PropertyException ("#" + _directiveName + ": " 
                                       + name + " was not found by the url " 
                                       + "provider or the broker");
         } catch (Exception e) {
            throw new PropertyException ("#" + _directiveName + ": " 
                                       + "Unexpected exception while getting "
                                       + name + " from the Broker", e);
         }
      } catch (Exception e) {
         throw new PropertyException ("#" + _directiveName + ": " 
                                    + "Unexpected exception while getting "
                                    +  name + " from the URL provider", e);
      }
   }
   
   public void accept (TemplateVisitor v) {
      v.beginDirective (_desc.name);
      v.visitDirectiveArg ("IncludeDirective", _strFilename);
      v.endDirective ();
   }   
}