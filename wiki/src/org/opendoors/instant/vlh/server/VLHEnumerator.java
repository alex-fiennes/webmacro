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

import java.util.*;

/**
 * Implements the enumeration pattern for an object array passed in to the constructor.
 */
class VLHEnumerator implements Enumeration {

	int cursor = 0;
	Object[] objectArray;
	
	VLHEnumerator(Object[] objectArray) {
		this.objectArray = objectArray;
	}

    public boolean hasMoreElements() {
    	if (objectArray != null)
			return cursor < objectArray.length;
		else
			return false;
	}

    public Object nextElement() {
	    if (cursor < objectArray.length) {
			return objectArray[cursor++];
		}
		throw new NoSuchElementException("objectArrayEnumerator");
	}
}


