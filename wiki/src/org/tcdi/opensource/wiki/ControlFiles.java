/*
 * Created on May 8, 2004
 *
 */
package org.tcdi.opensource.wiki;

import org.opendoors.util.SysEnv;

/**
 * Control files used by wiki
 * and can be viewed/updated by the administrator.
 * @author Lane Sharman
 */
public class ControlFiles {
  
  private Entry _wikiPropertyFile, _webXML,
          _wmProperties, _cssStyle;
  private String _installDir;
  private Entry[] controlFileList;

  public ControlFiles(String installDir)
  {
    this._installDir = installDir;
    refresh();
  }

  void refresh() {
    controlFileList = new Entry[4];

    // individual files are not mandatory.
    _wikiPropertyFile = new Entry("Wiki.properties", "Runtime Properties", _installDir + "deploy/WEB-INF/Wiki.Properties");
    controlFileList[0] = _wikiPropertyFile; 

    _webXML = new Entry("web.xml", "web.xml describes the wiki to the servlet container", _installDir + "deploy/WEB-INF/web.xml");
    controlFileList[1] = _webXML; 

    _wmProperties = new Entry("WebMacro.properties", "WebMacro.properties specifies features for the WebMacro engine", _installDir + "deploy/WEB-INF/WebMacro.properties");
    controlFileList[2] = _wmProperties; 

    _cssStyle = new Entry("site-style.css", "site-style.css is the style sheet used for this wiki", _installDir + "deploy/site-style.css");
    controlFileList[3] = _cssStyle; 
  }
  
  public Entry[] getEntries() {
    return controlFileList;
  }
  

  public static class Entry {
    String name;
    String description;
    String path;
    String contents;
    
    Entry(String name, String description, String path)
    {
      this.name = name;
      this.description = description;
      this.path = path;
      load(); 
    }

    /**
     * @return The contents of the file.
     */
    public String getContents()
    {
      return contents;
    }

    /**
     * @return
     */
    public String getDescription()
    {
      return description;
    }

    /**
     * @return
     */
    public String getName()
    {
      return name;
    }

    /**
     * @return
     */
    public String getPath()
    {
      return path;
    }

    /**
     * Sets the  contents of the file.
     * The file is saved
     */
    public void setContents(String value)
    {
      this.contents = value;
      save();
    }

    void load() 
    {
      try {
        contents = SysEnv.readTextFile(path);
      }
      catch (Exception e){
        throw new IllegalArgumentException(path + " does not point to a file.");
      }
    }
    
    void save() 
    {
      try {
        SysEnv.createTextFile(path, contents);
      }
      catch (Exception e){
        throw new IllegalArgumentException(path + " does not point to a file.");
      }
        
    }
  }

}
