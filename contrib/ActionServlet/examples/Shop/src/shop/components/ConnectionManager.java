package shop.components;

import java.sql.*;
import org.webmacro.Log;
import org.webmacro.as.ActionServlet;
import com.bitmechanic.sql.ConnectionPoolManager;

/**
 * Manages JDBC connections.
 *
 * Uses <A HREF="http://sourceforge.net/projects/hsqldb">Hypersonic SQL</A>
 * (but should be able to use any JDBC driver) and connection pooling technique
 * provided by <A HREF="http://www.bitmechanic.com/projects/jdbcpool">jdbcpool<A>.
 *
 * @author Petr Toman
 */
public class ConnectionManager {
    private Log log;
    private String directory;
    private ConnectionPoolManager pool;

    public ConnectionManager(ActionServlet as) throws Exception {
        log = as.getLog("shop");

        try {
            pool = new ConnectionPoolManager(120);
            pool.addAlias("shop",
                          "org.hsqldb.jdbcDriver",
                          "jdbc:hsqldb:"+as.getProperty("db.directory")+"/shop",
                          "SA",
                          "",
                          50, 120, 120);
        } catch (Exception e) {
            log.error("JDBC initialization error", e);
            throw e;
        }
    }

    /**
     * Returns a connection to database.
     */
    public Connection getConnection() throws SQLException {
        Connection con = DriverManager.getConnection(ConnectionPoolManager.URL_PREFIX + "shop", null, null);
        con.setAutoCommit(false);

        // setting the transaction isolation level does not work with 
        // hsqldb -> database transactions are not safe
        con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

        return con;
    }
}
