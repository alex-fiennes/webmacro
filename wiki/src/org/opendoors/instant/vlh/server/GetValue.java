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

import java.io.InputStream;
import java.sql.*;
import java.rmi.ServerException;
// import oracle.sql.BLOB;
import org.opendoors.osql.*;
import org.opendoors.instant.lcis.Connection;
import org.opendoors.util.SysEnv;

/**
 * Implements the vlh get(String) semantic using a jdbc implementation.
 */
public class GetValue extends AbstractVLHMethod {

	/**
	 * Set the immutable values for getting an object from the vlh.
	 */
	public void setValues(String partition, String key)  {
		this.partition = partition;
		this.key = key;
	}

	/**
	 * A convenience method for deserializing the value.
	 * <p>
	 * Note: this should be done on the machine where the class originated, not the server.
	 */
	public Object getValue() throws Exception {
		if (value == null)
			return null;
		return SysEnv.thawObjectByteArray(value);
	}

	public void localExecution(String rootDirectory) {
		String file = rootDirectory + "/" + partition + "/" + key;
		try {
			value = SysEnv.readInputFile(file);
		}
		catch (Exception e) {
         // file not found
			// e.printStackTrace(System.err);
			value = null;
		}
	}
		

	/**
	 * Overrides the execute method and perform the operation directly without queuing.
	 */
	public void execute(Connection peerConnection) throws Exception {
		this.peerConnection = peerConnection;
		byte[] cachedValue = null;
		if (VLHServiceEnv.writeCacheEnabled) {
			cachedValue = VLHServiceEnv.cachedQueue.readCachedValue(partition, key);
			if (cachedValue != null) {
				value = cachedValue;
				peerConnection.sendObject(this);
				return; // object found.
			}
			Thread.sleep(10);
		}
		// delegate to method
		method();
	}

	/**
	 * Gets the value.
	 * And, returns this object back to the peer.
	 */
	protected void method() throws Exception {
    	// get a sql connection:
		java.sql.Connection connection = (java.sql.Connection) VLHServiceEnv.connectionPool.get();
		SQLStatement get = null;
		Exception ex = null;
		try {
			// execute the get sqlstatement as a DQL
			get = new SQLStatement(StatementType.DQL);
			get.setStatementValue(VLHServiceEnv.sqlGetValueKey);
			TableDataSet tableValue = (TableDataSet) get.executePreparedStatement(connection, this); // auto-closes the statement.
			tableValue.blobsToByteArray();
			// return the value as a byte array to the client
			this.value = (byte[]) tableValue.getCell(0, 0);
		}
		catch (Exception e) {
			// keep the original exception
			ex = e;
		}
		finally {
			// return the connection to the pool; autocommit is on.
			VLHServiceEnv.connectionPool.free(connection);
		}
		if (ex != null)
			returnValue = new ServerException(ex.toString());
		peerConnection.sendObject(this); // send this object back with the value.
		if ( key.equals("testVector") ) {
			System.out.println("testVector. Value=" + value + " returnValue=" + returnValue);
		}
		if (ex != null)
			ex.printStackTrace(System.err);

		

	}

	public PreparedStatement getSQLParameters(PreparedStatement p) throws Exception {
		p.setString(1, partition);
		p.setString(2, key);
		return p;
	}

}
