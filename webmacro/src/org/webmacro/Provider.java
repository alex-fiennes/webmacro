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


package org.webmacro;
import org.webmacro.util.Settings;

/**
  * A Provider is an object responsible or loading and managing 
  * instances of a given type. The Provider is used by the Broker 
  * to look up objects on demand. 
  * <p> 
  * By implementing new Provider types and registering them with 
  * the broker via WebMacro.properties you can extend or change
  * WebMacro's behavior.
  */
public interface Provider {

  /**
    * Return an array representing the types this provider serves up
    */
  public String getType();

  /**
    * Initialize this provider based on the specified config.
    */
  public void init(Broker b, Settings config) throws InitException;

  /**
    * Clear any cache this provider may be maintaining
    */
  public void flush();

  /**
    * Close down this provider, freeing any allocated resources.
    */
  public void destroy();

  /**
    * Get the object associated with the specified query
    */
  public Object get(String query) throws ResourceException; 

}

