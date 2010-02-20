package org.webmacro.tools;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import java.io.File;
import java.util.Vector;

/**
 * Taskdef for validating WM templates.
 *
 * @author Brian Goetz
 */

public class WMTemplateAntTask extends Task
{

    protected Vector filesets = new Vector();

    private CommandlineJava cmdl = new CommandlineJava();
    private Path compileClasspath;

    public void addFileset (FileSet set)
    {
        filesets.addElement(set);
    }

    public void setClasspath (Path classpath)
    {
        if (compileClasspath == null)
        {
            compileClasspath = classpath;
        }
        else
        {
            compileClasspath.append(classpath);
        }
    }

    public Path getClasspath ()
    {
        return compileClasspath;
    }

    public Path createClasspath ()
    {
        if (compileClasspath == null)
        {
            compileClasspath = new Path(getProject());
        }
        return compileClasspath.createPath();
    }

    public void setClasspathRef (Reference r)
    {
        createClasspath().setRefid(r);
    }

    /**
     * Subclasses should override this
     * to supply their own template execution
     * vehicle.
     */
    protected String getMainClass ()
    {
        return "org.webmacro.tools.CheckTemplates";
    }

    public WMTemplateAntTask ()
    {
        cmdl.setVm("java");
        cmdl.setClassname(getMainClass());
    }


    public void execute ()
    {

        for (int i = 0; i < filesets.size(); i++)
        {
            FileSet fs = (FileSet) filesets.elementAt(i);
            File fromDir = fs.getDir(getProject());
            DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            String[] srcFiles = ds.getIncludedFiles();
            for (int j = 0; j < srcFiles.length; j++)
                cmdl.createArgument()
                        .setValue(new File(fromDir.getAbsolutePath(), srcFiles[j])
                        .getAbsolutePath());
        }


        Path classpath = cmdl.createClasspath(getProject());
        classpath.append(getClasspath());

        Execute.runCommand(this, cmdl.getCommandline());
    }

}
