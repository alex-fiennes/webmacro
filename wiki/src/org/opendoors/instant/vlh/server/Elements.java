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


/**
 * Implements the vlh elements() enumeration semantic as a pure wrapper to its base class.
 */
public class Elements extends AbstractEnumerator {

	public String toString() {
		if (objectArray == null)
			return ("<Elements>null</>");
		else
			return ("<Element Count>" + objectArray.length + "</>");
	}


}
