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
import java.util.Enumeration;
import org.opendoors.osql.*;
import org.opendoors.instant.lcis.Connection;
import org.opendoors.util.SysEnv;

/**
 * Implements the vlh contains(Key)semantic.
 * <p>
 * The search returns true if the key is found in the cache or the
 * store.
 */
public class SearchKey extends AbstractVLHMethod {

	/**
	 * Sets the immutable values required to search for an object.
	 */
	public void setValues(String partition, String key)  {
		this.partition = partition;
		// objects get stored as bytes as soon as they are provided.
		this.key = key;
		this.contains = false;
	}

	public void localExecution(String rootDirectory) {
		String file = rootDirectory + "/" + partition + "/" + key;
		try {
			contains = SysEnv.filePresent(file);
		}
		catch (Exception e) {
			contains = false;
		}
	}

	/**
	 * A convenience to tell if the object is contained
	 * in the store after the search has been performed.
	 */
	public boolean containsKey() {
		return contains;
	}

	/**
	 * Overrides the execute method.
	 * <p>
	 * Searches the cache for the value before looking at the store.
	 */
	public void execute(Connection peerConnection) throws Exception {
		this.peerConnection = peerConnection;
		if (VLHServiceEnv.writeCacheEnabled) {
			if (VLHServiceEnv.cachedQueue.readCachedValue(partition, key) != null) {
				contains = true;
				peerConnection.sendObject(this);
				return; // object found.
			}
		}
		// delegate to method:
		method();
	}

	/**
	 * Gets the value.
	 * And, returns this object back to the peer.
	 */
	protected void method() throws Exception {
    	// get a sql connection:
		java.sql.Connection connection = (java.sql.Connection) VLHServiceEnv.connectionPool.get();
		SQLStatement searchKey = null;

		Exception ex = null;
		try {
			// execute the get sqlstatement as a DQL
			searchKey = new SQLStatement(StatementType.DQL);
			searchKey.setStatementValue(VLHServiceEnv.sqlSearchKey);
			TableDataSet tableValue = (TableDataSet) searchKey.executePreparedStatement(connection, this); // auto-closes the statement.
			// return the value as a byte array to the client
			contains = ((Number) tableValue.getCell(0, 0)).intValue() > 0;
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
			returnValue = ex;
		peerConnection.sendObject(this); // send this object back with the value.
	}

	public PreparedStatement getSQLParameters(PreparedStatement p) throws Exception {
		p.setString(1, partition);
		p.setString(2, key);
		return p;
	}

}
