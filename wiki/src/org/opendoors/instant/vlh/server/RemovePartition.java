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
import org.opendoors.util.SysEnv;

/**
 * Implements the vlh removePartition(String).
 * <p>
 * This operation automatically removes all elements
 * of the partition as well.
 */
public class RemovePartition extends AbstractVLHMethod {

	private String[] storedCertificate;
	
	
	/**
	 * Sets the immutable values required to remove an object.
	 */
	public void setValues(String partition,
						  String immutableCertificate, String mutableCertificate){
		this.partition = partition;
		setCertificates(immutableCertificate, mutableCertificate);
	}

	/** Removes the partition at the root directory. */
	public void localExecution(String rootDir) throws Exception {
		String newDir = rootDir + "/" + partition;
		if (! SysEnv.isDirectory(newDir))
			throw new Exception("Partition does not exist=" + partition + " in " + rootDir);
		String newFile = rootDir + "/" + partition + ".sclass";
		CreatePartition record = (CreatePartition) SysEnv.thawObject(newFile);
		if (! record.immutableCertificate.equals(immutableCertificate) ||
			! record.mutableCertificate.equals(mutableCertificate) )
			throw new SecurityException("Certificates do not match");
		// remove the contents first:
		String[] file = SysEnv.dirList(newDir);
		for (int index = 0; index < file.length; index++)  {
        	//System.out.println("Removing File=" + file[index]);
			SysEnv.removeFile(newDir + "/" + file[index]);
		}

		// remove the directory:
		SysEnv.removeFile(newDir);

		// remove the certificate file:
		SysEnv.removeFile(newFile);
	}

	/**
	 * Provides the jdbc implementation of a remove.
	 */
	protected void method() throws Exception {
		System.out.println("Removing partition=" + partition);
    	// get a connection:
		java.sql.Connection connection = (java.sql.Connection) VLHServiceEnv.connectionPool.get();
		SQLStatement removePartition = null;
		SQLStatement certificates = null;
		Exception ex = null;
		try {
			// throws an exception if not found:
			VLHServiceEnv.testStoredCertificates(partition,
											immutableCertificate, mutableCertificate);

			// execute the removeAll semantic first
			removePartition = new SQLStatement(StatementType.DML);
			removePartition.setStatementValue(VLHServiceEnv.sqlValueRemoveAll);
			removePartition.executePreparedStatement(connection, this); // auto-closes the statement.

			removePartition.setStatementValue(VLHServiceEnv.sqlPartitionRemove);
			removePartition.executePreparedStatement(connection, this); // auto-closes the statement.
			
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
			returnValue = new ServerException(ex.getMessage());
		else
			returnValue = null;
		peerConnection.sendObject(this);
	}

	public PreparedStatement getSQLParameters(PreparedStatement p) throws Exception {
		p.setString(1, partition);
		return p;
	}
}
