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
 * Base class to implement the vlh methods according to a jdbc pattern.
 */
abstract public class AbstractVLHMethod implements RemotelyExecutable, SQLParameterProvider, QueueListener {

	protected String partition;
	protected String owner;
	protected String key;
	protected byte[] value;
	protected boolean contains;
	public Object returnValue;
	protected String immutableCertificate;
	protected String mutableCertificate;

	transient protected org.opendoors.instant.lcis.Connection peerConnection;
	
	/**
	 * Certificates if null are translated to 'null' for the purpose of sql comparisons.
	 */
	protected void setCertificates(String immutableCertificate, String mutableCertificate) {
		if (immutableCertificate == null)
			this.immutableCertificate = "null";
		else
			this.immutableCertificate = immutableCertificate;
		if (mutableCertificate == null)
			this.mutableCertificate = "null";
		else
			this.mutableCertificate = mutableCertificate;
	}

	/**
	 * The remote method path to execute the vlh method.
	 */
	public void execute(Connection peerConnection) throws Exception {
		this.peerConnection = peerConnection;
		method();
	}

	/** The queue listener method run in a separate thread. */
	public void readQueue(Object myself) {
		try {
			method();
		}
		catch (Exception e) {
			try {
				peerConnection.sendObject(e);
			}
			catch (Exception sendEx) {
				Console.error("AbstractVLHMethod. Unable to send error=" + e + " method=" + this, peerConnection, sendEx);
			}
		}
	}

	/**
	 * Implement this method to perform the vlh service.
	 */
	abstract protected void method() throws Exception;

}
