/*
* Copyright Open Doors Software and Acctiva, 1996-2000.
*
* Software is provided according to the ___ license.
* Open Doors Software and Acctiva provide this
* software on an as-is basis and make no representation as
* to fitness for a specific purpose.
*
* Direct all questions and comments to support@opendoors.com
*/

package org.opendoors.instant.vlh.directory;
import java.util.*;
import org.opendoors.instant.vlh.*;
import org.opendoors.intf.*;


/**
 * Manages a directory of directories.
 * <p>
 * A directory is a namespace of
 * objects usually but not always of a similar class type.
 * <p>
 * The directory factory finds its root according
 * to a set of properties provided to init().
 * <p>
 * It is therefore possible to have many Directory
 * Factories in the same vm space.
 */
public class DirectoryFactory implements QueueListener {

	/**
	 * The directory of directories.
	 */
	protected Hashtable rootDirectory;
	
	/**
	 * The properties of the root.
	 */
	protected Properties rootProperties;

	public DirectoryFactory(){}
	
	/**
	 * The factory is initialized according to properties
	 * to find the root directory.
	 */
	public void init(Properties properties) throws Exception {
		rootDirectory = VLHProvider.getInstance().getAgentAsHashtable(properties, this);
		if (rootDirectory == null)
			throw new NullPointerException("No root directory found with properties supplied=" + properties);
		rootProperties = properties;
	}
	
	/**
	 * Creates a directory with the name provided
	 * and a set of properties within the root.
	 * Returns the vlh hashtable reference.
	 * @param name The name of this dorectory.
	 * @param properties The propreties of this directory.
	 * 
	 */
	public Hashtable createDirectory(String name, Properties properties) throws IllegalArgumentException, Exception {
		if (rootDirectory.containsKey(name))
			throw new IllegalArgumentException("Name=" + name + " taken");
		rootDirectory.put(name, properties);
		return openDirectory(name);
	}
	
	/**
	 * Opens a directory within the root directory.
	 */
	public Hashtable openDirectory(String name) throws Exception  {
		Properties properties = (Properties) rootDirectory.get(name);
		return VLHProvider.getInstance().getAgentAsHashtable(properties, this);
	}
	
	/**
	 * Removes the root directory.
	 */
	public void removeDirectoryRoot()throws Exception {
		if (rootDirectory == null)
			throw new IllegalStateException("No root to remove");
		VLHProvider.getInstance().removePartition(rootProperties);
		rootDirectory = null;
		rootProperties = null;
	}
	
	/**
	 * Creates a directory root according to the properties provided.
	 */
	public void createDirectoryRoot(Properties properties) throws Exception {
		VLHProvider.getInstance().createPartition(properties);
		init(properties);
	}

	public void readQueue(Object exception) {
		System.err.println("Directory Error=" + exception);
	}

}
