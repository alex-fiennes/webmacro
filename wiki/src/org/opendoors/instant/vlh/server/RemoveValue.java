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
 * Implements the vlh remove(String) semantic using a jdbc implementation.
 */
public class RemoveValue extends QueuableVLHMethod {

	/**
	 * Sets the immutable values required to remove an object.
	 */
	public void setValues(String partition, String key) {
		this.partition = partition;
		this.key = key;
	}

	/** Removes the value locally. */
	public void localExecution(String rootDirectory) {
		String file = rootDirectory + "/" + partition + "/" + key;
		try {
			SysEnv.removeFile(file);
		}
		catch (Exception e) {
		}
	}

	/**
	 * Provides the jdbc implementation of a remove.
	 */
	protected void method() throws Exception {
    	// get a connection:
		java.sql.Connection connection = (java.sql.Connection) VLHServiceEnv.connectionPool.get();
		SQLStatement remove = null;

		Exception ex = null;
		try {
			// execute the insert semantic first statement
			remove = new SQLStatement(StatementType.DML);
			remove.setStatementValue(VLHServiceEnv.sqlValueRemove);
			remove.executePreparedStatement(connection, this); // auto-closes the statement.
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
		p.setString(2, key);
		return p;
	}
}
