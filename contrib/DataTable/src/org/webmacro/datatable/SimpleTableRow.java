package org.webmacro.datatable;
import java.util.*;

/**
 * a class for managing a row of values within a table as an ordered Map.
 * 
 * @see DataTable
 * @see SimpleDataTable
 */
public class SimpleTableRow extends AbstractMap 
{
  private FieldSet fields;
  private DataTable _tbl;
  private RowAttribs _attribs;
  
  /**
   * constructor
   * 
   * @param tbl
   * @param vals
   */
  public SimpleTableRow(SimpleDataTable tbl, Object[] vals){
    _tbl = tbl;
    fields = new FieldSet(vals);
    _attribs = new RowAttribs();
  }

  /**
   * Set of column entries in the current row
   */
  public Set entrySet(){ return fields; }

  /**
   * iterator for iterating over columns of the current row.
   */
  public Iterator iterator(){ return entrySet().iterator(); }

  /**
   * retrieves columns from this row by column name.
   * 
   * @param key
   */
  public Object get(Object key){
    Object o = super.get((_tbl.isCaseSensitive()) ? key : key.toString().toUpperCase());
    if ((o == null) && (!fields.contains(key))){
      return _tbl.formatError(
        key + " is not a column of this table!");
    }
    return o;
  }

  /**
   * Allows access to columns within a row by position (1-based).
   * 
   * @param pos
   */
  public Object itemAt(int pos){ 
    if ((pos<1) || (pos>fields.size())){
      return _tbl.formatError("Invalid index: " + pos 
        + ".  The column index must be between 1 and " + fields.size());
    }
    return fields.getEntry(pos-1);
  }
  public int getIndex(){
    return _tbl.indexOf(this) + 1;
  }
  public int getRowNumber(){
    return _tbl.getStartIndex() + _tbl.indexOf(this);
  }
  public Object getColor(){ return getAttribute("bgcolor"); }
  public Object getAttribute(Object attribute){
    Map attribs = _tbl.getRowAttributes();    
    Object[] attribVals = (Object[])attribs.get(attribute);
    if (attribVals == null) return null;
    int i = (getRowNumber() - 1) % attribVals.length;
    return attribVals[i];
  }
  public Object getAttributes(){
    return _attribs;
  }
  
  /**
   * This class allows row attributes to be accessed within a WM template in a straightforward way.  
   * E.g., $row.Attribute.bgColor or $row.Attribute.fontSize.
   */
  // local classes
  public class RowAttribs {
    public Object get(Object key){
      return getAttribute(key);
    }
  }
  
  /**
   * A Set of fields contained in the current row.
   */
  public class FieldSet extends AbstractSet {
    private Object[] _vals = null;
    public FieldSet(Object[] vals){
      _vals = vals;
    }
    public Iterator iterator(){ return new FieldSetIterator(this); }
    public int size(){ return _vals.length; }
    public FieldSetEntry getEntry(int i){
      return new FieldSetEntry(_tbl.getColumnName(i+1), _vals[i]);
    }
    public String toString(){
      StringBuffer sb = new StringBuffer();
      for (int i=0; i<_vals.length; i++){
        sb.append((i==0) ? '[' : ',');
        sb.append(_vals[i]);
      }
      sb.append(']');
      return sb.toString();
    }
  }

  /**
   * Iterator for traversing the FieldSet for the current row.
   */
  public class FieldSetIterator implements Iterator {
    private FieldSet fieldSet = null;
    private int pos = 0;
    public FieldSetIterator(FieldSet fieldSet){
      this.fieldSet = fieldSet;
    }
    public boolean hasNext(){ return pos < fieldSet.size(); }
    public Object next(){
      Object o = null;
      try {
        o = fieldSet.getEntry(pos++); 
      } catch (Exception e){
        e.printStackTrace();
      }
      return o;
    }
    public void remove(){ throw new UnsupportedOperationException(); }
  }

  /**
   * An entry within the current FieldSet.  Encapsulates the field and value properties of a column.
   */
  public class FieldSetEntry implements Map.Entry {
    public FieldSetEntry(Object key, Object val){
      this.key = key;
      this.value = val;
    }
    public Object getKey(){ return key; }
    public Object setValue(Object o){
      Object oldVal = value;
      value = o;
      return oldVal;
    }
    public Object getValue(){ return value; }
    public String toString(){ return value.toString(); }
    private Object key;
    private Object value;
  }
  
} // class SimpleTableRow

