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

package org.opendoors.instant.lcis;

/**
 * The ConnectionEventListener interface provides
 * a contract for notification to custom supplied event
 * handlers of connection opening and closing events on the server.
 */
public interface ConnectionEventListener  {

    /**
     * A client connection has been opened.
     */
    public void connectionOpened(Connection openedConnection);

    /**
     * A client connection has been closed.
     */
    public void connectionClosed(Connection closedConnection);

    /**
     * The server has been shut down and any resources being held opened
     * should be immediately closed.
     */
    public void serverShutdown();
    
}
