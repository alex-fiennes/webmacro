package org.webmacro.tools;

import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.types.*;

import org.webmacro.*;
import org.webmacro.engine.*;
import org.webmacro.parser.*;
import org.webmacro.resource.*;
import org.webmacro.util.*;

import java.io.*;
import java.util.*;

/**
 * <P>
 * Taskdef for compiling "offline" WM templates to static content.
 * Based on Brian Goetz's Ant Task that parses templates but without
 * writing them to disk.
 * </P>
 *
 * <P>
 * Takes a fileset of WM templates as input and outputs them relative to
 * the specified directory. Based on the filename, it will create files of the
 * corresponding file extension. i.e.
 * <UL>
 *   <LI>templates/dir1/yourtemplate.wm.wml becomes output/dir1/yourtemplate.wml
 *   <LI>templates/dir1/yourtemplate.wm.xml becomes output/dir1/yourtemplate.xml
 *   <LI>templates/dir1/yourtemplate.wm.html becomes output/dir1/yourtemplate.html
 * </UL>
 * There is a default case though - the "extension" attribute determines
 * the extension used when a template is simple xxxx.wm with no trailing extension.
 * The value of this extension attribute defaults to "html" so under normal usage,
 * you can just use .wm files.
 * </P>
 * <P>
 * <B>NOTE: The first extension does NOT have to be .wm - this task will
 * process whatever files you give it. e.g.<BR>
 * <PRE>
 * yourtemplate.wmtemplate.html --> yourtemplate.html
 * yourtemplate.wm.html         --> yourtemplate.html
 * yourtemplate.rubbish         --> yourtemplate.html
 * </PRE>
 * The prefix is simply there to indicate to you, the content writer, that
 * it is not a plain .html source file. However, using this method instead of
 * plain "myfile.html" for templates is good because you can use your system's
 * default editors for the template files and they will be displayed correctly
 * (with WebMacro intact of course).
 * </P>
 *
 * <P>
 * Supports the following attributes:<BR>
 * <TABLE>
 * <TR>
 * <TD>templatepath</TD><TD>dir from which to load #parse/#included templates - required if using #parse/#include and not using the <B>properties</B> attribute</TD>
 * </TR>
 * <TR>
 * <TD>encoding</TD><TD>char encoding for input files (mainly templates) - default is system file encoding</TD>
 * </TR>
 * <TR>
 * <TD>outputdir</TD><TD>base dir for output files - required</TD>
 * </TR>
 * <TR>
 * <TD>outputencoding</TD><TD>char encoding for generated files - default is system file encoding</TD>
 * </TR>
 * <TR>
 * <TD>properties</TD><TD>Webmacro properties file - optional</TD>
 * </TR>
 * <TR>
 * <TD>extension</TD><TD>extension to use when no double-extension specified - optional</TD>
 * </TR>
 * <TR>
 * <TD>macros</TD><TD>properties file of strings to load into context - optional</TD>
 * </TR>
 * </TABLE>
 * </P>
 * <P>
 * Supports the nested elements:<BR>
 * fileset = template files to turn into content.<BR>
 * </P>
 * @author Marc Palmer (marc@anyware.co.uk)
 */

public class AntWMCompile extends Task {

  protected Vector filesets = new Vector(5);

  private static String filesep =
     System.getProperties().getProperty( "file.separator");

  private Path compileClasspath;

  private Parser parser;
  private Broker broker;
  private Context context;
  private String propsFile;
  private String defaultExt;
  private String macrosFile;
  private String outputRoot;
  private String templatePath;
  private String inputEncoding;
  private String outputEncoding;
  private Map responseMap;

  /**
   * Parse the template and write it out. Writes output relative to
   * outputDirectory, in same way template is relative to its source
   * directory. i.e.
   * if source dir = "/templates"
   * and template file is "subdir/myfile.wm" under that directory,
   * it will create output in "/outputroot/subdir/myfile.newextenstion"
   *
   * @param dirstem Base directory for input file (i.e. /templates)
   * @param filename Filename, relative to base (i.e. subdir/myfile.wm)
   */
  private void parseTemplate(String dirstem, String filename)
     throws Exception
  {

      /* Read and parse */
      File fullpath = new File( dirstem, filename);
      FileInputStream fis = new FileInputStream( fullpath );
      Reader in = new BufferedReader( new InputStreamReader( fis), 16000);
      Builder bb = parser.parseBlock(fullpath.toString(), in);
      Block b = (Block)bb.build(new BuildContext(broker));

      /* Get it into a byte array first as we don't know the output name yet */
      ByteArrayOutputStream baos = new ByteArrayOutputStream( 16000);
      FastWriter fw = new FastWriter( broker, baos, outputEncoding);
      b.write( fw, context);
      fw.flush();

      String outname = extractOutputName( filename);

      File outfn = new File( outputRoot, outname);

      /* Force parent dirs to exist */
      File outbase = outfn.getParentFile();
      if (outbase != null)
          outbase.mkdirs();

      OutputStream fos = new BufferedOutputStream( new FileOutputStream( outfn),
           16000);
      try
      {
          fos.write( baos.toByteArray() );
      }
      finally
      {
        fos.close();
      }
  }

