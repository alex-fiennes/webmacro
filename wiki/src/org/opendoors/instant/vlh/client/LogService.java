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
package org.opendoors.instant.vlh.client;
import java.io.PrintStream;
import org.opendoors.util.Console;

/**
 * An adapter class subclassed by the VLH Agent and Proxy implementations.
 */
class LogService {

	private PrintStream logStream;
	
	/** Call back from connection with an object received. */
	LogService(PrintStream logStream) {
		this.logStream = logStream;
	}

	/** Reports a message within the client execution space. */
	void report(String message, Object obj) {
		Console.report(logStream, message, obj);
	}

	/** Reports an error within the client execution space. */
	void error(String message, Object obj, Throwable e) {
		Console.error(message, obj, e);
	}
}



