/*
 * @(#)SysEnv.java	1.1 97/09/01
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
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.beans.*;
import java.util.*;
import java.applet.*;


/**
 * A static collection of frequently used system, file, and networking routines.
 * <p>
 * Contains some important static variables as well.
 */
public class SysEnv {

	/**
	 * The line separator.
	 */
	public static String lineSeparator = System.getProperty("line.separator");

	/**
	 * Applications should set this to be their data directory, not the current directory.
	 */
	public static String dataDirectory = "./";

	private SysEnv() {}

	/**
	 * Opens a url on a remote server and saves the contents to a local directory
	 * and file name as specified.
	 * @param url The url eg, http://hostname:6001/distribution.jar
	 * @param localFileName the name of the output file, eg, Env.home + ../../../ + ditribiution.jar
	 * @throws Exception For example, the remote host is unreachable.
	 */
	public static void copyRemoteURL(String url, String localFileName) throws Exception {
		// create the input stream:
		InputStream in = (new URL(url)).openStream();
		FileOutputStream out = new FileOutputStream(localFileName);
		// read in with a good size buffer
		int bufSize = 4096;
		byte[] buf = new byte[bufSize];
		int bytesRead = in.read(buf);
		while (bytesRead != -1) {
			out.write(buf, 0, bytesRead);
			bytesRead = in.read(buf);
		}
		out.flush();
		out.close();
		in.close();
	}

	/**
	 * Returns a remote url as a java object.
	 */
	public static Object getRemoteObject(String url) throws Exception {
		// create the input stream:
   	    ObjectInputStream ois = new ObjectInputStream((new URL(url)).openStream());
        Object o = ois.readObject();
       	ois.close();
		return o;
	}

	/**
	 * Returns an array of file names or null if the directory is empty or
	 * the directory ref is invalid.
	 * <b>Note<b>: Only files contained in the directory are returned.
	 */
	public static String[] dirList(String directoryRef) throws Exception {
		File dir = new File(directoryRef);
		if (! dir.isDirectory())
			throw (new Exception(directoryRef + " not a directory"));
		String[] entries = dir.list();
		Vector files = new Vector();
		int count = entries.length;
		for (int index = 0; index < count; index++) {
			File f = new File(entries[index]);
			if (! f.isDirectory())
				files.addElement(entries[index]);
		}
		entries = new String[files.size()];
		files.copyInto(entries);
		return entries;
	}

	/**
	 * Returns the count of entries in a directory which are files.
	 */
	public static int dirListSize(String directoryRef) throws Exception {
		return dirList(directoryRef).length;
	}

	/**
	 * Gets an input stream from the classpath first
	 * and, if not sucessful, then from the file system.
	 * <p>
	 * Returns null if the stream does not exist.
	 */
	public static InputStream getResourceStream(String file) throws Exception {
		Class c = SysEnv.class;
		InputStream in = c.getResourceAsStream(file);
		if (null == in) {
			c = Class.class;
			in = c.getResourceAsStream(file);
		}
		if (null == in) { 
			in = new FileInputStream(file); 
		}
		return in;
	}

		

	public static String readInputStreamAsText(InputStream is) throws Exception {
		String s = "";
		while (true) {
			int nextChar = is.read();
			if (nextChar == -1) {
				return s;
			}
			else {
				s = s + (char) nextChar;
			}
		}
	}

	/**
	 * Takes a text file reference, reads it, converts it to a string.
	 * @param The file reference.
	 * @return The contents of the file or null if the file could not be opened.
	 */
	public static String readTextFile(String fileReference) throws Exception {
		String content = null;
		FileInputStream fileStream = new FileInputStream(fileReference);
		content = readInputStreamAsText(fileStream);
		fileStream.close();
		return content;
	}

	/**
	 * Provides the local file reference as an input stream.
	 */
	public static InputStream fileInput(String fileReference) throws Exception {
		return new FileInputStream(fileReference);
	}

	/**
	 * Reads a file and returns it as a byte array.
	 */
	 public static byte[] readInputFile(String fileReference) throws Exception {
	 	InputStream in = fileInput(fileReference);
	 	byte[] value = new byte[in.available()];
	 	in.read(value);
	 	in.close();
	 	return value;
	 }

	/**
	 * Provides the local file reference as an output stream.
	 */
	public static OutputStream fileOutput(String fileReference) throws Exception {
		return new FileOutputStream(fileReference);
	}

	/**
	 * Writes to a file a byte array.
	 */
	public static void writeOutputFile(String fileReference, byte[] value) throws Exception {
		OutputStream out = fileOutput(fileReference);
		out.write(value);
		out.close();
	}

	/**
	 * Provides the local file reference as a print stream.
	 */
	public static PrintStream printOutput(String fileReference) throws Exception {
		return new PrintStream(fileOutput(fileReference));
	}

