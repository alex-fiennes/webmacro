package org.webmacro.tools;

import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.types.*;

import java.io.*;
import java.util.*;

/**
 * Taskdef for validating WM templates
 *
 * @author Brian Goetz
 */

public class WMTemplateAntTask extends Task {

  protected Vector filesets = new Vector();

  private CommandlineJava cmdl = new CommandlineJava();
  private Path compileClasspath;
    
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

  public Path createClasspath() {
    if (compileClasspath == null) {
      compileClasspath = new Path(project);
    }
    return compileClasspath.createPath();
  }

  public void setClasspathRef(Reference r) {
    createClasspath().setRefid(r);
  }


  public WMTemplateAntTask() {
    cmdl.setVm("java");
    cmdl.setClassname("org.webmacro.tools.CheckTemplates");
  }


  public void execute() throws BuildException {

    for (int i=0; i<filesets.size(); i++) {
      FileSet fs = (FileSet) filesets.elementAt(i);
      File fromDir = fs.getDir(project);
      DirectoryScanner ds = fs.getDirectoryScanner(project);
      String[] srcFiles = ds.getIncludedFiles();
      for (int j=0; j<srcFiles.length; j++) 
        cmdl.createArgument()
          .setValue(new File(fromDir.getAbsolutePath(), srcFiles[j])
            .getAbsolutePath());
    }


    Path classpath = cmdl.createClasspath(project);
    classpath.append(getClasspath());
    
    Execute.runCommand(this, cmdl.getCommandline());
  }

}
