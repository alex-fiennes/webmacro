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
import org.opendoors.util.StringHelper;

/**
 * Implements the vlh contains(Object)semantic.
 * <p>
 * The search returns true if the byte representation of the object
 * is identical to the byte representation in the store.
 */
public class SearchValue extends AbstractVLHMethod {

	/**
	 * Sets the immutable values required to search for an object.
	 */
	public void setValues(String partition, Object value) {
		this.partition = partition;
		// objects get stored as bytes as soon as they are provided.
		this.contains = false;
		this.value = SysEnv.iceObjectAsByteArray(value);
	}

	/**
	 * A convenience to tell if the object is contained
	 * in the store after the search has been performed.
	 */
	public boolean containsValue() {
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
			contains = VLHServiceEnv.cachedQueue.containsValue(partition, value);
			if (contains) {
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
		SQLStatement searchValue = null;

		Exception ex = null;
		try {
			// execute the keys/values sqlstatement as a DQL
			SQLStatement enum = new SQLStatement(StatementType.DQL);
			enum.setStatementValue(VLHServiceEnv.sqlEnumerationValues);
			TableDataSet tableValue = (TableDataSet) enum.executePreparedStatement(connection, this); // auto-closes the statement.
			if (tableValue.getRowCount() > 0) {
				tableValue.transpose(); // to
				Object[] values = (tableValue.getColumnDataSet())[0]; //column 0 is the keys
				for (int index = 0; index < values.length; index++) {
					if (StringHelper.compare(this.value, (byte[]) values[index])){
						contains = true;
						break;
					}
				}
			}
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
		return p;
	}

}
