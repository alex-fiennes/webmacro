import java.util.*;
import java.io.*;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import org.webmacro.Template;
import org.webmacro.Log;
import org.webmacro.servlet.*;
import java.sql.*;
import org.webmacro.datatable.*;
import com.protomatter.jdbc.pool.*;
/**
 * @author Keats Kirsch
 * @version 1.0
 * @since 11-Sep-00
 */
public class TableDemo extends WMServlet {
    final static public boolean DEBUG = true;
    private Log _log = null;
    
    final static public String jdbcUrl = "jdbc:protomatter:pool:hyperPool";
    
    public Template handle(WebContext context) throws HandlerException {
        HttpSession sess = null;
        try {
            String tplName = "TableDemo.wm"; //getRequestedTemplateName(context);
            Template t = getTemplate(tplName);
            
            String sql = "SELECT * FROM address"; //FROM coffees";
            String cmd = context.getForm("cmd");
            String curr = context.getForm("curr");
            String pgsz = context.getForm("pageSize");
            int pageSize = 4;
            int skipRows = 0;
            if (curr != null){
                try {
                    skipRows = Integer.parseInt(curr);
                } catch (Exception e){}
            }
            if (pgsz != null){
                try {
                    pageSize = Integer.parseInt(pgsz);
                } catch (Exception e){}
            }
            if ("next".equals(cmd)){
                skipRows += pageSize;
            } else if ("prev".equals(cmd)){
                skipRows -= pageSize;
            } else if ("top".equals(cmd)){
                skipRows = 0;
            }
            Connection conn = null;
            ResultSetDataTable rsdt = null;
            try {
                conn = getConnection();
                rsdt = new ResultSetDataTable(conn, sql, pageSize, skipRows);
                rsdt.registerRowCallback(new DemoRowCallback(conn));
                rsdt.load();
            } finally {
                releaseConnection(conn);
            }
            Map rowAttribs = rsdt.getRowAttributes();
            rowAttribs.put("bgcolor", new String[]{"#CC4400", "#00CCCC"});
            
            context.put("tbl", rsdt);
            return t;
        } catch (Exception e){
            // put the exception and its StackTrace into the context
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            context.put("stackTrace", sw.toString());
            context.put("error", e);
            try {
                return getTemplate("error.wm");
            } catch (Exception e2){
                throw new HandlerException(e2.getMessage());
            }
        }
    }
    
    /**
     * This is called when the servlet environment initializes
     * the servlet for use via the init() method.
     * @exception ServletException to indicate initialization failed
     */
    protected void start() throws ServletException {
        try {
            createPool();
        } catch(Exception e) {
            System.err.print(e.getClass().getName() + ": " + e.getMessage());
        }
        
        try {
            // test the database connection
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            stmt.close();
            releaseConnection(conn);
        } catch(Exception ex) {
            String msg = "TableDemo servlet failed to connect to database. "
            + "\nBe sure the database has been properly started (use the "
            + "runserver script in the hsqldb directory."
            + "\n\nThe system reported: " + ex.getMessage();
            System.err.println(msg);
            ex.printStackTrace(System.err);
            throw new ServletException(msg);
        }
    } // start()
    
    /**
     * This is called when the servlet environment shuts down the servlet
     * via the shutdown() method. The default implementation does nothing.
     */
    protected void stop() {}
    
