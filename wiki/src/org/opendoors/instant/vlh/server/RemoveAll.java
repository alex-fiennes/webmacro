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
import org.opendoors.util.SysEnv;

/**
 * Implements the vlh removeAll() semantic using a jdbc implementation.
 */
public class RemoveAll extends AbstractVLHMethod {

	/**
	 * Sets the immutable values required to remove an object.
	 */
	public void setValues(String partition) {
		this.partition = partition;
	}

	/**
	 * Provides the jdbc implementation of a remove.
	 */
	protected void method() throws Exception {
		// first clear the queue if enabled
		if (VLHServiceEnv.writeCacheEnabled) {
			VLHServiceEnv.cachedQueue.clear();
			// allow any writes in progress to complete:
			Thread.sleep(10);
		}

    	// get a connection:
		java.sql.Connection connection = (java.sql.Connection) VLHServiceEnv.connectionPool.get();
		SQLStatement removeAll = null;

		Exception ex = null;
		try {
			// execute the insert semantic first statement
			removeAll = new SQLStatement(StatementType.DML);
			removeAll.setStatementValue(VLHServiceEnv.sqlValueRemoveAll);
			removeAll.executePreparedStatement(connection, this); // auto-closes the statement.
			//System.out.println("Clear Complete. Partition=" + partition);
		}
		catch (Exception e) {
			// keep the original exception.
			ex = e;
		}
		finally {
			// return the connection to the pool; autocommit is on.
			VLHServiceEnv.connectionPool.free(connection);
		}
		if (ex != null)
			throw ex;
	}

	public PreparedStatement getSQLParameters(PreparedStatement p) throws Exception {
		p.setString(1, partition);
		return p;
	}
}
