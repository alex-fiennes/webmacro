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
import java.util.Enumeration;


/**
 * An implementation of a queue using the linked list.
 * @version 1.01 15 Feb 1996 
 * @author Cay Horstmann & Lane Sharman
 */
public class Queue {

	/**
	 * The linked list.
	 */
	private LinkedList data = new LinkedList();

	/**
	 * Appends the object to the queue.
	 */
	public void enqueue(Object o) {
		data.append(o);
	}

	/**
	 * Dequeues the object from the the queue.
	 */
	public Object dequeue() {
		data.reset();
		return data.remove();
	}

	/**
	 * Gets the size of the queue.
	 */
	public int size() { return data.size(); }

	/**
	 * Returns the elements of the queue.
	 */
	public Enumeration elements() { return data.elements(); }
	
	/**
	 * Clears the queue.
	 */
	public void clear() {
		data.clear();
	}

}

