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
import org.opendoors.util.SysEnv;

/**
 * Implements the vlh keys() enumeration semantic as a pure wrapper to its base class.
 */
public class Keys extends AbstractEnumerator {

	public String toString() {
		if (objectArray == null)
			return ("<Keys>null</>");
		else
			return ("<Key Count>" + objectArray.length + "</>");
	}

	/** Puts the value locally. */
	public void localExecution(String rootDirectory) {
		String dir = rootDirectory + "/" + partition + "/";
		try {
			this.objectArray = SysEnv.dirList(dir);
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e.toString());
		}
	}


}
