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


package org.webmacro.resource;

import java.net.*;
import org.webmacro.*;

public class UrlTemplateTimedReference extends TimedReference {
  long lastModified;
  URL url;

  UrlTemplateTimedReference(Object referent, long timeout, 
                            URL url, long lastModified) {
    super(referent, timeout);
    this.url = url;
    this.lastModified = lastModified;
  }
 
  public boolean shouldReload() {
    return (lastModified != UrlProvider.getUrlLastModified(url));
  }
}
