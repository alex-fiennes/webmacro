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
package org.opendoors.instant.vlh.server;

import java.sql.*;
import org.opendoors.osql.*;
import org.opendoors.instant.lcis.RemotelyExecutable;
import org.opendoors.instant.lcis.Connection;
import org.opendoors.intf.QueueListener;
import org.opendoors.util.Console;

/**
 * VLH Methods which can use queuing subclass from this class.
 */

abstract public class QueuableVLHMethod extends AbstractVLHMethod {

	protected String partition;
	protected String owner;
	protected String key;
	protected byte[] value;
	transient protected org.opendoors.instant.lcis.Connection peerConnection;

	/**
	 * The remote method path to execute the vlh method.
	 */
	public void execute(Connection peerConnection) throws Exception {
		this.peerConnection = peerConnection;
		if (VLHServiceEnv.queuingEnabled)
			VLHServiceEnv.queue.addElement(this, this);
		else
			method();
	}

}
