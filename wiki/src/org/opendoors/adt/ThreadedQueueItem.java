/*
 * @(#)ThreadedQueue.java
 * 
 * Copyright (c) 1999 Open Doors Software, Inc. All Rights Reserved.
 * Open Doors Software MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. OPEN DOORS SOFTWARE SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 2.0
 * 
 */
package org.opendoors.adt;
import org.opendoors.intf.QueueListener;

/**
 * ThreadedQueueItem declares publically the content of the
 * an item on the queue: the object and its listener.
 * <p>
 * The contents are declared publicly for performance goals.
 * @author Lane Sharman
 * @version 2.0
 */
public class ThreadedQueueItem {

	/** The item. */
	public Object object;

	/** The listener for this object when dequeued. */
	public QueueListener handler;

	ThreadedQueueItem(Object object, QueueListener handler) {
		this.object = object;
		this.handler = handler;
	}
}

