/*
* Copyright Open Doors Software and Acctiva, 1996-2001.
*
* Software is provided according to the MPL license.
* Open Doors Software and Acctiva provide this
* software on an as-is basis and make no representation as
* to fitness for a specific purpose.
*
* Direct all questions and comments to support@opendoors.com
*/
package org.opendoors.cache;
import java.util.Properties;
import java.util.Hashtable;

/**
 * Some cache implementations will
 * expose an explicit udpate method
 * allowing clients to perform
 * pending updates to the cache which
 * brings the cache up-to-date with
 * recent mutations to the cache.
 */
public interface UpdateableCache extends Cache {

	/**
	 * Updates the cache.
	 * <p>
	 * Upon completion, the cache is up to date
	 * w/respect to all pending updates.
	 */
	public void update();

}

