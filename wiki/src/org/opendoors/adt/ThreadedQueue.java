/*
* Copyright Open Doors Software and Acctiva, 2000.
*
* Software is provided according to the ___ license.
* Open Doors Software and Acctiva provide this
* software on an as-is basis and make no representation as
* to fitness for a specific purpose.
*
* Direct all questions and comments to support@opendoors.com
*/

package org.opendoors.adt;

import org.opendoors.adt.Queue;
import org.opendoors.util.Console;
import org.opendoors.intf.QueueListener;

/**
 * ThreadedQueue implements a queue in which the elements are placed on the queue within the
 * callers thread and then dequeued within a separate thread.
 * <p>
 * When this thread is instantiated the, the receiving pipe must be declared to implement
 * the simple interface, QueueListener.
 * @author Lane Sharman
 * @version 2.0
 */
public class ThreadedQueue extends Thread {

	//-------public members-----

	//-------private and protected members-----
	/** The default listener if the object queued does not have its own listener. */	
	private QueueListener listener = null;

	/** The data. */
	private Queue recordQueue;

	/** The shared semaphor, not the thread. */
	private Object mutex = new Object();
	
	/** Notify this object when all elements have been handled.	 */
	private Object flushListener;

	
	//-------constructor(s)-----
	/**
	 * The constructor without a default listener.
	 */
	public ThreadedQueue() {
		super();
		recordQueue = new Queue();
		initQ();
	}

	/**
	 * The constructor which takes a subclass of queue.
	 */
	public ThreadedQueue(Queue specializedQueue) {
		super();
		recordQueue = specializedQueue;
		initQ();
	}

	
	/**
	 * The constructor which specifies a default listener.
	 */
	public ThreadedQueue(QueueListener listener) {
		super();
		recordQueue = new Queue();
		this.listener = listener;
		initQ();
	}

	/** Called by the constructor as common code. Starts the thread. */
	protected void initQ() {
		setDaemon(true);
		start();
	}
	
	/**
	 * Set the flushed listener.
	 * <p>
	 * If set, its notifyAll() method is set so everyone
	 * has a chance to go forward with the understanding that the
	 * queue transitioned to empty from non-empty.
	 */
	public void setFlushListener(Object flushListener) {
		this.flushListener = flushListener;
	}

	/** The current size of the queue. */
	public int size() {
		return recordQueue.size();
	}

	/**
	 * Sets the default listener.
	 */
	public void setListener(QueueListener listener) {
		this.listener = listener;
	}

	/**
	 * Adds a record to be handled by the listener.
	 * <p>
	 * If the second parameter is null, then the default listener
	 * is used and it must not be null. Otherwise
	 * the routine throws an illegal argument exception.
	 * @param The object to be handled in a separate thread.
	 * @param The listener to handle the object.
	 */
	public void addElement(Object event, QueueListener providedListener)
							throws IllegalArgumentException {
		QueueListener handler = null;
		if (providedListener != null)
			handler = providedListener;
		else
			handler = this.listener;
		if (handler == null)
			throw new IllegalArgumentException("No Listener to handle object");

		ThreadedQueueItem item = new ThreadedQueueItem(event, handler);
		recordQueue.enqueue(item);
		synchronized (mutex) {
			try {
				mutex.notify();
			}
			catch (Exception e) {
				Console.error("Notification on adding transaction", this, e);
			}
		}
	}
	
	public void run() {
		while (true) {
			synchronized (mutex) {
				try {
					if (recordQueue.size() == 0)
						mutex.wait(); // wait for a record added.
				}
				catch (Exception e) {
					Console.error("Waiting on background queue", this, e);
				}
			}
			while (recordQueue.size() > 0) {
				try {
					ThreadedQueueItem item  = (ThreadedQueueItem) recordQueue.dequeue();
					item.handler.readQueue(item.object);
				}
				catch (Exception e) {
					Console.error("Posting dequeued object.", this, e);
				}
			}
			if (flushListener != null) {
				synchronized (flushListener) {
					flushListener.notifyAll();
				}
			}
		}
	}

}

