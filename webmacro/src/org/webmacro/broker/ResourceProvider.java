
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
import java.util.*;

/**
  * Implement this to provide a category of service for WebMacro. 
  * <p>
  * Resources are described by types and names. A type represents a 
  * domain of service: for example "user", "template", "config". The 
  * type information is used by the broker to choose which ResourceProviders
  * may be able to service a request for information.
  * <p>
  * You can consider a name to be a sort of URL within a type domain. A 
  * name must uniquely describe a single object or concept that would be 
  * identical across multiple calls to the ResourceBroker within the 
  * same process. If you wish to serve up non-unique, transient, or 
  * changing objects it is suggested that the resource you return be 
  * a unique, intransient and unchanging wrapper capable of retreiving
  * the desired, volatile information.
  * <p>
  * An example: If you wish to override the default kind of User provided
  * by WebMacro, you could implement your own "user" type and register it 
  * in place of the WebMacro "user" provider. Your provider would then be 
  * used to look up and return users instead. You may also invent your own
  * domains of information and register arbitrary providers for them, so
  * as to make their resources available to any handler that asks.
  * <p>
  * Note that the intent of the Broker service is to parallelize 
  * processing, as well as separate requests for services from 
  * their implementations. Your provider must be threadsafe, and any 
  * data returned in a ResourceRequest must be threadsafe as well.  
  * Each method has a corresponding thread policy which you should 
  * observe so as to avoid deadlock, lockout, livelock, and other 
  * synchronization problems.
  * <p>
  * @see ResourceBroker
  * @see ResourceEvent
  */
public interface ResourceProvider 
{

   /**
     * Use this when setting expire times to indicate that the ResourceBroker
     * should never cache resources returned by this provider.
     * <p>
     * <b>Warning</b>: This refers only to the resource brokers cache and 
     * resource management abilities. In one sense your objects will never
     * expire: Since the ResourceBroker will not be managing them, they
     * will only be saved/deleted/revoked when the client code explicitly
     * calls revoke().  
     * <p> This setting would be useful for resources which are simply
     * delivered to the client and then forgotten--it is not important
     * whether resourceSave() is ever called.
     */
   final static public int NEVER_CACHE = 0;

   /**
     * Use this when setting expire times to indicate that the ResourceBroker
     * should cache resources returned by this provider forever.
     * <p>
     * <b>Warning</b>: Do not use this unless you are sure there are a
     * finite number of resources that could be requested, and that they
     * all fit into memory.
     * <p>
     * Also, this does not guarantee that your resources will survive for
     * the lifespan of the ResourceBroker--the broker may still decide to 
     * shut down your entire ResourceProvider from time to time. At that 
     * point your objects will be revoked and returned to you for 
     * save/delete.
     */
   final static public int INFINITE_CACHE = -1;

   /**
     * Find a resource that already exists and make it available for use.
     * <p>
     * Find an object matching the supplied description and type. If
     * the value can be obtained, set it with request.setResource()
     * and return. If it cannot be obtained, simply return without 
     * calling request.setResource(). 
     * <p>
     * No further action is required. The ResourceBroker will look 
     * after notifying threads appropriately.
     * <p>
     * Note that this method MAY execute in its own thread, and that the
     * requestor MAY cancel the event at any time, or another Provider MAY 
     * have already obtained the resource and set it before you have a 
     * chance to. If any of those situations occur, then this
     * ResourceEvent will not be settable and the work done by this method
     * will be ignored by the ResourceBroker.
     * <p>
     * In these cases your thread will be interrupted, its priority downgraded,
     * and the supplied event will throw an exception if you attempt to set 
     * it. You MAY wish to test ResourceEvent.isSettable() prior to starting
     * an expensive procedure in order to avoid wasting computing resources.
     * <p>
     * Also note that more than one thread may receive the objects that
     * you return, therefore any content put into the ResourceEvent MUST 
     * be thread safe. See the thread policy below.
     * <p>
     * <b>Thread policy</b>: You will not be called twice for the same object 
     * without at least one intervening resourceSave() on that object unless
     * you have set the cache to NEVER_CACHE. 
     * <p>
     * Be aware that several threads may be blocked waiting for you to return 
     * a value. (You can detect this: If your thread priority is raised 
     * from ResourceBroker.NORMAL_WORKER to ResourceBroker.CRITICAL_WORKER,
     * then someone is already waiting for you to return.
     * <p>
     * You MAY call the ResourceBroker and you MAY lock other ResourceEvent 
     * objects. But beware of deadlock: Other threads may be blocked waiting
     * for ResourceEvents that you provide. It is up to you to arrange a 
     * thread policy for your ResourceEvents and other objects such that 
     * providers don't block waiting for providers who are waiting for you.
     * <p>
     * This sounds complicated and it is. A simpler course of action is to
     * follow an "inner most lock" policy for all your objects: Do not 
     * synchronize on any object that is reachable outside the methods
     * in your instance.
     * <p>
     * You may throw a NotFoundException to indicate that you 
     * do service the requested object, but for some reason it is not 
     * currently available. The ResourceBroker will stop searching for it.
     * <p>
     * You may throw InterruptedException if your thread was interrupted
     * because the work it is doing is no longer wanted. In this case be 
     * sure and clean up any allocated resources prior to throwing the 
     * exception. Calling wait(0) will throw this exception if you have
     * been interrupted.
     * <p>
     * @param NotFoundException you refuse to allow retrieval
     * @param request the interface between you and your clients
     * @exception NotFoundException request refused
     * @exception InterruptedException your thread was interrupted
     */
   public void resourceRequest(RequestResourceEvent request)
              throws NotFoundException, InterruptedException; 


