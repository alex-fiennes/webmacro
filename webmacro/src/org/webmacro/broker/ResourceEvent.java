
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

import org.webmacro.*;
import org.webmacro.util.*;
import java.util.*;

/**
  * A ResourceEvent represents the request for, provision of, and 
  * revocation of a resource: the event has state, and passes through
  * these three stages during its lifetime. 
  * <p>
  * A ResourceProvider can construct a new AnnounceResourceEvent if it 
  * wishes to spontaneously announce a new resource to the world. More 
  * commonly a ResourceProvider will be passed a ResourceEvent by the 
  * Broker in response to a request from a client. In either 
  * case the ResourceProvider should locate the resource and set it with 
  * the set() method.
  * <p>
  * Clients can retreive the underlying resource from the Event, 
  * or revoke it. Clients would revoke an event to indicate to 
  * a provider that the request is no longer desired--this may allow
  * the provider to abort without doing expensive work.
  * <p>
  * ResourceEvents are handled asynchronously by the ResourceBroker.
  * Attempts to call the toString() or getValue() methods before a 
  * resource has been obtained will block until the resource 
  * becomes available, or has been revoked. You can call the 
  * isAvailable() method to determine if such a call would block.
  * <p>
  * In addition, Providers should bear in mind that some kinds of 
  * ResourceEvents are passed to multiple clients, so the returned
  * object must be thread safe. 
  * <p>
  * Also there may be more than one Provider working on obtianing the
  * resource, or a client may revoke the request for some reason or 
  * other (eg: a client disconnected).
  * <p>
  * This class supports the PropertyChangeListener and Observer 
  * interfaces, but does not attempt to register itself as an 
  * observer or listener on any object. If you register it as an 
  * observer or listener on any object, possibly the resource that
  * it contains, then it will notice whenever the resource has been
  * updated, and use this information to cache it longer.
  * <p>
  * <b>Thread policy:</b> Methods of this object never acquire locks on 
  * external objects. ResourceBroker WILL lock ResourceEvent objects
  * as needed, though--so if you lock on this object you MUST NOT 
  * call the ResourceBroker. If you synchronize on ResourceEvent, it
  * is recommended that the code inside the synchronization block 
  * only call ResourceEvent methods.
  * <p>
  * @see ResourceProvider
  * @see ResourceBroker
  */
