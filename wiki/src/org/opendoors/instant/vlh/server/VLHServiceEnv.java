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
import java.util.Properties;
import java.io.PrintStream;
import java.sql.*;
import org.opendoors.instant.vlh.*;
import org.opendoors.util.*;
import org.opendoors.adt.*;
import org.opendoors.osql.*;

/**
 * VLHServiceEnv is a static class permitting
 * the creation of a single service environment
 * for vlh on the middle tier.
 * <p>
 * In addition, there is a singleton
 * class for
 * <p>
 * This class is package protected
 * and not intended to be used except in
 * supporting the vlh classes of this package.
 */
class VLHServiceEnv {

	static Properties configuration;
    static ConnectionFactory connectionFactory;
    static ConnectionPool connectionPool;
    static boolean statusEnabled = true;

	// the sql statements:
    static String sqlValueInsert;
    static String sqlPartitionInsert;
    static String sqlPartitionCount;
    static String sqlPartitionImmutableCount;
    static String sqlPartitionMutableCount;
    static String sqlValueUpdate;
    static String sqlGetValueKey;
	static String sqlValueRemove;
	static String sqlValueRemoveAll;
	static String sqlPartitionRemove;
	static String sqlEnumerationKeys;
	static String sqlEnumerationValues;
	static String sqlSize;
	static String sqlSearchValue;
	static String sqlSearchKey;
	static String sqlIdentity;
	
    // performance:
    static int initialCacheWriteSize = 100;
    static boolean queuingEnabled = false;
    static boolean writeCacheEnabled = false;

	// queuing support
    static ThreadedQueue queue;
	static CachedQueue cachedQueue;
	static long start = 0;
	static Object flushListener;
    

	/**
	 * Initializes the environment attributes
	 * required to run this
	 * implementation of the vlh service.
	 */
	static void init(Properties properties) throws Exception {
		configuration = properties;
		
		// logging options provided:
        // set up the console output streams if there are files:
        String file = configuration.getProperty("Log.Errors.File");
        try {
        	PrintStream err = SysEnv.printOutput(file);
        	Console.errStream = err;
        }
        catch (Exception e) {}
        file = configuration.getProperty("Log.Notices.File");
        try {
        	PrintStream out = SysEnv.printOutput(file);
        	Console.outStream = out;
        }
        catch (Exception e) {}
        String logNotices = configuration.getProperty("Log.Notices");
        if (logNotices != null)
        	if (logNotices.equalsIgnoreCase("true"))
	        	Console.reportingEnabled = true;
	        else
	        	Console.reportingEnabled = false;

        // let it be known:
        Console.report("VLHServiceEnv. Initializing at=" + new java.util.Date(), null);

		// create the connection factory:
		connectionFactory = (ConnectionFactory)
			SysEnv.objectFactory(configuration.getProperty("VLH.jdbc.ConnectionFactory"));

		// set the connection factory properties:
		connectionFactory.initConnectionFactory(configuration);
		// identify the pool to the service using the factory just built.
		connectionPool = new ConnectionPool(connectionFactory);

		// PERFORMANCE
		String q = configuration.getProperty("VLH.WriteQueuing.Enabled");
		if (q != null && q.equalsIgnoreCase("true"))
			queuingEnabled = true;
		if (! queuingEnabled) {
			writeCacheEnabled = false;
			queue = new ThreadedQueue();
		}
		else {
			// is the write cache enabled?
			q = configuration.getProperty("VLH.WriteCache.Enabled");
			if (q != null && q.equalsIgnoreCase("true")) {
				writeCacheEnabled = true;
				cachedQueue = new CachedQueue();
				queue = new ThreadedQueue( cachedQueue );
			}
			else {
				writeCacheEnabled = false;
				queue = new ThreadedQueue();
			}
			// enables the listening of an empty queue
			flushListener = new Object();
			queue.setFlushListener(flushListener);
		}	
		Console.report("VLHServiceEnv. Queuing enabled="+queuingEnabled + " WriteCacheEnabled=" + writeCacheEnabled, null);

		// test:
		Connection connection = (Connection) connectionPool.get();
		Console.report("VLHServiceEnv. Testing JDBC connection=", connection);
		connectionPool.free(connection);
		
		// unpack the sql commands:
		sqlPartitionInsert = configuration.getProperty("sql.partition.insert");
		sqlPartitionCount = configuration.getProperty("sql.partition.count");
		sqlPartitionImmutableCount = configuration.getProperty("sql.partition.immutable.count");
		sqlPartitionMutableCount = configuration.getProperty("sql.partition.mutable.count");
		sqlValueInsert = configuration.getProperty("sql.value.insert");
		sqlValueUpdate = configuration.getProperty("sql.value.update");
		sqlGetValueKey = configuration.getProperty("sql.get.value.key");
		sqlValueRemove = configuration.getProperty("sql.value.remove");
		sqlValueRemoveAll = configuration.getProperty("sql.value.removeAll");
		sqlPartitionRemove = configuration.getProperty("sql.partition.remove");
		sqlEnumerationKeys = configuration.getProperty("sql.enumeration.keys");
		sqlEnumerationValues = configuration.getProperty("sql.enumeration.values");
		sqlSize = configuration.getProperty("sql.size");
		sqlSearchValue = configuration.getProperty("sql.search.value");
		sqlSearchKey = configuration.getProperty("sql.search.key");
		sqlIdentity = configuration.getProperty("sql.identity");
	}

	/**
	 * Called to manage metrics.
	 */
	static void metrics() {
		if (start == 0) {
			start = System.currentTimeMillis();
		}
	}
	
	static void waitForFlushedQueue() {
		// allow other tasks to work
		Thread.yield();
		if (! queuingEnabled || queue.size() == 0)
			return;
		synchronized (flushListener) {
			if (queue.size() == 0)
				return; // race resolved.
			try {
				flushListener.wait();
				Thread.yield();
			}
			catch (Exception e) {}
		}
	}
	
	/**
	 * Throws an exception if the certificates do not match
	 * a row in the database.
	 */
	static void testStoredCertificates(String partition, String immutable, String mutable)
					throws Exception {
		CertificateProvider params = new CertificateProvider(partition, immutable, mutable);
		java.sql.Connection connection = (java.sql.Connection) connectionPool.get();
		SQLStatement count = null;
		Exception ex = null;
		try {
			// execute the get sqlstatement as a DQL
			count = new SQLStatement(StatementType.DQL);
			count.setStatementValue(sqlPartitionCount);
			TableDataSet tableValue = (TableDataSet) count.executePreparedStatement(connection, params); // auto-closes the statement.
			// return the value as a byte array to the client
			Number value = (Number) tableValue.getCell(0, 0);
			if (value.intValue() != 1)
				throw new Exception("No partition found with key and certificates provided");
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
			throw ex;
	}

}

class CertificateProvider implements SQLParameterProvider {
	String immutable, mutable, partition;
		
	CertificateProvider(String partition, String immutable, String mutable) {
		this.partition = partition;
		this.immutable = immutable;
		this.mutable = mutable;
	}
	public PreparedStatement getSQLParameters(PreparedStatement p) throws Exception {
		p.setString(1, partition);
		p.setString(2, immutable);
		p.setString(3, mutable);
		return p;
	}
}
