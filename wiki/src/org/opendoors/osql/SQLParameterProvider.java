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
import java.sql.PreparedStatement;

/**
 * SQL Statements often have paramters which must be provided when
 * a statement is executed.
 * <p>
 * Client applications executing Statement must implement this interface
 * and provide the parameters through the callback when supplying paramters.
 */
public interface SQLParameterProvider {
    /**
     * @param incompleteStatement The incomplete prepared statement needing parameters
     * @return The statement returned.
     * @exception Exception The call back failed. 
     */
    PreparedStatement getSQLParameters(PreparedStatement incompleteStatement) throws Exception ;
}
