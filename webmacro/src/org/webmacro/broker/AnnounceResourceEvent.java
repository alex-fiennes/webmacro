
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
  * A resource which was constructed and announced by a ResourceProvider.
  */
final public class AnnounceResourceEvent extends ResourceEvent
{

   /**
     * Create a new ResourceEvent. Providers can call this constructor to 
     * construct a new resource they wish to announce.
     * <p>
     * Providers MUST specify a ResourceBroker as the source of an event 
     * A ResourceEvent constructed from a request will always have the 
     * ResourceBroker as its source.
     * <p>
     * @param broker is the source broker which handles this event
     * @param type is required by Providers when filling requests
     * @param name may be used by a provider filling a request
     * @exception InvalidTypeException if a null value is passed in
     */
   public AnnounceResourceEvent(ResourceBroker broker, String type, String name,
         Object resource) throws InvalidTypeException
   {
      super(broker,type,name,resource);
   }

   final void providerSet(ResourceProvider rp) {
      // XXX: cannot call provider with announce!
   }

}
 
