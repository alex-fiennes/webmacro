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
import java.rmi.ServerException;
import org.opendoors.osql.*;
import org.opendoors.instant.lcis.RemotelyExecutable;
import org.opendoors.instant.lcis.Connection;
import org.opendoors.intf.QueueListener;
import org.opendoors.util.*;

/**
 * Creates a vlh partition.
 * <p>
 * Partitions are not queued so the client application gets
 * an immediate exception if the queue could not be created.
 */
public class CreatePartition extends AbstractVLHMethod {
	
	/**
	 * Sets the immutable values for creating
	 * a partition.
	 */
	public void setValues(String partition, String owner,
						  String immutableCertificate, String mutableCertificate) {
		this.partition = partition;
		this.owner = owner;
		setCertificates(immutableCertificate, mutableCertificate);
	}

	public void localExecution(String rootDir) throws Exception {
		String newDir = rootDir + "/" + partition;
		if (SysEnv.isDirectory(newDir))
			throw new Exception("Partition exists=" + partition + " in " + rootDir);
		SysEnv.makeDirectory(newDir);
		// now save the create:
		String newFile = rootDir + "/" + partition + ".sclass";
		SysEnv.iceObject(newFile, this);
	}

	/**
	 * The method to executed to provide the create partition service.
	 * <p>
	 * Note this method provides a return value to the peer.
	 */
	protected void method() throws Exception {
		//System.out.println("Creating partition=" + partition);
    	// get a connection:
		java.sql.Connection connection = (java.sql.Connection) VLHServiceEnv.connectionPool.get();
		Exception ex = null;

		try {
			// execute the create partition statement
			SQLStatement create = new SQLStatement(StatementType.DML);
			create.setStatementValue(VLHServiceEnv.sqlPartitionInsert);
			create.executePreparedStatement(connection, this); // auto-closes the statement.
		}
		catch (Exception e) {
			ex = e;
		}
		finally {
			// return the connection to the pool; autocommit is on.
			VLHServiceEnv.connectionPool.free(connection);
		}
		if (ex != null)
			returnValue = new ServerException(ex.getMessage());
		else
			returnValue = null;
		//System.out.println("Sending=");
		peerConnection.sendObject(this);
		//System.out.println(this.toString());
		
	}

	/**
	 * Call back to provide the values required to create a partition.
	 */
	public PreparedStatement getSQLParameters(PreparedStatement p) throws Exception {
		p.setString(1, partition);
		p.setString(2, owner);
		p.setString(3, immutableCertificate);
		p.setString(4, mutableCertificate);
		return p;
	}
}
