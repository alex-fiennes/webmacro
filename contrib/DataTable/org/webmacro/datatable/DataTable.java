package org.webmacro.datatable;
import java.util.*;

/**
 * An interface that adds methods to the List interface for managing tabular data.
 * 
 * @author Keats Kirsch
 * @version 1.0
 * @since 11-Sep-00
 */
public interface DataTable extends java.util.List {

  /**
   * 
   * @return a String array of column names
   */
  public String[] getColumnNames();

  /**
   * 
   * @return the number of columns in this table
   */
  public int getColumnCount();

  /**
   * 
   * @return the name of column i (one-based index)
   */
  public String getColumnName(int i);
  
  /**
   * add a row of values to the table
   * 
   * @param vals an array of values that constitute a new row in the table.
   */
  public void addRow(Object[] vals);

  /**
   * add a row of values to the table
   * 
   * @param vals a List of values that constitute a new row in the table.
   */
  public void addRow(List vals); 
  
  /**
   * formats an error message for display in generated page.
   * 
   * @param errmsg The message to be formatted
   * @return The formatted message
   */
  public String formatError(String errmsg);
  
  /**
   * @return The index of the first row included in this table.
   */
  public int getStartIndex();
  
  /**
   * @return The number of rows in this table.  It is the same as size() but
   * it uses the Java bean accessor style so that it can be accessed 
   * as $tbl.Size in a WebMacro template.
   */
  public int getSize();
  
  /**
   * This method gives access to a list of custom row attributes for a DataTable.
   * A row attribute is a named list of values that are accessable by rows of
   * the table.  A single value is associated with each row in order, 
   * repeating when necessary.  I.e., if there are N values in the list, 
   * the attribute associated with row i would be indexed by (i-1) modulus N.
   * <br><br>
   * For example, to associate a list of background colors with a DataTable
   * referenced by tbl:<br><br>
   * <code>
   * String[] colors = new String[]{ "red", "green", "blue" };<br>
   * tbl.getAttributes().put("bgcolor", colors);<br>
   * </code>
   * <br>In a WebMacro template you could access the property thusly:<br><br>
   * <code>
   * &lt;table&gt;<br>
   * #foreach $row in $table {<br>
   *   &lt;tr bgcolor="$row.Attribute.bgcolor"&gt;<br>
   *     &lt;td&gt;$row.ColumnA&lt;/td&gt;&lt;td&gt;$row.ColumnB&lt;/td&gt;&lt;/tr&gt;<br>
   * }<br>
   * &lt;/table&gt;<br>
   * </code>
   * 
   * @return a List of custom row attributes.
   */
  
  public Map getRowAttributes();
  
  /*
   * indicates if column names are to be case sensitive.
   */
  public boolean isCaseSensitive();
}

