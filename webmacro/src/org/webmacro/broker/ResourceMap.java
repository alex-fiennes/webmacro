
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


package org.webmacro.broker;
import org.webmacro.util.*;
import org.webmacro.*;

/**
  * A ResourceMap gives you what may be a more convenient way to 
  * access the ResourceBroker. It also allows for more convenient
  * use of the Broker using Property introspection.
  * <p>
  * Each ResourceMap is associated with exactly one type. The 
  * type string is assumed in each call to the ResourceMap, where
  * you supply only the name. The interface is very much like the
  * Dictionary interface, except that teh ResourceMap can raise
  * a variety of exceptions.
  * <p>
  * This class is just syntactic sugar over the ResourceBroker.
  * <p>
  * @see Property
  */
public interface ResourceMap {
   /**
     * Get the ResourceEvent with the supplied name.
     * @exception NotFoundException request refused
     * @exception InvalidTypeException no such type
     * @see ResourceBroker#request
     */
   public ResourceEvent get(String name) 
      throws InvalidTypeException, NotFoundException;

   /**
     * Remove the ResourceEvent with this name. 
     * @exception NotFoundException request refused
     * @exception InvalidTypeException no such type
     * @see ResourceBroker
     */
   public void remove(String name)
      throws InvalidTypeException, NotFoundException;

   /**
     * Create and store the ResourceEvent with the supplied name
     * and optional argument. Note that you do not need to use this
     * method (and cannot) for normal storage of an object you have
     * modified. Nor can you use it to replace an object--it will 
     * fail if the resource already exists.
     * @exception NotFoundException request refused
     * @exception InvalidTypeException no such type
     * @see ResourceEvent
     */
   public ResourceEvent put(String name, Object argument)
      throws InvalidTypeException, NotFoundException;
}

