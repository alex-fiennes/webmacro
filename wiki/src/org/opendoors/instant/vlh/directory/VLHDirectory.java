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
import org.opendoors.instant.vlh.VLHProvider;
import org.opendoors.directory.AbstractDirectory;

/**
 * All vlh directories derive from
 * this base class which derives from AbstractDirectory
 * <p>
 * The a directory is actually
 * two directories, owners and their attributes.
 */
abstract public class VLHDirectory extends AbstractDirectory {
	
	/**
	 * The VLH provider.
	 */
	private VLHProvider vlhProvider;
	
	/**
	 * Creates a shell AbstractDirectory.
	 */
	public VLHDirectory() {
		vlhProvider = VLHProvider.getInstance();
	}
	
	/**
	 * Connects the directory to its source with the properties provided.
	 */
	public void connect(Properties memberProperties) throws Exception {
		members = vlhProvider.getAgentAsHashtable(memberProperties, null);
		this.directoryProperties = memberProperties;
	}
	
	/**
	 * Creates a new directory if this is a shell and connects.
	 */
	public void createDirectory(Properties memberProperties)
		throws Exception {
		if (members != null)
			throw new IllegalStateException("Owner directory is not null");
		vlhProvider.createPartition(memberProperties);
		connect(memberProperties);
	}
	/**
	 * Destroys the directory and disconnects from the source.
	 */
	public void destroyDirectory() throws Exception {
		vlhProvider.removePartition(directoryProperties);
		this.directoryProperties = null;
	}
}
