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
import java.sql.*;
import java.util.Properties;
import org.opendoors.osql.ConnectionFactory;
import org.opendoors.util.*;

/**
 * Implements a 2 tier connection factory an Oracle database.
 *   <p>
 *  See the vlh property file for the required properties. The connection factory is implemented
 *  as a concrete implementation allowing others to subclass or to create a different
 *  kind of driver.
 * <p>
 * The semantics for implementing an Oracle 8 connection are universal given the
 * right properties.
 */
public class OracleConnectionFactory extends ConnectionFactory {

    private String tnsName;
    private String schema;
    private String password;
    private String driver;
    private String connectString;

    /**
     * Provides the properties required for the Oracle Connection Factory.
     *  <pre>
     * #
     * # Properties to drive the oracle8 connection pool for vlh
     * # File should be seen only on the server side so as to minimize pw compromising
     * jdbc.oracle.tnsname=opendoors
     * jdbc.oracle.schema=vlh
     * jdbc.oracle.password=anonymous
     * jdbc.oracle.driver=jdbc:oracle:oci8
     * </pre>
     * The above are example properties in a properties instance. 
     */
    public Connection createConnection() throws Exception {
    	Connection connection = DriverManager.getConnection(connectString, schema, password);
        connection.setAutoCommit(false); // every vlh operation is atomic
        System.out.println("OracleConnectionFactory. Produced=" + connection);
        return connection;
    }

    public void initConnectionFactory(Properties properties) {
        super.initConnectionFactory(properties);
        tnsName = properties.getProperty("jdbc.oracle.tnsname");
        schema = properties.getProperty("jdbc.oracle.schema");
        password = properties.getProperty("jdbc.oracle.password");
        driver = properties.getProperty("jdbc.oracle.driver.version");
        try {
			SysEnv.objectFactory(properties.getProperty("jdbc.driver"));
		}
		catch (Exception e) {
			Console.error("Unable to load jdbc driver", this, e);
		}
        connectString = driver + "@" + tnsName;
    }

}
