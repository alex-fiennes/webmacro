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
import java.util.*;
import org.opendoors.osql.*;
import org.opendoors.util.*;

/**
 * Provides all the core logic for an enumeration of either keys or of elements().
 */
public class AbstractEnumerator extends AbstractVLHMethod {

	/** The keys or elements here. */
	protected Object[] objectArray;
	
	/**
	 * Set the immutable values for getting the keys.
	 */
	public void setValues(String partition)  {
		this.partition = partition;
	}

	/**
	 * A convenience method for obtaining an Enumeration of the keys.
	 */
	public Enumeration keys() {
		return new VLHEnumerator(objectArray);
	}

	/**
	 * A convenience method for obtaining an Enumeration of the objects.
	 */
	public Enumeration elements() {
		// convert the array elements to their object values
		if (objectArray != null) {
			for (int index = 0; index < objectArray.length; index++)
				objectArray[index] = SysEnv.thawObjectByteArray((byte[])objectArray[index]);
		}
		return new VLHEnumerator(objectArray);
	}

	

	/**
	 * Gets the value which may be either an instance of Keys or of Elements.
	 * And, returns this object back to the peer.
	 */
	protected void method() throws Exception {
    	// get a sql connection:
		java.sql.Connection connection = (java.sql.Connection) VLHServiceEnv.connectionPool.get();
		SQLStatement enum = null;

		Exception ex = null;
		try {
			// wait for elements to be flushed.
			//VLHServiceEnv.waitForFlushedQueue();

			// execute the keys/values sqlstatement as a DQL
			enum = new SQLStatement(StatementType.DQL);
			boolean valueEnum = false;
			if (this instanceof Keys)
				enum.setStatementValue(VLHServiceEnv.sqlEnumerationKeys);
			else {
				enum.setStatementValue(VLHServiceEnv.sqlEnumerationValues);
				valueEnum = true;
			}
			TableDataSet tableValue = (TableDataSet) enum.executePreparedStatement(connection, this); // auto-closes the statement.
			// rows?
			if (tableValue.getRowCount() > 0) {
				// convert blobs to byte arrays if present
				if (valueEnum)
					tableValue.blobsToByteArray();
				tableValue.transpose(); // transpose rows to columns
				// return the array to the client in this.
				//Console.report("Inspecting col[0]=", tableValue.getColumnDataSet()[0] );
				//Console.report("Inspecting col[0][0]=", tableValue.getColumnDataSet()[0][0] );
				this.objectArray = (tableValue.getColumnDataSet())[0]; //column 0 is the keys;
			}
			else {
				this.objectArray = null;
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
			ex.printStackTrace(System.err);
		peerConnection.sendObject(this); // send this object back with the value.
	}

	public PreparedStatement getSQLParameters(PreparedStatement p) throws Exception {
		p.setString(1, partition);
		return p;
	}

}

