

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
  * This interface represents a request to the broker. The request may 
  * be being processed in another thread, and this interface will get 
  * "filled in" with the requested value once it is executed. The most
  * common type of request is to retrieve an object, in which case the
  * object can be accessed through this interface once it has been 
  * located.
  */
public interface BrokerRequest 
{

   /**
     * What type are we looking for?
     */
   public String getType();

   /**
     * What's the name of the object we are looking for?
     */
   public String getName();

   /**
     * Return the object we are seeking. If the request is being performed
     * in a separate thread, this method will block until the request 
     * has been completed.
     * @exception NotFoundException the request could not be resolved
     */
   public Object getValue() throws NotFoundException;

   /**
     * Check whether a call to getValue() will block: this method returns
     * false until the request is completed, and true after that. It may 
     * subsequently return false again if, for some reason, the Broker 
     * reclaims the requested object. 
     */
   public boolean isAvailable();

}