   /**
     * Allocate a resource that did not previously exist, possibly storing
     * it for future reference as well.
     * <p>
     * Exactly the same semantics as resourceRequest, except that the 
     * resource is to be created. Throw a NotFoundException if
     * you do not want to create the resource, and you don't want other
     * Providers to create it either--this may be because the resource
     * already exists, or has been deleted, etc.
     * <p>
     * If you simply don't allow resource creation it might be better 
     * to do nothing and let the broker locate a different provider of 
     * the same type that does. 
     * <p>
     * @param create container for the resource which is to be created
     * @exception NotFoundException you refuse to allow creation
     * @exception InterruptedException thread interrupted (requested abort)
     */
   public void resourceCreate(CreateResourceEvent create)
               throws NotFoundException, InterruptedException;

   /**
     * Delete a resource that already exists, possibly removing it from 
     * permanent storage.
     * <p>
     * Exactly the same semantics as save. The resource may or may not
     * have been previously allocated--this is a request to delete it.
     * <p>
     * This method may be called instead of resourceSave(). You can 
     * choose to save the resource and throw an exception if you want
     * to deny deletions.
     * <p>
     * You can throw a NotFoundException if you refuse to 
     * allow deletion of the named resource--because you don't allow
     * deletion, the resource has already been deleted, etc. If the 
     * resource simply does not exist you should do nothing, to let 
     * the broker try other providers to see if they have it.
     * @param delete the resource to be deleted
     */
   public boolean resourceDelete(ResourceEvent delete);


   /**
     * Save a previously allocated resource, on permanent storage 
     * if necessary.
     * <p>
     * This method is called by the ResourceBroker when a previously 
     * allocated resource is about to be revoked. It will not be called
     * if the resource was allocated as part of a resourceDelete 
     * operation--deleted resources will simply be discarded. 
     * <p>
     * If the resource allocated has any persistent information which needs 
     * to be stored on disk, or written out, etc. If you handle the request,
     * return true. If you did not handle it (and the Broker should keep 
     * looking to see if a different provider owns the object) return false.
     * <p>
     * This method (or resourceDelete) will be called once for each 
     * ResourceEvent previously allocated by this ResourceProvider, but only
     * if the ResourceProvider is still registered with the ResourceBroker 
     * when the resource gets revoked. (ie: if you deregister your broker,
     * the ResourceBroker will not know how to find you anymore.)
     * <p>
     * <b>Thread Policy:</b> Do not synchronize on any external objects.
     * <p>
     * <b>WARNING</b>: This method will already have been synchronized on 
     * the supplied ResourceEvent by the ResourceBroker, to ensure that 
     * clients do not interfere with the save and revoke procedure. This 
     * implies some fairly draconian restrictions on what this method can 
     * do safely: 
     * <p>
     * <ul><li>DO NOT call any methods on the broker
     *     <li>DO NOT call any methods on any other ResourceEvent
     * </ul>
     * Please obey, or you may deadlock!
     * <p>
     * @return true if you handled it, false if the broker should try elsewhere
     */
   public boolean resourceSave(ResourceEvent save);

   /**
     * Return the desired amount of concurrency for this provider: The
     * broker will start up to this many threads to service requests.
     * Specifying zero implies no concurrency at all, meaning requests
     * will be resolved in the clients request thread. Zero is recommended
     * for resource providers which are CPU intensive, since they do not 
     * benefit from added concurrency, and will suffer degraded performance
     * and liveness as a result of the thread overhead. Resource providers 
     * which are disk or network bound may benefit from concurrency and 
     * can specify the desired number of threads.
     * <p>
     * NOTE: This is advisory information only and may not be used by the
     * broker under all circumstances. In particular, other providers of
     * the same type may have specified a different value.
     */
   public int resourceThreads();

   /**
     * Return, in milliseconds, the preferred maximum amount of time the 
     * broker should cache the results of this resource provider. Note 
     * that this is advisory information only, and may not be used by the 
     * broker under all circumstances.
     * <P>
     * You can use the constants INFINITE_CACHE and NEVER_CACHE, but 
     * be warned that these values will only be used if your provider
     * is the first one registered against the broker--therefore it is
     * recommended that all providers of the same type registered with
     * the ResourceBroker return the same cache control information.
     */
   public int resourceExpireTime();


   /**
     * Get a list of the types that this ResourceProvider can supply.
     * This method will be called when the ResourceProvider is 
     * registered with a ResourceBroker. The ResourceBroker will only
     * forward events matching one of the listed types to the Provider.
     * <p>
     * Note that this method should return the same list each time its
     * called by the ResourceBroker. If you wish to change the list of
     * types your listener is intersted in, you should remove it from
     * all ResourceBrokers, change the list, and then re-add it.
     * <p>
     * @see ResourceProvider
     * @see ResourceConsumer
     */
    public String[] getTypes(); 


   /**
     * Called whent he ResourceBroker is starting up. You can initialize
     * your provider at this point. If initialization failed for some 
     * reason you can throw an exception.
     * @param broker the resoruce broker initializing the listener
     * @exception ResourceInitException cannot initialize the listener
     */
    public void init(ResourceBroker broker) throws ResourceInitException;


   /**
     * Called when the RequestBroker is shutting down. The ResourceProvider
     * should save its persistent state and shut down as well.
     */
   public void destroy();

}

