/*
 * @(#)SequenceServer.java	1.1 97/mm/dd
 * 
 * Copyright (c) 1996, 1997 Open Doors Software, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Open Doors Software
 * Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Open Doors Software.
 * 
 * Open Doors Software MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. OPEN DOORS SOFTWARE SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1
 * 
 */

package org.opendoors.osql;

import java.io.Serializable;
import java.math.*;
import java.util.*;
import java.rmi.ServerException;
import org.opendoors.intf.Sequence;
import org.opendoors.util.*;
import org.opendoors.util.PostponeObservable;



/**
 * Provide consistent and unique serial number generation on a network wide basis.
 * <p>
 * This class may be used with caution on a disconnected client provided it does not
 * require access to a host due to sequence exhaustion.
 * <p>
 * This class is Oracle specific and specific to the definition
 * within the schema named "SEQ_GENERATOR".
 */
public class SequenceServer implements Serializable, Observer {

	/**
	 * The select statement to return the sequence number
	 */
	protected static final String selectStatement = "select seq_generator.nextval from dual";

	/**
	 * The serial number
	 */
	protected long sequence = 0;

	/**
	 * The modulo number on the host. When the Sequence number modulo this number is zero a new number is needed
	 */
	protected static final long increment = 250;

	/**
	 * The modulo in which to warn the user.
	 */
	protected static final long reorderLevel = 200;

	/**
	 * The sequence server must be valid at all times to be used.
	 */
	protected boolean validSequence = false;

	/**
	 * The static singleton reference.
	 */
	protected static SequenceServer instance = null;

	protected static final int POSTPONE_INTERVAL = 3000;

	/**
	 * The observer to manage the integrity of the sequence server.
	 */
	transient protected PostponeObservable observer = null;

	/**
	 * The connection pool for the server.
	 */
	transient protected ConnectionPool connectionPool = null;

	/**
	 * Instantiates a sequence server. There should only be one per vm space.
	 * Serialized instances should not be shared.
	 */
	protected SequenceServer() {
	}
	

	/**
	 * Get the single instance of the sequence server.
	 * Explicitly throws an exception if the instance cannot be returned.
	 */
	public static SequenceServer getInstance() throws Exception {
		if (instance == null) {
			// get the disk image
			instance = (SequenceServer) SysEnv.thawObject("SequenceServer.sclass");
			// error? No saved image?
			if (instance == null) {
				SequenceServer valid = null;
				try {
					valid = new SequenceServer();
					valid.refreshSequence();
				}
				catch (Exception e) {
					throw e;
				}
				instance = valid;
				instance.observer = new PostponeObservable(POSTPONE_INTERVAL, true);
				instance.observer.addObserver(instance);
				instance.save(); // save it out now.
			}
			else {
				instance.observer = new PostponeObservable(POSTPONE_INTERVAL, true);
				instance.observer.addObserver(instance);
			}	
		}
		if (instance != null)
			return instance;
		else
			throw new Exception("Cannot initialize sequence server");
			
	}
	
	/**
	 * Sets the server's connection pool.
	 */
	public void setConnectionPool(ConnectionPool connectionPoo) {
		this.connectionPool = connectionPool;
	}

	/** Call back for notification of an update */
	public void update(Observable o, Object argument) {
		System.out.println("Saving sequence from observer...");
		save();
	}

	/**
	 * Gets a unique serial number for a record or an object.
	 * This is an internal method and should be used with caution.
	 * @return A unique number or INVALID_IDENTIFIER if the sequence
	 * cannot be computed on the host. Users should test for this result when
	 * using this method.
	 */
	public long getNext() {
		//System.out.println("Getting global sequence. sequence % increment=" + sequence % increment);
		try {
			if (sequence % increment == 0 || ! validSequence ) {
				refreshSequence();
			}
			observer.setChanged();
			return sequence++;
		}
		catch (Exception e) {
			Console.error("Error. You have exhausted your local pool of sequence numbers. Please shutdown and connect to your central server.", this, e);
			validSequence = false;
			throw new IllegalStateException("Sequence numbers exhausted: " + e.toString());
		}
	}

	/**
	 * Gets the next sequence.
	 * <p>
	 * Returns an exception for any error.
	 * @return The next sequence
	 * @throws If the sequence could not be refreshed or it could not be saved.
	 */
	public long nextSequence() throws Exception {
		//System.out.println("Getting global sequence. sequence % increment=" + sequence % increment);
		if (runningLow())
			showWarning();
		if (sequence % increment == 0 || ! validSequence) {
			refreshSequence();
		}
		observer.setChanged();
		return sequence++;
	}

	/**
	 * Gets the next sequence as a String.
	 */
	public String nextSequenceAsString() throws Exception {
		return nextSequence() + "";
	}

	/**
	 * Informs the caller if the current sequence number is low and close to being refreshed.
	 * @return true If the current sequence modulo the increment is greater than the reorder level.
	 */
	public boolean runningLow() {
		return ( ! validSequence || (sequence % increment) >= reorderLevel);
	}

	/**
	 * Refreshes the serial number server by accessing the hosting database directly.
	 * <p>
	 * If the host database cannot be accessed the sequence is unchanged.
	 * @return The new sequence number available to assign or
	 * @throws If the host cannot be reached or a connection is not in place, an exception is thrown.
	 */
	public void refreshSequence() throws Exception {
    	// get a sql connection:
		java.sql.Connection connection = (java.sql.Connection) connectionPool.get();
		SQLStatement get = null;
		Exception ex = null;
		try {
			// execute the get sqlstatement as a DQL
			get = new SQLStatement(StatementType.DQL);
			get.setStatementValue(selectStatement);
			TableDataSet tableValue = (TableDataSet) get.execute(connection);
			this.sequence = ((Number) tableValue.getCell(0, 0)).longValue();
			validSequence = true;
			
		}
		catch (Exception e) {
			// keep the original exception
			ex = e;
		}
		finally {
			// return the connection to the pool; autocommit is on.
			connectionPool.free(connection);
		}
		if (ex != null)
			throw new ServerException(ex.toString());
	}	

	/** Saves the sequence server to disk. */
	public void save () {
		if (validSequence) {
			SysEnv.iceObject("SequenceServer.sclass", this);
			System.out.println("Sequence Saved.");
		}
	}

	public void shutdown() {
		System.out.println("Closing Sequence server...");
		observer.deleteObservers();
		observer = null;
		save();
		instance = null;
	}
		

	/** Informs as to whether the sequence is currently valid. */
	public boolean validSequence() {
		return validSequence;
	}

	/** Shows the warning dialog unless the user has clicked the don't show anymore check box */
	public void showWarning() {
		Console.error("Sequence number warning. Sequence % increment=" + (sequence % increment), this, null);
	}

	public static void main(String[] argv) throws Exception {

	}
	

}
	
