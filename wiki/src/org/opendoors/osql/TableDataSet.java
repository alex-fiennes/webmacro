/*
* Copyright Open Doors Software and Acctiva, 2000.
*
* Software is provided according to the ___ license.
* Open Doors Software and Acctiva provide this
* software on an as-is basis and make no representation as
* to fitness for a specific purpose.
*
* Direct all questions and comments to support@opendoors.com.
*/

package org.opendoors.osql;
import java.io.Serializable;
import java.io.InputStream;
import java.sql.*;
import java.util.Vector;
//import oracle.sql.BLOB;
import org.opendoors.util.Console;

/**
 * Contains the results from the execution of a DQL statement.
 * <p>
 * To preserve access speed the results are provided as public members.
 * <p>
 * A convenience method is provided to transpose the rows and columns.
 * <p>
 * The class may be serialized so as to preserve the data.
 */
public class TableDataSet implements Serializable, StatementResult {

	/**
	 * All of the rows in the data set.
	 * rows[0][*] refers to the column indexes in row 0
	 */
	protected Object[][] rows;

	/**
	 * All of the columns in the data set, if transposed.
	 * columns[0][*] refers to the row indexes in column 0
	 */
	protected Object[][] columns;

	/** Is the data set matrix transposed. */
	protected boolean transposed = false;

	/**
	 * The estimated size of rows normally read in. Default=1000.
	 */
	protected static int averageRowCount = 1000;

	/** The default constructor. */
	public TableDataSet(){}

	/** Gets a specific cell from the array. */
	public Object getCell(int rowIndex, int columnIndex) throws Exception {
		if (rowIndex < rows.length && columnIndex < rows[0].length)
			return rows[rowIndex][columnIndex];
		else
			throw new Exception("Indexes provided are out of range.");
	}

	/**
	 * Scans the array of rows and converts blobs to byte arrays.
	 * <p>
	 * Blob types converted: oracle.sql.BLOB.
	 * <p>
	 * This must be called explictly. The container of values
	 * is not automatically converted.
	 */
	public void blobsToByteArray() throws Exception {
	/**	int rowCount = 0;
		if ((rows == null) || ((rowCount = rows.length) == 0))
			return; // nothing to allocate
		int columnCount = rows[0].length;
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
				if (rows[rowIndex][columnIndex] instanceof oracle.sql.BLOB) {
					oracle.sql.BLOB blob = (oracle.sql.BLOB) rows[rowIndex][columnIndex];
					//System.out.println("Blob Length=" + blob.length()); 
					InputStream in = blob.getBinaryStream();
					//System.out.println("BlobConv. Avail=" + in.available());
					byte[] value = new byte[(int)blob.length()];
					in.read(value);
					rows[rowIndex][columnIndex] = value;
					//System.out.println("BloBConv. Lgth=" + value.length + " Now Avail=" + in.available());
				}
			}
		} **/
	}

	/**
	 * The convenience method which transposes rows into columns.
	 */
	public void transpose() {
		int rowCount = 0;
		if ((rows == null) || ((rowCount = rows.length) == 0))
			return; // nothing to allocate
			
		// transpose the rows:
		int columnCount = rows[0].length;
		this.columns = new Object[columnCount][rowCount];
		// iterate over each column:
		for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
			// iterate over each row, then each column:
			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				// transpose:
				columns[columnIndex][rowIndex] = rows[rowIndex][columnIndex];
			}
		}
	}

	/**
	 * The package level scanner.
	 */
	void scan(ResultSet rset) throws SQLException {
		ResultSetMetaData mset = rset.getMetaData(); // so we know how mant columns
		int columnCount = mset.getColumnCount();

		// We cannot initialize the arrays until we have read into our local vector the entire
		// result set row by row, field by field.
		Vector tmpRow = new Vector(averageRowCount); 

		// Create interim objects to hold the data
		int rowCount = 0; //tracks the number of rows.
 	   	// Iterate through the result and build the temporary structures.
		while (rset.next ()) {
			Object rowdata[] = new Object[columnCount];
			for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
				rowdata[columnIndex-1] = rset.getObject(columnIndex);
			}
			// add the row:
			tmpRow.addElement(rowdata);
			rowCount++;
		}
		rows = new Object[rowCount][columnCount];
		tmpRow.copyInto(rows);
	}

	/**
	 * Returns the rows of data.
	 * <p>
	 * This is a reference to the actual data last returned.
	 * Not a copy.
	 */
	public Object[][] getRowDataSet() {
		return rows;
	}

	/**
	 * Returns the columns of data.
	 * <p>
	 * This is a reference to the transposed values.
	 */
	public Object[][] getColumnDataSet() {
		return columns;
	}

	/**
	 * Returns the count of rows.
	 */
	public int getRowCount() {
		if (rows != null)
			return rows.length;
		else
			return 0;
	}

}
