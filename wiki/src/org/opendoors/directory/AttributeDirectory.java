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
import java.util.Hashtable;


/**
 * An attribute directory is
 * a directory of attributes
 * identified by owner and name.
 */
public class AttributeDirectory {

	/**
	 * The attribute directory.
	 */
	protected Hashtable attributeDirectory;
	
	public AttributeDirectory(Hashtable attributeDirectory) {
		this.attributeDirectory = attributeDirectory;
	}
	
	/**
	 * Creates a new directory of attributes for the given
	 * owner. Any existing attributes are completely lost.
	 * @param Owner The owner of the attributes.
	 */
	public void createOwnerAttributeContainer(String owner) {
		attributeDirectory.put(owner, new Hashtable());
	}
	
	/**
	 * Removes the owner from the attribute directory.
	 */
	public void removeOwnerAttributeContainer(String owner) {
		attributeDirectory.remove(owner);
	}
	
	
	/**
	 * Adds/Updates an attribute of a certain name to the attribute
	 * list of the owner. If the name of the attribute exists
	 * the old value is updated.
	 * @param owner The owner of the attribute.
	 * @param name The name of the attribute.
	 * @param value The value of the attribute.
	 */
	public void putAttribute(String owner, String name, Object value) {
		Hashtable attributes = (Hashtable) attributeDirectory.get(owner);
		attributes.put(name, value);
		attributes.put(owner, attributes);
	}

	/**
	 * Removes an attribute of a certain name from the attribute
	 * list of the owner. If the name of the attribute exists
	 * the old value is removed.
	 * @param owner The owner of the attribute.
	 * @param name The name of the attribute.
	 * @param value The value of the attribute.
	 */
	public void removeAttribute(String owner, String name, Object value) {
		Hashtable attributes = (Hashtable) attributeDirectory.get(owner);
		attributes.remove(name);
		attributes.put(owner, attributes);
	}

	/**
	 * Gets the value of an attribute for the owner
	 * @param owner The owner of the attribute.
	 * @param name The name of the attribute.
	 */
	public Object getAttribute(String owner, String name) {
		Hashtable attributes = (Hashtable) attributeDirectory.get(owner);
		return attributes.get(name);
	}
	
}