	/**
	 * Provides the local file as properties object.
	 */
	public static Properties loadPropertiesFromFile(String fileReference) throws Exception {
		Properties properties = new Properties();
		InputStream input = fileInput(fileReference);
		properties.load(input);
		return properties;
	}

	/**
	 * Writes a string to a text file.
	 * @param fileReference The fully qualified file reference.
	 * @param source The string source.
	 * @throws Exception If there is any file error operation.
	 */
	public static void createTextFile(String fileReference, String source) throws Exception {
      	PrintWriter p = new PrintWriter( new FileOutputStream(fileReference) );
      	p.write(source);
      	p.close();
	}

	/**
	 * Makes a directory and any needed intermediate directories as required.
	 * @param dirReference The fully qualified new directory reference.
	 * @throws Exception If there is any file error operation.
	 */
	public static void makeDirectory(String dirReference) throws Exception {
		File dir = new File(dirReference);
		dir.mkdirs();
	}

	/**
	 * Tests if the reference is a directory.
	 * @param dirReference The fully qualified directory reference.
	 * @throws Exception If there is any file error operation.
	 */
	public static boolean isDirectory(String dirReference) throws Exception {
		File dir = new File(dirReference);
		return dir.isDirectory();
	}

	/**
	 * Method to serialize an object in the data directory. Use subdirectory references only if you are sure
	 * that the subdirectory exists or that it has been created.
	 * @SEE thawObject
	 */
	public static void iceObject(String fileReference, Object o) {
		// Serialize the object:
 	   try {
	      	ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(fileReference) );
	      	oos.writeObject(o);
			oos.close();
	    } 
		catch(Exception ex) {
	    	Console.error("Could not serialize object to disk " + dataDirectory + fileReference, null, ex);
    	}
	}

	/**
	 * Method to serialize an object and return it as a byte array.
	 * @SEE thawObject
	 */
	public static byte[] iceObjectAsByteArray(Object o) {
		// Serialize the object:
 	   try {
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream( bStream );
			oos.writeObject(o);
			byte[] byteRepresentation = bStream.toByteArray();
			oos.close();
			return byteRepresentation;
	    } 
		catch(Exception ex) {
	    	Console.error("Could not serialize object to a byte array.", ex, null);
			return null;
    	}
	}

	/**
	 * Method to thaw an object serialized with iceObject in the data directory
	 */
	public static Object thawObject(String fileReference) throws Exception {
		// Instantiate the object from a file
		Object o = null;
   	    ObjectInputStream ois = new ObjectInputStream( new FileInputStream(fileReference) );
        o = ois.readObject();
       	ois.close();
		return o;
	}

	/**
	 * Method to thaw an object serialized with iceObject() which transforms
	 * the object from a string representation.
	 * @param byteRepresentation as a byte array.
 	 * @return The object or null if it could not be deserialized
	 */
	public static Object thawObjectByteArray(byte[] byteRepresentation) {
		// Instantiate the object from a string.
		Object o = null;
	    try {
			ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream(byteRepresentation) );
			o = ois.readObject();
			ois.close();
			return o;
	    } 
		catch(Exception ex) {
		      Console.report("Could not derserialize object as a byte array.", ex);
	  		  return null;
	    }
	}

	/**
	 * Instantiates the object using the class name provided.
	 * <p>
	 * Returns an exception if the object cannot be found or, if found, cannot be instantiated.
	 */
	public static Object objectFactory(String className) throws Exception {
		return  Class.forName(className).newInstance();
	}

	public static boolean dataFilePresent(String fileReference) {
		// Test for file present in dataDirectory
		try {
			File f = new File(dataDirectory, fileReference);
			if (f.exists()) {
				return true;
			}
			else {
				return false;
			}
		}
		catch (Exception e) {
			return false;
		}
	}
	public static boolean filePresent(String fileReference) throws Exception {
		File f = new File(fileReference);
		return f.exists();
	}
	
	public static void removeFile(String fileReference) {
		File f = null;
		try {
			f = new File(fileReference);
			if ( ! f.exists() ) return;
			f.delete(); // nothing to do
		}
		catch (Exception e) {
			Console.error("Unable to remove file " + fileReference, f, e);
			return;
		}
	}				
		

	public static void removeDataFile(String fileReference) {
		File f = null;
		try {
			f = new File(dataDirectory, fileReference);
			if ( ! f.exists() ) return;
			f.delete(); // nothing to do
		}
		catch (Exception e) {
			Console.error("Unable to remove file " + fileReference, f, e);
			return;
		}
	}				

	/** Execs a process and adds it to the list of processes which will be destroyed on shutdown. */
	public static Process exec(String command) throws Exception {
 	   Runtime rt = Runtime.getRuntime();
 	   Process p = rt.exec(command);
	   return p;
	}






}
