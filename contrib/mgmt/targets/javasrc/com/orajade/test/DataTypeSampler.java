/*
 * Open Doors Software Data Model Design and Code Generation Service.
 * Software, materials, and templates for generation service
 * is from Open Doors Software and is protected under US Copyright Law. 
 * Copyright 2000-2001.
 * All rights are reserved by Open Doors Software.
 * Date and Time of Java Class File Generation: Tue Nov 27 09:31:36 PST 2001
 */
package com.orajade.test;
import java.sql.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 * DataTypeSampler is a class which
 * implements sql persistence via the java.sql.SQLData interface.
 * <p>
 * It exposes the data instance members on a public
 * basis for introspection and access purposes. There
 * is no special behavior associated with this class.
 * <p>
 * It is expected that subclasses would wrap this class with specific
 * behaviors on the data values as required.
 * <p>
 * This class is automatically generated. Changes made by hand are not
 * preserved from one generation run to the next.
 * @see org.opendoors.store.ORSchema
 * @see org.opendoors.store.SQLDataDef.tml
 * This file was generated on Tue Nov 27 09:31:36 PST 2001.
 */
public class DataTypeSampler implements SQLData, Serializable {

  
  /** The serialized instance survives type evolution with this value. */
  static final long serialVersionUID = 0L;

  public String textMapping; 
  public String charMapping; 
  public BigDecimal numberMapping; 
  public byte byteMapping; 
  public int integerMapping; 
  public long longMapping; 
  public double doubleMapping; 
  public Timestamp timestampMapping; 
  public Date dateMapping; 
  public Time timeMapping; 
  public Blob blobMapping; 
  public Clob clobMapping; 
  public Memo objectMapping; 
  public Ref refMapping; 
  public Array arrayMapping; 
  public boolean booleanMapping; 


  /** The name of the sql type for the jdbc sql map. */
  public static final String SQL_MAP_NAME = 
              "Def_100.DataTypeSampler";

  /** The public constructor. */
  public DataTypeSampler() {
    onNew();
  }

  // lifecycle methods provided for subclass implementations.
  
  /**
   * onNew is a lifecylce method which the constructor
   * invokes and which subclasses override as needed.
   * <p>
   * This lifecycle method provides subclasses a notification
   * that a new instance of the object has been created.
   */
  protected void onNew() {}
  
  /**
   * beforeWrite is a lifecycle method
   * which subclasses should override as needed.
   * <p>
   * This method is functionally equivalent to a dbms trigger. It is
   * invoked by writeStream before the elements are written to the stream
   * and on to the database. This is therefore the place to test for
   * dependencies, invoke validators.
   * @throws SQLException which aborts the write process.
   */
  protected void beforeWrite() throws SQLException {}
   
  /**
   * afterWrite is a lifecycle method
   * which subclasses should override as needed.
   * <p>
   * The general use for this method is to notify listeners
   * for changes to the storage model as a result of this
   * instance being written to storage.
   */
  protected void afterWrite() {}
   
  /**
   * beforeRead is a lifecycle method
   * which subclasses should override as needed.
   * <p>
   * This method provides a notification that this instance
   * is about to have its values replaced by those in storage.
   * It is at this point that this operation can be blocked if required.
   * @throws SQLException which aborts the read process.
   */
  protected void beforeRead() throws SQLException {}
   
  /**
   * afterRead is a lifecycle method
   * which subclasses should override as needed.
   * <p>
   * The general use for this method is to notify listeners
   * for new instance values which can then be cached if required.
   */
  protected void afterRead() {}
  
  // primary key support 
  // optionally generated if the design provided for a primary key

  /** Returns the object identifier. */
  public String getOID() {
    return textMapping;
  }
  
  /** Sets the object identifier. */
  public void setOID(String oid) {
    textMapping = oid;
  }

  // if there is a priamry key or a not null constraint 
  // specified by the sql designer, this method will tell
  // the runtime java client which fields are in viloation of this
  // design constraint.

  
  /**
   * Tests for a constraint violation: a non-null entry being null.
   * <p>
   * If a value cannot be null and it is null, this method
   * will return a list of those members which are null
   * and which should not be null.
   * @return Null if there are no constraint violations or
   * a list of those entries which are null and should not be.
   */
  public List getNullConstraintViolations() {
    List list = new ArrayList();
    if (charMapping == null) list.add("charMapping");
    if (list.size() > 0) return list;
    else return null;
  }
  

   
  // the SQL Data interface.

  /**
   * Gets the map name used in the Oracle database.
   * @see java.sql.SQLData
   * @return The map name found in the sql schema which by convention
   * is the name of the class. 
   */
  public String getSQLTypeName() throws SQLException {
    return SQL_MAP_NAME;
  }


  /**
   * Writes the members in proper order to the sql stream.
   */
  public void writeSQL(SQLOutput stream) throws SQLException {
    beforeWrite();
    stream.writeString(textMapping);
    stream.writeString(charMapping);
    stream.writeBigDecimal(numberMapping);
    stream.writeByte(byteMapping);
    stream.writeInt(integerMapping);
    stream.writeLong(longMapping);
    stream.writeDouble(doubleMapping);
    stream.writeTimestamp(timestampMapping);
    stream.writeDate(dateMapping);
    stream.writeTime(timeMapping);
    stream.writeBlob(blobMapping);
    stream.writeClob(clobMapping);
    stream.writeObject(objectMapping);
    stream.writeRef(refMapping);
    stream.writeArray(arrayMapping);
    stream.writeBoolean(booleanMapping);
    afterWrite();
  }


  /**
   * Reads the sql stream in proper order into the members.
   */
  public void readSQL(SQLInput stream, String sqlTypeName) throws SQLException {
    beforeRead();
    textMapping = stream.readString();
    charMapping = stream.readString();
    numberMapping = stream.readBigDecimal();
    byteMapping = stream.readByte();
    integerMapping = stream.readInt();
    longMapping = stream.readLong();
    doubleMapping = stream.readDouble();
    timestampMapping = stream.readTimestamp();
    dateMapping = stream.readDate();
    timeMapping = stream.readTime();
    blobMapping = stream.readBlob();
    clobMapping = stream.readClob();
    objectMapping = (Memo) stream.readObject();
    refMapping = stream.readRef();
    arrayMapping = stream.readArray();
    booleanMapping = stream.readBoolean();
    afterRead();
  }

}


