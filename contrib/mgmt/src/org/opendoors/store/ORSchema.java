/*
 * Copyright (C) 1998-2000 Semiotek Inc.  All Rights Reserved.  
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

public class ORSchema {

  public static final MapType BOOLEAN_MAP = 
      new MapType(Boolean.class, Types.BIT, "number(1)", "boolean");
    
  public static final MapType TEXT_MAP = 
      new MapType(String.class, Types.VARCHAR, "varchar2", "String");
    
  public static final MapType LONG_MAP = 
      new MapType(Long.class, Types.BIGINT, "number", "long");

  /** Factory method for an ORSchema.Entry */
  public static ORSchema.Entry getEntry() { return new ORSchema.Entry(); }
  
  /** Factory method for an ORSchema.Record. */
  public static ORSchema.Record getRecord() { return new ORSchema.Record(); }
  
  /**
  * SchemaType is a strongly typed object representing the
  * maps from jdbc to java and from java to jdbc supported
  * by the type mapping.
  */
  public static class MapType {
    
    private Class implClass;
    private int jdbcType;
    private String sqlType;
    private String javaType;
    private String formalJavaName;
    
    public MapType(Class implClass, int jdbcType, String sqlType,
                    String javaType) {
      this.implClass = implClass;
      this.jdbcType = jdbcType;
      this.sqlType = sqlType;
      this.javaType = javaType;
      // make primitives capped
      this.formalJavaName = javaType.substring(0, 1).toUpperCase() +
                            javaType.substring(1);
    }
    
    /** 
     * Formats a sql declaration properly taking into account if this is
     * the last entry. 
     */
    protected String getSQLDeclaration(Entry entry) {
      StringBuffer value = new StringBuffer(entry.name + " ");
      value.append(sqlType);
      if (entry.length > 0) value.append("(" + entry.length + ")");
      // last entry?
      int index = entry.owner.indexOf(entry);
      if (index <  (entry.owner.size() - 1) )
        value.append(",");
      return value.toString();
    }
    
    protected String getJavaMemberDeclaration(Entry entry) {
      StringBuffer value = new StringBuffer(javaType);
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
      value.append(" = stream.read").append(formalJavaName).append("()");
      return value.toString();
    }
   
  }
  
  public static class Entry {
    public MapType type;
    public  String name;
    public int length;
    protected Record owner;
    
    public String getSQLDeclaration() {
      return type.getSQLDeclaration(this);
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
  }
  
  public static class Record extends ArrayList {
    
    private HashSet fieldSet = new HashSet(10);
    private String recordName;
    private String schemaName;
    
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
    
  }                                          
}
