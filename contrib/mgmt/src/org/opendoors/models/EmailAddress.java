/*
 * Open Doors Software Data Model Design and Code Generation Service.
 * Software, materials, and templates for generation service
 * is from Open Doors Software and is protected under US Copyright Law. 
 * Copyright 2000-2001.
 * All rights are reserved by Open Doors Software.
 * Date and Time of Java Class File Generation: Fri Nov 02 07:58:38 PST 2001
 */
package org.opendoors.models;
import java.sql.*;
import java.io.Serializable;

/**
 * EmailAddress is a class which
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
 * This file was generated on Fri Nov 02 07:58:38 PST 2001.
 */
public class EmailAddress implements SQLData, Serializable {
  public String value; 
  public Timestamp recorded; 
  public Timestamp lastValidated; 
  public String observedHostSource; 
  public String applicationMemo; 
  
  /** The name of the sql type for the jdbc sql map. */
  public static final String SQL_MAP_NAME = "Def.EmailAddress";

  /** The public constructor. */
  public EmailAddress() {}
  
  // the SQL Data interface.
  /**
   * Gets the map name found used in the database.
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
    stream.writeString(value);
    stream.writeTimestamp(recorded);
    stream.writeTimestamp(lastValidated);
    stream.writeString(observedHostSource);
    stream.writeString(applicationMemo);
  }
  /**
   * Reads the sql stream in proper order into the members.
   */
  public void readSQL(SQLInput stream, String sqlTypeName) throws SQLException {
    value = stream.readString();
    recorded = stream.readTimestamp();
    lastValidated = stream.readTimestamp();
    observedHostSource = stream.readString();
    applicationMemo = stream.readString();
  }
}
