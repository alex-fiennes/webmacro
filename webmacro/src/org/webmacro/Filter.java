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

/**
  * A Filter is a factory which returns a Macro which filters another
  * Macro. A Filter is a Macro whose express purpose is to filter the 
  * results of another Macro. The Filter interface is used to
  * instantiate Filters as needed on a page.
  * <p>
  * In addition to instantiating filters, a Filter must define 
  * the mapping of a property name list to a specific tool. For 
  * example, if a Filter applies to a property called $Customer
  * then it must determine how $Customer.Name is to be handled.
  */
public interface Filter {

  /**
    * Return the Filter which should be used to handle a
    * sub-property. Three options are available here: return a 
    * null to indicate no filtering, return self to indicate 
    * the same filtering, or return a different Filter to
    * indicate different handling for the sub-property.
    * @param name the name of the sub-property to be filtered
    * @return the Filter to be used for the sub-property, or null
    */
  public Filter getFilter(String name); 

  /**
    * Instantiate a new filter. There are several options for the
    * return value of this method: return null to drop the Macro 
    * from the input stream entirely; return the Macro itself to 
    * avoid filtering this particular case, or return some new 
    * Macro to replace the supplied Macro. The expectation is that
    * the returned Macro will execute the original and apply some
    * post-processing to it. 
    * @param source the Macro which this filter will post-process
    * @return the Macro wrapper to be executed in place of source
    */
  public Macro getMacro(Macro source);

}
