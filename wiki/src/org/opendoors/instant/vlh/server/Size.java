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
import org.opendoors.instant.lcis.Connection;
import org.opendoors.util.*;

/**
 * Implements the vlh size() semantic using a jdbc implementation.
 */
public class Size extends AbstractVLHMethod {

	public int count;

	/**
	 * Set the immutable values for getting an object from the vlh.
	 */
	public void setValues(String partition) {
		this.partition = partition;
	}

	/**
	 * A convenience method for getting the size.
	 */
	public int size() {
		return this.count;
	}

	/** Puts the value locally. */
	public void localExecution(String rootDirectory) {
		String dir = rootDirectory + "/" + partition + "/";
		try {
			this.count = SysEnv.dirListSize(rootDirectory);
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e.toString());
		}
	}

	/**
	 * Gets the size. Returns this object back to the peer.
	 */
	protected void method() throws Exception {
    	// get a sql connection:
		java.sql.Connection connection = (java.sql.Connection) VLHServiceEnv.connectionPool.get();
		SQLStatement size = null;

		Exception ex = null;
		try {
			// execute the get sqlstatement as a DQL
			size = new SQLStatement(StatementType.DQL);
			size.setStatementValue(VLHServiceEnv.sqlSize);
			TableDataSet tableValue = (TableDataSet) size.executePreparedStatement(connection, this); // auto-closes the statement.
			// return the value as a byte array to the client
			Number value = (Number) tableValue.getCell(0, 0);
			this.count = value.intValue();
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

	/**
	 * Provides description of contents.
	 */
	public String toString() {
			
		return ("<Size>" + count + "</>");
	}

}
