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

import org.opendoors.util.*;
import org.opendoors.instant.lcis.server.*;
import org.opendoors.instant.lcis.*;
import java.util.*;

/**
 * StartServer starts the VLH Service within this package.
 * <p>
 * The service requires a reference to the properties for this specific intance.
 * <p>
 * Note. The VLH Server will hook in your custom connection event listener and your custom object
 * listener service. This makes the VLH Server extensible. Whenever the VLH processes a new
 * connection, a closed connection, or an incoming request from a client, your custom code
 * gains access to the event, the Connection back to the client, and to the request from the
 * client such as a put(), get(), or clear().
 * @see org.opendoors.instant.lcis.server.AbstractServer
 * @see org.opendoors.instant.lcis.server.ExecServer
 * @see org.opendoors.instant.RemoteEventListener
 * @see org.opendoors.instant.RemoteEventListener
 * @see org.opendoors.instant.Connection
 */
public class StartServer {

	static ExecServer execServer;
	
    /**
     * Start the server from the command line.
     * <p>
     * You must provide a file reference, not a url reference, to the property file:
     * <pre>
     * java org.opendoors.instant.vlh.server.StartServer </path/to/vlhServer.properties>
     * </pre> 
     */
	public static void main(String[] argv) throws Exception {
        Properties properties = SysEnv.loadPropertiesFromFile(argv[0]);
        // list the properties to system out:
        System.out.println("Starting VLH Service with following properties:");
        Enumeration keys = properties.keys();
        while (keys.hasMoreElements()) {
        	String key = (String) keys.nextElement();
        	String value = properties.getProperty(key);
        	System.out.println(key + "=" + value);
        }
        new StartServer().start(properties);
        vlhStatistics();
	}

    /**
     * Starts the VLH Service on the server according to the properties provided.
     * @exception Exception BaseServer
     * @exception VLH Service
     * @exception or JDBC Service could not be started.
     * @param properties The properties for the VLH Service.
     */
    public void start(Properties properties) throws Exception  {

        // initialize the vlh service environment.
        VLHServiceEnv.init(properties); // initializes the environment.

        // now start the exec server which will start the vlh service.
        int port = Integer.parseInt(properties.getProperty("VLH.Server.Port"));
		execServer = new ExecServer("VLH Dedicated Server", port); // the default base server.

		String className = properties.getProperty("VLH.Pluggable.ConnectionEventListener");
		if (className != null) {
			ConnectionEventListener listener = (ConnectionEventListener) SysEnv.objectFactory(className);
			execServer.addConnectionListener(listener);
		}
			
		className = properties.getProperty("VLH.Pluggable.MethodListener");
		if (className != null) {
			ObjectListener listener = (ObjectListener) SysEnv.objectFactory(className);
			execServer.addObjectListener(listener);
		}
			
		// start the server.
		execServer.startServer();
    }

    public static void vlhStatistics() {
    	try {
			while (true) {
				System.out.println("Reporting 10 seconds.");
				Console.report(report(), null);
				Thread.sleep(10000);
			}
		}
		catch (Exception e) {}
	}

	public static String report() {
		StringBuffer report = new StringBuffer();
		report.append("VLH Service Report at: ").append(new java.util.Date().toString()).append(SysEnv.lineSeparator)
			.append("Objects Processed To Date By Server=" + execServer.objectsReceivedToDate).append(SysEnv.lineSeparator);
		long tet = System.currentTimeMillis() - VLHServiceEnv.start;
		report.append("Total Elpased Time Since First Object=" + tet).append(SysEnv.lineSeparator);
		if (VLHServiceEnv.queuingEnabled) {
			report.append("Queuing Enabled. Length=" + VLHServiceEnv.queue.size()).append(SysEnv.lineSeparator);
			if (VLHServiceEnv.writeCacheEnabled) {
				report.append("Cache Enabled. Reads=" + VLHServiceEnv.cachedQueue.totalReads).append(SysEnv.lineSeparator)
					.append("Writes=" + VLHServiceEnv.cachedQueue.totalWrites).append(SysEnv.lineSeparator)
					.append("Cache Hits=" + VLHServiceEnv.cachedQueue.totalCacheHits);
			}
		}
		return report.toString();
	}

}

