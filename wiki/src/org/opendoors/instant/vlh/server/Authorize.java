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
import org.opendoors.instant.lcis.Connection;
import org.opendoors.util.*;

/**
 * Implements the vlh authorize() semantic using a jdbc implementation.
 */
public class Authorize extends AbstractVLHMethod {

	/**
	 * If true, the partition and immutable certificate match a partition.
	 */
	public boolean immutableAuthorized = false;

	/**
	 * If true, the partition and mutable certificate match a partition.
	 */
	public boolean mutableAuthorized = false;
	
	private String certParam;

	/**
	 * Set the values to authorize the use of the partition.
	 */
	public void setValues(String partition, String immutableCertificate, String mutableCertificate) {
		this.partition = partition;
		setCertificates(immutableCertificate, mutableCertificate);
	}

	/** Authorizes access to the partition. */
	public void localExecution(String rootDir) throws Exception {
		String newDir = rootDir + "/" + partition;
		if (! SysEnv.isDirectory(newDir))
			throw new Exception("Partition does not exist=" + partition + " in " + rootDir);
		String newFile = rootDir + "/" + partition + ".sclass";
		//System.out.println("Authorizing file = " + newFile);
		CreatePartition record = (CreatePartition) SysEnv.thawObject(newFile);
		immutableAuthorized = record.immutableCertificate.equals(immutableCertificate);
		mutableAuthorized = record.mutableCertificate.equals(mutableCertificate);
	}


	/**
	 * Sets the authorization values and returns this
	 * object to the server with the values set.
	 */
	protected void method() throws Exception {
    	// get a sql connection:
		java.sql.Connection connection = (java.sql.Connection) VLHServiceEnv.connectionPool.get();
		SQLStatement cert = null;
		Exception ex = null;
		try {
			// execute the statement on the immutable
			cert = new SQLStatement(StatementType.DQL);
			cert.setStatementValue(VLHServiceEnv.sqlPartitionImmutableCount);
			certParam = immutableCertificate;
			TableDataSet tableValue = (TableDataSet) cert.executePreparedStatement(connection, this); // auto-closes the statement.
			Number value = (Number) tableValue.getCell(0, 0);
			immutableAuthorized = (value.intValue() == 1);
			
			//now the mutable:
			cert.setStatementValue(VLHServiceEnv.sqlPartitionMutableCount);
			certParam = mutableCertificate;
			tableValue = (TableDataSet) cert.executePreparedStatement(connection, this); // auto-closes the statement.
			value = (Number) tableValue.getCell(0, 0);
			mutableAuthorized = (value.intValue() == 1);
			System.out.println("ImmAuth=" + immutableAuthorized + " MutAuth=" + mutableAuthorized);
		}
		catch (Exception e) {
			// keep the original exception
			ex = e;
			e.printStackTrace(System.err);
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
		p.setString(2, certParam);
		return p;
	}

}