    private void createPool(){
      /*
    // initialization params are kept in a Hashtable
    Hashtable args = new Hashtable();
       
    // the underlying driver -- in this case, the HypersonicSQL driver
    args.put("jdbc.driver", "org.hsqldb.jdbcDriver");
       
    // the URL to connect the underlyng driver with the server
    args.put("jdbc.URL", "jdbc:hsqldb:hsql:test");
       
    // these are properties that get passed
    // to DriverManager.getConnection(...)
    Properties jdbcProperties = new Properties();
    jdbcProperties.put("user", "sa");
    jdbcProperties.put("password", "");
    args.put("jdbc.properties", jdbcProperties);
       
    // a statement that is guaranteed to work
    // if the connection is working.
    args.put("jdbc.validityCheckStatement", "SELECT 1 FROM ADDRESS");
       
    // If this is specified, a low-priority thread will
    // sit in the background and refresh this pool every
    // N seconds.  In this case, it's refreshed every two minutes.
    args.put("pool.refreshThreadCheckInterval", new Integer(120));
       
    // the initial size of the pool.
    args.put("pool.initialSize", new Integer(5));
       
    // the maximum size the pool can grow to.
    args.put("pool.maxSize", new Integer(10));
       
    // each time the pool grows, it grows by this many connections
    args.put("pool.growBlock", new Integer(2));
       
    // between successive connections, wait this many milliseconds.
    args.put("pool.createWaitTime", new Integer(2000));
       
    // finally create the pool and we're ready to go!
    try {
      JdbcConnectionPool hyperPool
        = new JdbcConnectionPool("hyperPool", args);
    } catch (Exception e){
      throw new RuntimeException(
        "Error creating JdbcConnectionPool.  Nested exception: " + e);
    }
       */
    }
    
    private Connection _conn = null;
    public Connection getConnection() throws Exception {
        //return DriverManager.getConnection(jdbcUrl); //, _jdbcUser, _jdbcPass);
        if (_conn == null)
            Class.forName("org.hsqldb.jdbcDriver");
        _conn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost", "sa", "");
        return _conn;
    }
    
    static public void releaseConnection(Connection conn){
        try { conn.close(); } catch (Exception e){}
    }
    
    private void test(){
        createPool();
        Connection conn = null;
        SimpleDataTable sdt = null;
        try {
            conn = getConnection();
            String sql = "SELECT * FROM address"; //FROM coffees";
            sdt = ResultSetDataTable.create(conn, sql, 4, 0);
            Map rowAttribs = sdt.getRowAttributes();
            rowAttribs.put("bgcolor", new String[]{"#CC4400", "#00CCCC"});
            for (int i=0; i<sdt.size(); i++){
                SimpleTableRow str = (SimpleTableRow)sdt.get(i);
                System.out.print((i+1) + " | ");
                java.util.Iterator iter = str.entrySet().iterator();
                while (iter.hasNext()){
                    Map.Entry entry = (Map.Entry)iter.next();
                    System.out.print(entry.getKey() + "=" + entry.getValue() + " | ");
                }
                System.out.println();
            }
        } catch (Exception e){
            e.printStackTrace(System.err);
        } finally {
            releaseConnection(conn);
        }
    }
    
    public class DemoRowCallback implements org.webmacro.datatable.RowCallback {
        
        /** Creates new DemoRowCallback */
        private Connection _conn;
        private String SQL = "SELECT * FROM document WHERE addressid=";
        
        public DemoRowCallback(Connection conn)
        throws SQLException {
            _conn = conn;
        }
        
        public void processRow(SimpleTableRow strow){
            String id = strow.get("ID").toString();
            try {
                // create a DataTable and attach to this row
                ResultSetDataTable tbl =
                ResultSetDataTable.create(_conn, SQL + id);
                strow.setData(new ChildTable(tbl));
            }
            catch (SQLException sqlE){
                throw new RuntimeException();
            }
        }
        
        public class ChildTable {
            private boolean _expanded = false;
            private ResultSetDataTable _tbl = null;
            
            public ChildTable(ResultSetDataTable tbl){
                _tbl = tbl;
            }
            public ResultSetDataTable getTable(){ return _tbl; }
            public boolean isExpanded(){ return _expanded; }
            public void setExpanded(boolean b){ _expanded = b; }
        }
    }
    
    /** test harness */
    static public void main(String[] args){
        //TODO: create and load table
        TableDemo demo = new TableDemo();
        demo.test();
    }
}
