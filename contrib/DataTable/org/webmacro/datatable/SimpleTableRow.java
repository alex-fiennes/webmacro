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
  
  public SimpleTableRow(SimpleDataTable tbl, Object[] vals){
    _tbl = tbl;
    fields = new FieldSet(vals);
    _attribs = new RowAttribs();
  }
  public Set entrySet(){ return fields; }
  public Iterator iterator(){ return entrySet().iterator(); }
  public Object get(int i){
    if ((i<1) || (i>fields.size())){
      return _tbl.formatError("Invalid index: " + i 
        + ".  The column index must be between 1 and " + fields.size());
    }
    return fields.getEntry(i-1);
  }
  public Object get(Object key){
    Object o = super.get(key);
    if ((o == null) && (!fields.contains(key))){
      return _tbl.formatError(
        key + " is not a column of this table!");
    }
    return o;
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
  
  // local classes
  public class RowAttribs {
    public Object get(Object key){
      return getAttribute(key);
    }
  }
  
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

