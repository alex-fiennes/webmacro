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
package org.opendoors.intf;
import java.util.Hashtable;

/**
 * AbstractUserFactory is a factory which produces a hashtable of
 * users conforming to a base class.
 */
public interface AbstractFactory {

	/**
	 * Creates a factory or a subtype which
	 * will manage a collection of objects as
	 * a hashtable which are subclasses of the class
	 * name provided.
	 * <p>
	 * @param factoryName The name of the factory created.
	 * @param className The name of the base class of the object
	 * being maintained.
	 * @return AbstractFactory Usually a subclass of this class.
	 */
	public AbstractFactory createFactory(String factoryName, String className) throws
		IllegalArgumentException;

	/**
	 * Returns the objects of the factory as a hashtable.
	 * @return Hashtable The container of all objects managed by the factory.
	 */
	public Hashtable getContainer();

	/**
	 * A lifecycle method. Starts the factory.
	 */
	public void start();

	/**
	 * A lifecyle method. Stops the factory.
	 */
	public void stop();

	/**
	 * Destroys the contents of the factory and the factory.
	 */
	public void destroy(String factoryName);
}