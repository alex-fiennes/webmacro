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
import java.io.Serializable;
import java.util.*;

/**
 * A member is an element in
 * an AbstractDirectory.
 * <p>
 * It must implement these core methods
 * which are really just wrappers for
 * a hashtable. Therefore
 * a concrete member would have a hashtable as
 * its attributes.
 * <p>
 */
public interface Member extends Serializable {
	
	/**
	 * The unique identifier of the user.
	 */
	public String getIdentifier();
	
	/**
	 * The attribute key list of the user.
	 */
	public Enumeration getAttributeKeys();
	
	/**
	 * The attribute value list of the user.
	 */
	public Enumeration getAttributeValues();
	
	/**
	 * Informs if the user has this attribute.
	 */
	public boolean hasAttribute(String attributeName);
	
	/**
	 * Gets the attribute value by name.
	 */
	public Object getAttributeValue(String attributeName);
	
	/**
	 * Gets all the attributes as a hashtable of a member.
	 */
	public Hashtable getAttributes();
	
}
