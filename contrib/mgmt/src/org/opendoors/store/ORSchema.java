/*
 * Copyright (C) 1998-2000 Open Doors Software.  All Rights Reserved.  
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted under the terms of either of the following
 * Open Source licenses:
 *
 * The GNU General Public License, version 2, or any later version, as
 * published by the Free Software Foundation
 * (http://www.fsf.org/copyleft/gpl.html);
 *
 *  or 
 *
 * The Semiotek Public License (http://webmacro.org/LICENSE.)  
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See www.webmacro.org for more information on the WebMacro project.  
 */

package org.opendoors.store;
import java.util.*;
import java.sql.*;
import java.math.BigDecimal;
import java.io.Serializable;


public class ORSchema  implements Serializable {

  /** The varchar2 mapping to String. */
  public static final MapType TEXT_MAP = 
    new MapType(String.class, Types.VARCHAR, "varchar2", "String", null);

  /** The char mapping to String. */    
  public static final MapType CHAR_MAP = 
    new MapType(String.class, Types.CHAR, "char", "String", null);
    
  /** The number mapping to BigDecimal. */
  public static final MapType NUMBER_MAP = 
    new MapType(BigDecimal.class, Types.NUMERIC, "number", "BigDecimal", null);
    
  /** The number(3,0) mapping to byte. */
  public static final MapType BYTE_MAP = 
    new MapType(Byte.class, Types.TINYINT, "number(3,0)", "byte", null);
    
  /** The number(10,0) mapping to int. */
  public static final MapType INTEGER_MAP = 
    new MapType(Integer.class, Types.INTEGER, "number(10,0)", "int", null);
  
  /** The number(19,0) mapping to long. */    
  public static final MapType LONG_MAP = 
    new MapType(Long.class, Types.BIGINT, "number(19,0)", "long", null);
    
  /** The number mapping to double. */    
  public static final MapType DOUBLE_MAP = 
    new MapType(Double.class, Types.DOUBLE, "number", "double", null);
    
  /** The date mapping to a SQL Timestamp. */    
  public static final MapType TIMESTAMP_MAP = 
    new MapType(java.sql.Timestamp.class, Types.TIMESTAMP, "date", 
      "Timestamp", null);

  /** The date mapping to a SQL Date. */    
  public static final MapType DATE_MAP = 
    new MapType(java.sql.Date.class, Types.DATE, "date", "Date", null);
    
  /** The date mapping to a SQL Time. */    
  public static final MapType TIME_MAP = 
    new MapType(java.sql.Time.class, Types.TIME, "date", "Time", null);

  /** The blob mapping to a sql Blob. */
  public static final MapType BLOB_MAP = 
    new MapType(java.sql.Blob.class, Types.BLOB, "blob", "Blob", null);

  /** The clob mapping to sql Clob. */
  public static final MapType CLOB_MAP = 
    new MapType(java.sql.Clob.class, Types.CLOB, "clob", "Clob", null);

  /** The number(1,0) boolean (not standard) map to boolean. */ 
  public static final MapType BOOLEAN_MAP = 
    new MapType(Boolean.class, Types.BIT, "number(1,0)", "boolean", null);
    
      
  /** The serial UID. */
	static final long serialVersionUID = 0L;

  /** Factory method for an ORSchema.Entry */
  public static ORSchema.Entry getEntry() { return new ORSchema.Entry(); }
  
  /** Factory method for an ORSchema.Record. */
  public static ORSchema.Record getRecord() { return new ORSchema.Record(); }
  
  /** Factory for a non static java object. */
  public static ORSchema.MapType ObjectMapType(String typeName) {
    return new MapType(Object.class, Types.JAVA_OBJECT, typeName, 
      "Object", null);
  }
  
  /** Factory for a non-static ref type. */
  public static ORSchema.MapType RefMapType(String typeName) {
     return new MapType(java.sql.Ref.class, Types.REF, typeName, 
      "Ref", null);
  }
  
  /** Factory for a non-static arraytype. */
  public static ORSchema.MapType ArrayMapType(String typeName, 
                                              String contained) {
     return new MapType(java.sql.Array.class, Types.ARRAY, typeName, 
       "Array", contained);
  }
  
 /**
  * SchemaType is a strongly typed object representing the
  * maps from jdbc to java and from java to jdbc supported
  * by the type mapping.
  */
  public static class MapType implements Serializable {
    
    Class implClass;
    int jdbcType;
    String sqlType;
    String javaType;
    String formalJavaName;
    String containedType;
    
  	/** The serial UID. */
	  static final long serialVersionUID = 0L;

