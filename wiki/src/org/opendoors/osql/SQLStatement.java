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
import java.sql.*;

/**
 * A statement is an executable instance. 
 * <p>
 * It may be executed repeatedly and may mutate over time to reflect the need
 * of the client application.
 * <p>
 * Statements are lightweight with respect to jdbc. They do not keep
 * refs to connections, actual jdbc statements and so forth with hog
 * up a lot of memory and can go bad. 
 * @author Lane Sharman
 * @version 3.0
 * @since 18-July-2000
 */
public class SQLStatement {
    /**
     * Identifies the type of statement publically. 
     */
    public final StatementType type;
    private String statementValue;

    public SQLStatement(StatementType t) {
        this.type = t;
    }

    /**
     * Executes the statement.
     *  <p>
     *  If the statement is a DQL, it will return a TableDataSet.
     *  <p>
     *  If the statement is not a DQL, it will return some other implementation of StatementResult.
     *  <p>
     *  Exceptions are thrown for lots of reasons: instance does not have a statement,
     *  connection provided is defunct, etc.
     */
    public StatementResult execute(java.sql.Connection connection) throws SQLException, Exception, IllegalStateException  {
		if ((connection == null) || (statementValue == null))
			throw new IllegalStateException("Connection or StatementValue null.");

		java.sql.Statement statement = connection.createStatement();

		if (type == StatementType.DQL) {
			ResultSet rset = null;
			// Execute it
    		rset = statement.executeQuery(statementValue);
    		TableDataSet result = new TableDataSet();
    		result.scan(rset);
			rset.close();
			statement.close();
			return result;
		}
		else {
	        statement.executeUpdate(statementValue);
	        statement.close();
	        return null;
		}
    }

    /**
     * Executes the statement as a prepared statement with a parameter providing a call back.
     *  <p>
     *  If the statement is a DQL, it will return a TableDataSet.
     *  <p>
     *  If the statement is not a DQL, it will return some other implementation of StatementResult.
     *  <p>
     *  Exceptions are thrown for lots of reasons: instance does not have a statement,
     *  connection provided is defunct, etc.
     */
    public StatementResult executePreparedStatement(Connection connection, SQLParameterProvider paramProvider) throws SQLException, Exception, IllegalStateException  {
		if ((connection == null) || (statementValue == null))
			throw new IllegalStateException("Connection or StatementValue null.");

		// prepare the statement for parameters.
		PreparedStatement pStatement = connection.prepareStatement(statementValue);
		paramProvider.getSQLParameters(pStatement);
		if (type == StatementType.DQL) {
			ResultSet rset = null;
			// Execute it
    		rset = pStatement.executeQuery();
    		TableDataSet result = new TableDataSet();
    		result.scan(rset);
			rset.close();
			pStatement.close();
			return result;
		}
		else {
			pStatement.executeUpdate();
			pStatement.close();
	        return null;
		}
    }

     /**
     * Sets the value of the statement. Without this the statement cannot
     * execute. 
     */
    public void setStatementValue(String sqlStatement) {
        statementValue = sqlStatement;
    }
}
