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

/**
 * ThreadedQueuePool implements a pooling of threaded queues which can be used to perform work
 * within a threaded queue.
 */
public class ThreadedQueuePool extends Pool {

    protected int initial = 5;
    protected int increment = 10;
    protected boolean first = true;

	protected Object[] createPool() throws Exception {
		int size;
		if (first)
			size = initial;
		else
			size = increment;
		ThreadedQueue[] newPool = new ThreadedQueue[size];
		for (int index = 0; index < size; index++)
			newPool[index] = new ThreadedQueue();
		first = false;
		return newPool;
	}
}
