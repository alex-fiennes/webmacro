/*
 * @(#)QueueListener.java	2.0 1.1 97/10/16
 * 
 * Copyright (c) 1999 Open Doors Software, Inc. All Rights Reserved.
 * 
 * Open Doors Software MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. OPEN DOORS SOFTWARE SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 */
 
package org.opendoors.intf;

/**
 * An interface allowing a listener to obtain objects from a queue.
 * @see org.opendoors.core.model.ThreadedQueue
 */
public interface QueueListener {

	/**
	 * Reads from the queue.
	 */
	public void readQueue(Object o);

}
