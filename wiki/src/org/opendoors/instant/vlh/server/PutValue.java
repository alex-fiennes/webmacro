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
 * Implements the vlh put(String, Object) semantic using a jdbc implementation.
 * <p>
 * The put() sql implementation can be tuned and a put() with
 * queuing and caching enabled results in steep performance
 * improvements in cases where an object is updated frequently
 * over a short period of time.
 */
public class PutValue extends QueuableVLHMethod {


	/**
	 * Sets the immutable values required to put an object.
	 */
	public void setValues(String partition, String key, Object value)  {
		this.partition = partition;
		this.key = key;
		// objects get stored as bytes as soon as they are provided.
		this.value = SysEnv.iceObjectAsByteArray(value);
	}

	/** Puts the value locally. */
	public void localExecution(String rootDirectory) {
		String file = rootDirectory + "/" + partition + "/" + key;
		try {
			SysEnv.writeOutputFile(file, value);
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e.toString());
		}
	}

	/**
	 * Creates the partition using the implementation
	 * provided by the vlh server service.
	 */
	protected void method() throws Exception {
    	// get a connection:
		//System.out.println("Putting=" + key + "/" + value);
		java.sql.Connection connection = (java.sql.Connection) VLHServiceEnv.connectionPool.get();
		SQLStatement put = null;

		Exception ex = null;
		try {
			// execute the insert semantic first statement
			put = new SQLStatement(StatementType.DML);
			put.setStatementValue(VLHServiceEnv.sqlValueInsert);
			put.executePreparedStatement(connection, this); // auto-closes the statement.
			//System.out.println("Put Complete");
		}
		catch (Exception e) {
			// keep the original exception and now attempt a replace operation:
			ex = e;
			try {
				// try the update:
				put.setStatementValue(VLHServiceEnv.sqlValueUpdate);
				put.executePreparedStatement(connection, new SQLParameterProvider() {
					public PreparedStatement getSQLParameters(PreparedStatement p) throws Exception {
						p.setBytes(1, value);
						p.setString(2, partition);
						p.setString(3, key);
						return p;
					}
				});
				ex = null;
				//System.out.println("Put Complete Using Update. Key=" + key);
			}
			catch (Exception updateEx) {
				ex = updateEx; // pass back the update operation as the last one tried.
			}
		}
		finally {
			// return the connection to the pool; autocommit is on.
			VLHServiceEnv.connectionPool.free(connection);
		}
		// the put operation is asynch so nothing to send to the client
		if (ex != null)
			throw ex;
	}

	public PreparedStatement getSQLParameters(PreparedStatement p) throws Exception {
		p.setString(1, partition);
		p.setString(2, key);
		p.setBytes(3, value);
		return p;
	}
	
	public String toString() {
		return ("<Put>Key=" + key + "</>");
	}
}