  public void addFileset(FileSet set) {
    filesets.addElement(set);
  }

  public void setClasspath(Path classpath) {
    if (compileClasspath == null) {
      compileClasspath = classpath;
    } else {
      compileClasspath.append(classpath);
    }
  }

  public Path getClasspath() {
    return compileClasspath;
  }

  public void setOutputDir( String outputDir)
  {
     outputRoot = outputDir;
  }

  public void setTemplatePath( String templDir)
  {
     templatePath = templDir;
  }

  public void setOutputEncoding( String outputEnc)
  {
     outputEncoding = outputEnc;
  }

  public void setEncoding( String inputEnc)
  {
     inputEncoding = inputEnc;
  }

  public void setExtension( String defEx)
  {
     defaultExt = defEx;
  }

  /**
   * Take relative file name of the form dir1/dir2/myfile.wm.html and return
   * output filename, i.e. dir1/dir2/myfile.html
   *
   * If no middle-extension, just changes existing extension to the default
   * extension. i.e. dir1/dir2/myfile.wm goes to dir1/dir2/myfile.html
   * if default is "html".
   */
  private String extractOutputName( String fn)
  {
     int lastsep = fn.lastIndexOf( filesep);

     String pathpart = (lastsep != -1) ?
          fn.substring( 0, lastsep+1) : "";
     String fnpart = (lastsep != -1) ?
          fn.substring( lastsep+1, fn.length()) : fn;

     int lastdot = fnpart.lastIndexOf('.');
     int lastdot2nd = fnpart.lastIndexOf('.', lastdot-1);

     /* If no second extension, just change to default extension */
     if (lastdot2nd == -1)
     {
          if (lastdot == -1)
               return fn+'.'+defaultExt;
          else
               return pathpart+fnpart.substring( 0, lastdot+1) + defaultExt;
     }
     else
     {
          /* We have two extensions, chop out the one in between */
          StringBuffer s = new StringBuffer( fnpart);
          s.delete( lastdot2nd, lastdot);
          return pathpart + s.toString();
     }
  }

  public void setProperties( String propertiesFile)
  {
     propsFile = propertiesFile;
  }

  public void setMacros( String propertiesFile)
  {
     macrosFile = propertiesFile;
  }

  public Path createClasspath() {
    if (compileClasspath == null) {
      compileClasspath = new Path(project);
    }
    return compileClasspath.createPath();
  }

  public void setClasspathRef(Reference r) {
    createClasspath().setRefid(r);
  }

  public AntWMCompile() {
     defaultExt = "html";
  }

  public void execute() throws org.apache.tools.ant.BuildException {

    if (outputEncoding == null)
      outputEncoding = System.getProperties().getProperty( "file.encoding");
    if (inputEncoding == null)
      inputEncoding = System.getProperties().getProperty( "file.encoding");

    try
    {
         WM wm = null;
         if (propsFile != null)
           wm = new WM(propsFile);
         else
           wm = new WM();

         broker = wm.getBroker();
         broker.setEvaluationExceptionHandler(
            new CrankyEvaluationExceptionHandler() );

         if (templatePath != null)
         {
            TemplateProvider p = new TemplateProvider();
            Properties wmProps = wm.getBroker().getSettings().getAsProperties();
            Properties props = new Properties( wmProps );
            if (props.get("TemplatePath") == null)
               props.put( "TemplatePath", templatePath);
            Settings settings = new PropertiesSettings(props);

            p.init( broker, settings);

            broker.addProvider( p, "template");

         }

         parser = (Parser)broker.get("parser", "wm");
         context = wm.getContext();

         if (macrosFile != null)
         {
            Properties macros = new Properties();
            FileInputStream fis = new FileInputStream( macrosFile);
            try
            {
               macros.load( fis);
            }
            finally
            {
               fis.close();
            }

            context.putAll( macros);
         }

//         /* Make context swallow up $Response if ever used */
//         responseMap = new Hashtable( 5);
//         context.put( "Response", responseMap);

         for (int i=0; i<filesets.size(); i++) {
           FileSet fs = (FileSet) filesets.elementAt(i);
           File fromDir = fs.getDir(project);
           DirectoryScanner ds = fs.getDirectoryScanner(project);
           String[] srcFiles = ds.getIncludedFiles();
           for (int j=0; j<srcFiles.length; j++)
             parseTemplate( fromDir.getAbsolutePath(), srcFiles[j]);
         }
     }
     catch (Exception e)
     {
          throw new org.apache.tools.ant.BuildException( e);
     }

  }

  /**
   * We need this so we can init the TemplateProvider with templatepath
   */
  private static class PropertiesSettings extends Settings
  {
     public PropertiesSettings(Properties p)
     {
          super( p);
     }
  }
}

