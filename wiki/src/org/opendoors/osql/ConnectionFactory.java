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

package org.opendoors.osql;
import java.sql.Connection;
import java.util.Properties;

/**
 * Provides an abstract base class for producing jdbc connections.
 * <p>
 * Connections are made according to properties and these 
 * properties are specified in the constructor. 
 */
abstract public class ConnectionFactory {

    Properties properties;

    /**
     * @param properties Initializes the properties required by the connection producer.
     */
    public void initConnectionFactory(Properties properties) {
    	this.properties = properties;
	}

    /**
     * Produce a java.sql.Connection according to the properties supplied.
     * @exception Exception If the properties supplied are defective
     * @exception an exception will be thrown. 
     */
    public abstract Connection createConnection() throws Exception;

}
