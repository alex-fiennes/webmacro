package org.webmacro.datatable;
import java.util.*;
import java.sql.*;

/**
 * an implementation of the DataTable interface for managing JDBC ResultSet data
 * in a way that is convenient for use with WebMacro templates.
 * 
 * @author Keats Kirsch
 * @version 1.0
 * @since 11-Sep-00
 * @see SimpleTableRow
 */
public class ResultSetDataTable extends SimpleDataTable {
  final static public int DEFAULT_MAX_ROWS = 1000;
  private ResultSet _rs = null;
  private int _maxRows = DEFAULT_MAX_ROWS;
  private int _skipRows = 0;
  private boolean _hasMore = false;
  private boolean _hideErrs = true;
  private String _errPrefix = "ERROR";
  private String _nullDisplay = "&nbsp;";
  private String[] _types = null;
  
  public ResultSetDataTable(){}  
  public ResultSetDataTable(ResultSet rs){
    _rs = rs;
  }
  public ResultSetDataTable(ResultSet rs, int maxRows){
    if (maxRows<1) throw new IllegalArgumentException("maxRows argument must be > 0!");
    _rs = rs;
    _maxRows = maxRows;
  }
  public ResultSetDataTable(ResultSet rs, int maxRows, int skipRows){
    if (maxRows<1) throw new IllegalArgumentException("maxRows argument must be > 0!");
    _rs = rs;
    _maxRows = maxRows;
    _skipRows = skipRows;
  }
  public void load() throws SQLException {
    ResultSetMetaData rsmd = _rs.getMetaData();
    int colcnt = rsmd.getColumnCount();
    String[] cols = new String[colcnt];
    _types = new String[colcnt];
    boolean hasColMeta = true;
    try {
      rsmd.getColumnName(1);
    } catch (Exception e){
      hasColMeta = false;
      // if getColumnName doesn't work, use this kludge
      for (int i=1; i<=colcnt; i++){
        cols[i-1] = "COL" + i;
      }
    }
    if (hasColMeta){
      for (int i=1; i<=colcnt; i++){
        cols[i-1] = rsmd.getColumnName(i).toUpperCase();
        _types[i-1] = rsmd.getColumnTypeName(i);
      }
    }
    setColumnNames(cols);
    // skip skipRows rows of result set
    for (int i=0; i<_skipRows && _rs.next(); i++);
    // copy the data from the resultset into this table
    Object o = null;
    java.io.BufferedReader clobReader = null;
    while (size() < _maxRows && _rs.next()){
      Object[] vals = new Object[colcnt];
      for (int i=1; i<=colcnt; i++){
        try {
          o = _rs.getObject(i);
          boolean isNull = (o == null);
          try {
            if (_rs.wasNull()) isNull = true;
          } catch (Exception noWasNullEx){}
          if (isNull){
            o = _nullDisplay;
          } else if (_types[i-1].equalsIgnoreCase("ARRAY")){
            // TODO: handle SQL3 array types
            java.sql.Array arr = (java.sql.Array)o;
            o = arr.getArray();
          } else if (_types[i-1].equalsIgnoreCase("CLOB")){
            Clob clob = (Clob)o;
            clobReader = new java.io.BufferedReader(clob.getCharacterStream());
            String s;
            StringBuffer sb = new StringBuffer();
            while ((s = clobReader.readLine()) != null){
              sb.append(s);
            }
            o = sb.toString();
          }
        } catch (Exception e){
          o = _errPrefix 
            + "<!-- " + e.getClass().getName() + ": " 
            + e.getMessage() + " -->";
        } finally {
          vals[i-1] = o;     
          if (clobReader != null){
            try { clobReader.close(); }
            catch (Exception e){}
          }
        }
      }
      this.addRow(vals);
    }   
    _hasMore = _rs.next();
  }
  
  public void setResultSet(ResultSet rs){ _rs = rs; }
  
  public void setMaxRows(int maxRows){ _maxRows = maxRows; }
  public int getMaxRows(){ return _maxRows; }
  
  public void setHideErrors(boolean hideErrs){ _hideErrs = hideErrs; }
  public void setHideErrors(String hideErrs){
    if ("false".equalsIgnoreCase(hideErrs)){
      _hideErrs = false;
    } else if ("true".equalsIgnoreCase(hideErrs)){ 
      _hideErrs = true; 
    } else {
      throw new IllegalArgumentException(hideErrs 
        + " is invalid.  Must be \"true\" or \"false\".");
    }
  }  
  public boolean isHideErrors(){ return _hideErrs; }
  public boolean getHideErrors(){ return isHideErrors(); }
  
  public void setErrorString(String errPrefix){ _errPrefix = errPrefix; }
  public String getErrorString(){ return _errPrefix; }
  
  public void setSkipRows(int skipRows){ _skipRows = skipRows; }
  public int getSkipRows(){ return _skipRows; }

  public int getStartIndex(){ return _skipRows + 1; }
  
  public boolean hasMore(){ return _hasMore; } 
  
  // convenience methods that encapsulate ResultSet creation
  public static ResultSetDataTable create(
      Connection conn, String sql) throws SQLException
  {
    return create(conn, sql, DEFAULT_MAX_ROWS, 0);
  }
  public static ResultSetDataTable create(
      Connection conn, String sql, int maxRows) throws SQLException
  {
    return create(conn, sql, maxRows, 0);
  }
  public static ResultSetDataTable create(
      Connection conn, String sql, int maxRows, int skipRows) throws SQLException
  {  
    Statement stmt = conn.createStatement();
    ResultSetDataTable rsdt = create(stmt, sql, maxRows, skipRows);
    stmt.close();
    return rsdt;
  }
  public static ResultSetDataTable create(
      Statement stmt, String sql) throws SQLException
  {  
    return create(stmt, sql, DEFAULT_MAX_ROWS, 0);
  }
  public static ResultSetDataTable create(
      Statement stmt, String sql, int maxRows) throws SQLException
  {  
    return create(stmt, sql, maxRows, 0);
  }
  public static ResultSetDataTable create(
      Statement stmt, String sql, int maxRows, int skipRows) throws SQLException
  {  
    ResultSet rs = stmt.executeQuery(sql);
    ResultSetDataTable rsdt = new ResultSetDataTable(rs, maxRows, skipRows);
    rsdt.load();
    rs.close();
    return rsdt;
  }
  public String getColumnTypeName(int i){ return this._types[i]; }

  public String formatError(String errmsg){ 
    StringBuffer sb = new StringBuffer(_errPrefix);
    if (_hideErrs) sb.append("<!-- ");
    sb.append(errmsg);
    if (_hideErrs) sb.append(" -->");
    return sb.toString(); 
  }
}

