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
package org.opendoors.instant.vlh.client;
import org.opendoors.instant.lcis.*;

/**
 * An adapter class subclassed by the VLH Agent and Proxy implementations.
 */
class ListenerAdapter implements ObjectListener {

	/** Call back from connection with an object received. */
	public void objectReceived(Object object, Connection connection) {	}

	/** Call back from connection that the connection is closing. */
	public void connectionClosing(Connection connection) {	}
}



