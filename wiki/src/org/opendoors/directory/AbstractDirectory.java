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
package org.opendoors.directory;
import java.util.*;

/**
 * An abstract directory containing elements implementing the member interface.
 * <p>
 * An element in the directory implements the Member interface.
 */
abstract public class AbstractDirectory {

	/**
	 * The members.
	 */
	protected Hashtable members;
	
	/**
	 * The properties specifying the member directory.
	 */
	protected Properties directoryProperties;
	
	/**
	 * Creates a shell AbstractDirectory.
	 */
	public AbstractDirectory() {
	}
	
	/**
	 * Connects the directory to its source with the properties provided.
	 */
	abstract public void connect(Properties directoryProperties) throws Exception;
	
	/**
	 * Creates a new directory and connects it to its source.
	 */
	abstract public void createDirectory(Properties directoryProperties) throws Exception;
	
	/**
	 * Puts a member in the directory.
	 */
	public void putMember(Member member) {
		members.put(member.getIdentifier(), member);
	}
	
	/**
	 * Gets a member from the directory.
	 */
	public Member getMember(String identifier) {
		return (Member) members.get(identifier);
	}
	
	/**
	 * Removes a member from the directory.
	 */
	public void removeMember(Member member) {
		members.remove(member.getIdentifier());
	}
	
	/**
	 * Is there a member with this key.
	 */
	public boolean isMember(String identifier) {
		return members.containsKey(identifier);
	}
	
	/**
	 * Destroys the directory and disconnects from the source.
	 */
	abstract public void destroyDirectory() throws Exception;

}
