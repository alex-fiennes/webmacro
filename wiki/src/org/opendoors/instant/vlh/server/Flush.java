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
import org.opendoors.util.*;

/**
 * Implements the vlh size() semantic using a jdbc implementation.
 */
public class Flush extends AbstractVLHMethod {

	/**
	 * Set the immutable values for getting an object from the vlh.
	 */
	public void setValues(String partition) {
		this.partition = partition;
	}

	/**
	 * Requests notification when the cache is zero.
	 */
	protected void method() throws Exception {
 		Exception ex = null;
		try {
			VLHServiceEnv.waitForFlushedQueue();
			peerConnection.sendObject(this); // send this object back with the value.
		}
		catch (Exception e) {
			// keep the original exception
			ex = e;
		}
		if (ex != null)
			throw ex;
	}
	public PreparedStatement getSQLParameters(PreparedStatement p) throws Exception {
		return p;
	}
}