    public MapType(Class implClass, int jdbcType, String sqlType,
                    String javaType, String containedType) {
      this.implClass = implClass;
      this.jdbcType = jdbcType;
      this.sqlType = sqlType;
      this.javaType = javaType;
      this.containedType = containedType;
      // make primitives capped
      this.formalJavaName = javaType.substring(0, 1).toUpperCase() +
                            javaType.substring(1);
    }
    
    /** 
     * Formats a sql declaration properly taking into account if this is
     * the last entry. 
     */
    protected String getSQLDeclaration(Entry entry, Record record) {
      StringBuffer value = new StringBuffer(entry.name + " ");
      String verb = "";
      if (jdbcType == Types.REF) verb = "ref ";
      String schema = "";
      switch (jdbcType) {
        case Types.JAVA_OBJECT:
        case Types.REF:
        case Types.ARRAY:
          schema = record.schemaName + ".";
      }
      value.append(verb).append(schema).append(sqlType);
      if (entry.length > 0) value.append("(" + entry.length + ")");
      // last entry?
      int index = entry.owner.indexOf(entry);
      if (index <  (entry.owner.size() - 1))
        value.append(",");
      return value.toString();
    }
    
    protected String getJavaMemberDeclaration(Entry entry) {
      String type = null;
      if (jdbcType == Types.JAVA_OBJECT)
        type = sqlType;
      else
        type = javaType;
      StringBuffer value = new StringBuffer(type);
      value.append(" ").append(entry.name).append(";");
      return value.toString();
    }
    
    protected String getSQLDataWrite(Entry entry) {
      StringBuffer value = new StringBuffer("stream.write");
      value.append(formalJavaName).append("(").append(entry.name).append(");");
      return value.toString();
    }
    
    protected String getSQLDataRead(Entry entry) {
      StringBuffer value = new StringBuffer(entry.name);
      // provide a stong type cast to the underlying sql object type
      String typeCast = "";
      if (jdbcType == Types.JAVA_OBJECT)
        typeCast = "(" + sqlType + ") ";
      value.append(" = ").append(typeCast).append("stream.read")
        .append(formalJavaName).append("();");
      return value.toString();
    }
    
  }
  
  public static class Entry implements Serializable {
    public MapType type;
    public  String name;
    public int length;
    public boolean primaryKey = false;
    public boolean nullConstrained = false;
    protected Record owner;
  
	  /** The serial UID. */
  	static final long serialVersionUID = 0L;

    public String getSQLDeclaration() {
      return type.getSQLDeclaration(this, owner);
    }
    public String getJavaMemberDeclaration() {
      return type.getJavaMemberDeclaration(this);
    }
    public String getSQLDataWrite() {
      return type.getSQLDataWrite(this);
    }
    public String getSQLDataRead() {
      return type.getSQLDataRead(this);
    }
    public String getSQLType() {
      return type.sqlType;
    }
    public String getContainedType() {
      return type.containedType;
    }
    public boolean isArrayType() {
      return (type.jdbcType == Types.ARRAY);
    }
    public String getJavaType() {
      return type.javaType;
    }
    public void setPrimaryKey(boolean value) {
      if (value) nullConstrained = true;
      primaryKey = value;
    }
    public boolean isPrimaryKey() {return primaryKey;}
    
    public void setNullConstrained(boolean value) {
      nullConstrained = value;
    }
    public boolean isNullConstrained() {return nullConstrained;}
  }
  
  public static class Record extends ArrayList implements Serializable {
    
	  /** The serial UID. */
  	static final long serialVersionUID = 0L;

    private HashSet fieldSet = new HashSet(10);
    String recordName;
    String schemaName;
    
    public boolean isNullConstrained() {
      Iterator i = iterator();
      while (i.hasNext()) {
        ORSchema.Entry se = (ORSchema.Entry) i.next();
        if (se.nullConstrained) return true;
      }
      return false;
    }
    
    public void add(int index, Object element) {
      ORSchema.Entry se = (ORSchema.Entry) element;
      if (fieldSet.contains(se.name))
        throw new IllegalStateException(
                      se.name + " is a duplicate schema name");
      fieldSet.add(se.name);
      se.owner = this;
      super.add(index, element);
    }
    
    public boolean add(Object element) {
      ORSchema.Entry se = (ORSchema.Entry) element;
      if (fieldSet.contains(se.name))
        throw new IllegalStateException(
                      se.name + " is a duplicate schema name");
        fieldSet.add(se.name);
        se.owner = this;
        return super.add(element);
    }
    
    // implement remove semantic properly here...
    
    public void setName(String recordName) {
      this.recordName = recordName;
    }
    
    public String getName() {
      return recordName;
    }
    
    public void setSchemaName(String schemaName) {
      this.schemaName = schemaName;
    }
    
    public String getSchemaName() {
      return schemaName;
    }
    
    public String getDistinctName() {
      return schemaName + "." + recordName;
    }
   
  }                                          
}
