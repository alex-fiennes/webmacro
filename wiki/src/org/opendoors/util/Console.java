/*
 * 
 * Copyright (c) 1996, 1997 Open Doors Software, Inc. All Rights Reserved.
 * 
 * Open Doors Software MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. OPEN DOORS SOFTWARE SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 */

package org.opendoors.util;
import java.io.*;

/**
 * This is a static utility class supporting low-level console reporting.
 */
public class Console {

	/**
	 * Enable/Disable status reporting through report().
	 */
	public static boolean reportingEnabled = true;
	

	/**
	 * The current output stream for reports.
	 */
	public static PrintStream outStream = System.out;

	/**
	 * The current output stream for errors.
	 */
	public static PrintStream errStream = System.err;

	/**
	 * Default private constructor because this is a static system-level class
	 */
	private Console() {
	}

	/**
	 * Make a report to the current output stream if reporting enabled.
	 */
	public static void report(String message, Object obj) {
		report(outStream, message, obj);
	}

	/**
	 * Make a report to the current console output stream if reporting enabled.
	 */
	public static void report(PrintStream out, String message, Object obj) {
		if (reportingEnabled) {
			Runtime runtime = Runtime.getRuntime();
			java.util.Date now = new java.util.Date();
			out.println("--------Console Message---------------------------------");
			out.println("Reporting " + " on " + now.toString());
			out.println("Message=" + message);
			out.println("Object=" + obj);
			if (obj != null)
	        	out.println("Class=" + obj.getClass().getName());
			out.println("Free Memory in System=" + runtime.freeMemory() );
			out.println("--------End of Message----------------------------------");
		}
	}
		
	
	/**
	 * Reports an error condition to the current, default output stream.
	 */
	public static void error(String message, Object obj, Throwable e) {
		error(errStream, message, obj, e);
	}

	/**
	 * Reports an error condition to the current, supplied output stream.
	 */
	public static void error(PrintStream err, String message, Object obj, Throwable e) {
		StringBuffer report = new StringBuffer();
		Runtime runtime = Runtime.getRuntime();
		java.util.Date now = new java.util.Date();
		report.append("Message=").append(message).append(SysEnv.lineSeparator);
		if (obj != null) {
			report.append("Underlying Object=").append(obj.toString()).append(SysEnv.lineSeparator);
        	report.append("Class=").append(obj.getClass().getName()).append(SysEnv.lineSeparator);
        }
    	if (e != null) {
			report.append("Reported Exception=").append(e.getMessage()).append(SysEnv.lineSeparator);
			// start the error
			try {
				err.println("__Current Error Report and Stack Trace___");
				e.printStackTrace(err);
		    }
		    catch (Exception ex) {
		    	report.append("Unable to format the stack trace for the exception. Message=").append(ex.getMessage()).append(SysEnv.lineSeparator);
		    }

		}
		else {
			err.println("__Current Error Report. No exception reported___");
		}

		report.append("Free Memory in System=").append(runtime.freeMemory() + "" ).append(SysEnv.lineSeparator);
		report.append("Call stack spooled to console.").append(SysEnv.lineSeparator);

		err.println(report.toString());
		Thread.dumpStack();
		err.println("__End of Error Report and Stack Trace___");

	}

	/**
	 * Gets user input.
	 */
	public static String getInput(String prompt) {
		try {
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			System.out.println(prompt);
   			return stdIn.readLine();
		}
		catch(Exception e) {
			return null;
		}
	}

}

	

