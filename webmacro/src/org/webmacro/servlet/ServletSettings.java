package org.webmacro.servlet;

import javax.servlet.*;

import org.webmacro.*;
import org.webmacro.util.*;

public class ServletSettings extends Settings {
  public ServletSettings(Settings defaults) {
    super(defaults);
  }

  public void load(ServletContext sc) {
  }
}