abstract public class ResourceEvent 
   extends java.util.EventObject
   implements BrokerRequest, Observer, java.beans.PropertyChangeListener
{

   private String type;
   private String name;

   private Object resource;          // our underlying resource
   private boolean valid = true;     // we are still valid...
   private boolean notfound = true; // set if not found
   boolean deleted = false; // set if is deleted


   /**
     * This value is set whenever something accesses the event in a way
     * that indicates its value is in use. It is only ever se to true 
     * by this class. Periodically the reaper thread runs under 
     * ResourceBroker and sets this to false. If the reaper detects 
     * that the value is false, the event will be revoked--implying that
     * if the reaper sees it twice with no access in between, nobody is
     * using it, and it will get revoked.
     */
   boolean accessed = true;      

   /**
     * Create a new ResourceEvent. Providers can call this method to 
     * construct a new resource they wish to announce. Normally a 
     * ResourceEvent is constructed by a ResourceBroker in response
     * to a request from a client.
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
   protected ResourceEvent(ResourceBroker broker, String type, String name,
         Object resource)
      throws InvalidTypeException
   {
      super(broker);
      this.type = type.intern();
      this.name = name.intern();
      this.resource = resource;
      if (resource == null || type == null || name == null || broker == null) 
      {
         throw new InvalidTypeException("Arguments cannot be null.");
      }
   }


   /**
     * Only the ResourceBroker can construct a resource which does 
     * not yet have a resource value.
     * @param broker the broker creating this request
     * @param type   the type of the resource
     * @param name   the name of the resource
     */
   ResourceEvent(ResourceBroker broker, String type, String name)
      throws InvalidTypeException
   {
      super(broker);
      this.type = type.intern();
      this.name = name.intern();
      resource = null;
      if (type == null || name == null || broker == null) 
      {
         throw new InvalidTypeException("Arguments cannot be null.");
      }
   }

   /**
     * Return the type of this resource. Each resource has a type which 
     * associates it with a set of providers. Providers
     * that handle multiple types should examine this field to determine
     * what type of requests this is. Providers which handle only a 
     * single type can ignore it, as the Broker will only call a Provider
     * with a matching type.
     * @return the string type of this resource.
     */
   final public String getType() 
   {
      // thread: type and name are immutable, no synch
      return type;
   }

   /**
     * Get the name of this resource. This is an arbitrary string 
     * that is meaningful to the provider and/or client. For example, 
     * it could be a query.
     * <p>
     * Providers should examine this string to determine what exactly
     * is being requested.
     * @return the string name of this ResourceEvent
     */
   final public String getName()
   {
      // thread: type and name are immutable, no synch
      return name;
   }

   /**
     * Set the resource. This can only be done once. Calling it a second
     * time has no effect. This method also has no effect if the event
     * has already been revoked. Returns whether the object was 
     * successfully set or not.
     * <P>
     * This method "accesses" the resource, resetting any cache expire.
     * <p>
     * @exception InvalidTypeException if attempt to set to a null value
     * @exception ResourceRevokedException if the resource has been revoked
     */
   final synchronized public void set(Object resource)
      throws ResourceRevokedException, InvalidTypeException
   {
      accessed = true;

      // cannot set resource to a null value
      if (resource == null) {
         throw new InvalidTypeException("Cannot set to null value");
      }

      // thread: does not acquire any further locks
      if (valid && (this.resource == null)) {
         notfound = false;
         this.resource = resource;
         notifyAll();
      } else {
         throw new ResourceRevokedException(
               this + " has been revoked: " + this.resource);
      }

   }

   /**
     * Invalidate this resource. 
     */
   final synchronized void revoke(String reason) 
   {
      // thread: does not acquire any further locks
      valid = false;
      resource = reason;
      notifyAll();
   }

   /**
     * Updating the event indicates to the caching mechanism that the 
     * event is still in use. Accessing the event in any way also 
     * touches it--this mechanism is provided in case you want to touch
     * it when you access the object it's holding on to, to let the 
     * cache know that it is still in use even though you have not
     * actually used the ResourceEvent itself.
     * <P>
     * This method "accesses" the resource, resetting any cache expire.
     * <p>
     */
   final public void update()
   {
      accessed = true;
   }

   /**
     * <b>Observer Interface</b>: If this method is called, the reaper
     * will note that the object has been used, and keep it around 
     * a little longer. You can register the ResourceEvent as an observer
     * on any observable object whose use is tied to the duration of time
     * that the underlying resource should be kept alive.
     * <P>
     * This method "accesses" the resource, resetting any cache expire.
     * <p>
     */
   final public void update(Observable o, Object arg)
   {
      accessed = true;
   }

   /**
     * <b>Property Change Listener Interface</b>: If this method is called,
     * the reaper will note that the object has been used, and keep it 
     * around a little longer. You can register the ResourceEvent as a 
     * property change listener on any object whose use is tied to the 
     * duration of time that the underlying resource should be kept alive.
     * <P>
     * This method "accesses" the resource, resetting any cache expire.
     * <p>
     */
   final public void propertyChange(java.beans.PropertyChangeEvent evt)
   {
      accessed = true;
   }

   /**
     * Return the name and type of this resource event
     */
   final public String toString()
   {
      return "Resource[" + type + ", " + name + "]";
   }

   /**
     * This method will block until the resource is available, and once
     * it's available, it will return it. It will throw an exception
     * if the resource cannot be located, has been revoked already, or 
     * if the call to this method violates the thread policy. Note that
     * very few thread policy violations can be detected--most will just
     * deadlock.
     * <P>
     * This method "accesses" the resource, resetting any cache expire.
     * <p>
     * @return    The underlying resource
     * @exception ResourceRevokedException resource has been revoked
     * @exception NotFoundException cannot retreive/doesn't exist
     */
   final public synchronized Object getValue()
      throws NotFoundException, ResourceRevokedException
   {
      accessed = true;
      try {
         // thread: must not acquire any further locks
         // must be synch. so not returned after invalidated
         if (valid && (resource == null)) {
            ResourceBroker rb = (ResourceBroker) getSource();
            rb.blocked(this); // does not acquire any locks
            while (valid && (resource == null)) {
               wait(); 
            }
            accessed = true;
         }
      } catch (InterruptedException e) {
         Thread.interrupted(); // clear the interrupt
      }
      if (valid) {       
         return resource; 
      } else {             
         if (notfound) {
            throw new NotFoundException(this + 
                  " not found: " + this.resource);
         } else {
            throw new ResourceRevokedException(
                  this + " has been revoked: " 
                  + this.resource);
         }
      }
   }

   /**
     * Return true if the resource is currently available, so that calling
     * getValue() will not block. Once a resource becomes available, 
     * calls to getValue will never block--even if the resource 
     * is revoked, the return will be immediate (and null in that case).
     * <p>
     * Calling this method marks the object as "accessed", resetting 
     * any cache expire count.
     */
   final public boolean isAvailable()
   {
      // thread: no need to synch, can only change in one direction
      // in particular once it isAvailable() getValue() will never block
      // even if it is revoked after being set.
      return (valid && (resource != null));
   }


   /**
     * Is this resource still valid? This will return false if the 
     * event has been revoked or cancelled, or the resource was
     * non-existant or unavailable.
     */
   final public boolean isRevoked()
   {
      // thread: no point to synch, can only change in one direction
      // and it could change just after we return anyway
      return !valid;
   }

   /**
     * Return whether it is still possible to set this event. In other
     * words, the event is still valid, and it has not yet been set.
     * Equivalent to !isRevoked() && !isAvailable().
     */
   final public boolean isSettable()
   {
      // thread: no point to synch, can only change in one direction
      // and it could change just after we return anyway
      return (valid && (resource == null));
   }

   /**
     * Method for double dispatch: calls appropriate method on provider
     */
   abstract void providerSet(ResourceProvider rp)
      throws NotFoundException, InterruptedException;

   /**
     * Method for double dispatch: calls appropriate method on provider
     */
   void providerSave(ResourceProvider rp) {
      rp.resourceSave(this);
   }

}


