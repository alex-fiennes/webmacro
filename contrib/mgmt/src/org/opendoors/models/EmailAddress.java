/*
 * OraJade. Copyright 2001. All Rights Reserved.
 * Developed and licensed by Open Doors Software
 * and right to use subject to end user license agreement.
 * Date and Time of Java Class File Generation: Sat Dec 01 11:14:36 PST 2001
 */
package org.opendoors.models;
import java.sql.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 * EmailAddress is a java data class.
 * <p>
 * Its persistence mechanism is realized 
 * via the java.sql.SQLData interface implemented herein.
 * <p>
 * Subclasses will provide behavior to this class as required
 * and will implement the lifecycle methods.
 * <pre>
 * onNew()
 * beforeRead()
 * afterRead()
 * beforeWrite()
 * afterWrite()
 * </pre>
 * <p>
 * Different subclasses can provide with these methods
 * control over whether the class is immutable, a dependent object,
 * or member of a composition or a part of a collection.
 * <p>
 * This class is automatically generated. Changes made by hand are not
 * preserved from one generation to the next.
 * <p>
 * The class file was generated on Sat Dec 01 11:14:36 PST 2001.
 * <p>
 * <a href=mailto:support@opendoors.com>Support for Orajade</a>
 */
public class EmailAddress implements SQLData, Serializable {

  
  /** The serialized instance survives type evolution with this value. */
  static final long serialVersionUID = 0L;

  public String value; 
  public Timestamp recorded; 
  public Timestamp lastValidated; 
  public String observedHostSource; 
  public String applicationMemo; 


  /** The name of the sql type for the jdbc sql map. */
  public static final String SQL_MAP_NAME = 
              "DEF_100.EMAILADDRESS";

  /** The public constructor. */
  public EmailAddress() {
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
    return value;
  }
  
  /** Sets the object identifier. */
  public void setOID(String oid) {
    value = oid;
  }

  // if there is a priamry key or a not null constraint 
  // specified by the sql designer, this method will tell
  // the runtime java client which fields are in viloation of this
  // design constraint.


   
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
    stream.writeString(value);
    stream.writeTimestamp(recorded);
    stream.writeTimestamp(lastValidated);
    stream.writeString(observedHostSource);
    stream.writeString(applicationMemo);
    afterWrite();
  }


  /**
   * Reads the sql stream in proper order into the members.
   */
  public void readSQL(SQLInput stream, String sqlTypeName) throws SQLException {
    beforeRead();
    value = stream.readString();
    recorded = stream.readTimestamp();
    lastValidated = stream.readTimestamp();
    observedHostSource = stream.readString();
    applicationMemo = stream.readString();
    afterRead();
  }

}


