

/*
 * Copyright (c) 1998, 1999 Semiotek Inc. All Rights Reserved.
 *
 * This software is the confidential intellectual property of
 * of Semiotek Inc.; it is copyrighted and licensed, not sold.
 * You may use it under the terms of the GNU General Public License,
 * version 2, as published by the Free Software Foundation. If you 
 * do not want to use the GPL, you may still use the software after
 * purchasing a proprietary developers license from Semiotek Inc.
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See the attached License.html file for details, or contact us
 * by e-mail at info@semiotek.com to get a copy.
 */


package org.webmacro;

/**
  * This interface defines the things you can expect from the Broker.
  * The Broker is both an object cache, and a mechanism for plugging 
  * new components in to WebMacro.
  */
public interface Broker 
{

   /**
     * An argument for request(String,String,boolean), specifying that 
     * the request should be processed in a separate thread.
     */
   static public final boolean ASYNCHRONOUS = true;

   /**
     * An argument for request(String,String,boolean), specifying that 
     * the request should be processed in the current thread.
     */
   static public final boolean SYNCHRONOUS = false;

   /**
     * This string serves as the TYPE for configuration information 
     */
   static public final String CONFIG_TYPE = "config";

   /**
     * Retrieve an object of the specified type, with the specified name.
     * The "type" is used to determine what mechanism or plug-in is used
     * to locate the object, and "name" identifies the particular object
     * you are interested in.
     * <p>
     * @exception NotFoundException the resource could not be retrieved
     * @exception InvalidTypeException you asked for an unknown type
     */
   public Object getValue(final String type, final String name)
      throws InvalidTypeException, NotFoundException;

}


