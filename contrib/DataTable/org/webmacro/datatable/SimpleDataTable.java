package org.webmacro.datatable;
import java.util.*;

/**
 * a simple implementation of the DataTable interface for managing
 * a list of SimpleTableRow objects.
 * 
 * @author Keats Kirsch
 * @version 1.0
 * @since 11-Sep-00
 * @see SimpleTableRow
 */
public class SimpleDataTable extends ArrayList implements DataTable {
  private String[] _colNames = null;
  private boolean _caseSensitive = false;
  //private Object[] _rowColors = null;
  
  /** HashMap that can hold custom attributes for table rows */
  private HashMap _rowAttribs = new HashMap(5);
  
  public SimpleDataTable(){}
  public SimpleDataTable(String[] colNames){ _colNames = colNames; }
  public String[] getColumnNames(){ return (String[])_colNames.clone(); }
  public int getColumnCount(){ 
    return (_colNames == null) ? -1 : _colNames.length; 
  }
  /** NOTE: 1-based index */
  public String getColumnName(int i){ return _colNames[i-1]; }
  public void setColumnNames(String[] colNames){ 
    if (_caseSensitive){
      _colNames = colNames; 
    } else {
      _colNames = new String[colNames.length];
      for (int i=0; i<colNames.length; i++){
        _colNames[i] = colNames[i].toUpperCase();
      }
    }
  }
  public boolean isCaseSensitive(){ return _caseSensitive; }
  public void setCaseSensitive(boolean cs){ _caseSensitive = cs; }
  public void addRow(Object[] vals){ add(new SimpleTableRow(this, vals)); }
  public void addRow(List vals){ addRow(vals.toArray()); }
  public int getSize(){ return size(); }
  public int getStartIndex(){ return 1; }
  public String formatError(String errmsg){ 
    return "ERROR <!-- " + errmsg + " -->"; 
  }
  /** assign a list of background colors to associate with table rows
    * in a repeating sequence
    */
  public void setRowColors(Object[] colors){ 
    _rowAttribs.put("bgcolor", colors);
  }
  /** convenience modulus method for templates */
  public int modulus(int i, int base){ return i % base; }
  /** get the color associated with the specified row */
  public Object getRowColor(int rowNum){
    Object[] colors = (Object[])getRowAttributes().get("bgcolor");
    if (colors == null) return null;
    int colorIndex = (rowNum - 1) % colors.length;
    return colors[colorIndex];
  }
  /** get a hash map of row attributes */
  public Map getRowAttributes(){ return _rowAttribs; }    
  /** add a named list of values to be associated with rows 
    * in a repeating sequence
    */
  public Object[] putRowAttribute(String attrib, Object[] vals){
    Object[] oldVals = (Object[])_rowAttribs.get(attrib);
    _rowAttribs.put(attrib, vals);
    return oldVals;
  }  
}

