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
public class TableDemo extends WMServlet 
{
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
      SimpleDataTable sdt = null;
      try {
        conn = getConnection();
        sdt = ResultSetDataTable.create(conn, sql, pageSize, skipRows);
      } finally {
        releaseConnection(conn);
      }
      Map rowAttribs = sdt.getRowAttributes();
      rowAttribs.put("bgcolor", new String[]{"#CC4400", "#00CCCC"});
      
      context.put("tbl", sdt);      
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
			String msg = getClass().getName() 
			  + ".start() - SQLException: " + ex.getMessage();
			System.err.println(msg);
			throw new ServletException(msg);
		}
  } // start()
  
   /**
     * This is called when the servlet environment shuts down the servlet 
     * via the shutdown() method. The default implementation does nothing.
     */
  protected void stop() {}  
  
  private void createPool(){
    // initialization params are kept in a Hashtable
    Hashtable args = new Hashtable();

    // the underlying driver -- in this case, the HypersonicSQL driver
    args.put("jdbc.driver", "org.hsql.jdbcDriver");

    // the URL to connect the underlyng driver with the server
    args.put("jdbc.URL", "jdbc:HypersonicSQL:hsql://localhost");

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
  }
  
  static public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(jdbcUrl); //, _jdbcUser, _jdbcPass);
  }
  
  static public void releaseConnection(Connection conn){
    try { conn.close(); } catch (Exception e){}
  }
  
}
