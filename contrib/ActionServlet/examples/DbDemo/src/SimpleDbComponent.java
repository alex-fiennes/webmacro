import java.sql.*;
import java.util.Properties;
import java.util.MissingResourceException;
import org.webmacro.datatable.*;
import org.webmacro.as.ActionServlet;
import org.webmacro.as.Destroyed;

/**
 * A simple component that provides access to SQL database via JDBC 
 * using SimpleTextDB and <TT>org.webmacro.datatable</TT> package.
 *
 * Note: connection pooling technique should be used instead of 
 *       reserving a connection - see "Shop" example.
 *
 * @author Petr Toman
 */
public class SimpleDbComponent implements Destroyed {
    private ResultSetDataTable table;
    private String sql;
    private Connection con;
    private String orderBy = "";
    private int pageSize = 5;
    private int skipRows = 0;
    private boolean hasMore;

    /**
     * Loads JDBC driver and reserves a connection.
     */
    public SimpleDbComponent(ActionServlet as, String name) {
        try {
           try {
               pageSize = Integer.parseInt(as.getProperty(name, "page.size"));
           } catch(NumberFormatException e) {
           } catch(MissingResourceException e) {}

           Class.forName("jdbc.SimpleText.SimpleTextDriver");
           Properties prop = new java.util.Properties();
           prop.put("Directory", as.getProperty(name, "db.directory"));
           con = DriverManager.getConnection("jdbc:SimpleText", prop);
        } catch (ClassNotFoundException e) {
           as.getLog("SimpleDbComponent").error("Cannot load JDBC driver", e);
        } catch (SQLException e) {
           as.getLog("SimpleDbComponent").error("SQL Error", e);
        }
    }

    /**
     * Loads data into table.
     */
    private void load() throws SQLException {
        table = ResultSetDataTable.create(con, sql + orderBy, pageSize, skipRows);
    }

    /**
     * Set select statement.
     */
    public void setSelect(String sql) {
        skipRows = 0;
        this.sql = sql;
    }

    /**
     * Sets ordering.
     *
     * @param attribute which the result set is ordered by (null = no ordering)
     */
    public void setOrderBy(String attribute) {
       if (attribute == null) orderBy = "";
           else orderBy = " order by " + attribute;
    }

    /**
     * Scrolls to row of a given number.
     */
    public void gotoRow(int rowNumber) {
        if (rowNumber > 0) {
           skipRows = rowNumber;
        }
    }

    /**
     * Scroll to first page.
     */
    public void first() {
        skipRows = 0;
    }

    /**
     * Scroll to previous page.
     */
    public void prev() {
        skipRows -= pageSize;
        if (skipRows < 0) skipRows = 0;
    }

    /**
     * Scroll to next page.
     */
    public void next() {
        skipRows += pageSize;
    }

    /**
     * Scroll to last page.
     */
    public void last() throws SQLException {
        if (table == null) load();

        while(table.hasMore()) {
            skipRows += pageSize;
            load();
        }
    }

    /**
     * Sets page size.
     */
    public void setPageSize(int pageSize) {
        if (pageSize > 0) this.pageSize = pageSize;
    }

    /**
     * Returns table generated by select.
     */
    public DataTable getTable() throws SQLException {
        if (sql != null) load();

        if (table != null && table.getSize() == 0) {
            first();
            last();
        }

        return table;
    }

    /**
     * Closes connection.
     */
    public void destroy() {
        try {
            if (con != null) con.close();
        } catch (SQLException e) {}
    }
}
