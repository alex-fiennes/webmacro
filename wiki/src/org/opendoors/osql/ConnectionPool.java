/*
* Copyright Open Doors Software and Acctiva, 2000.
*
* Software is provided according to the ___ license.
* Open Doors Software and Acctiva provide this
* software on an as-is basis and make no representation as
* to fitness for a specific purpose.
*
* Direct all questions and comments to support@opendoors.com
*/
package org.opendoors.osql;
import org.opendoors.adt.Pool;
import java.sql.Connection;


/**
 * ConnectionPool implements a pooling of connection objects so they can be reused. 
 */
public class ConnectionPool extends Pool {
    /**
     * The factory to produce the connection. 
     */
    protected ConnectionFactory factory;

    protected int initial = 5;
    protected int increment = 5;
    protected boolean first = true;
    

    /**
     * Constructs a connection pool with the connection factory.
     * @param factory The factory which will produce the connections. 
     */
	public ConnectionPool(ConnectionFactory factory) {
    	this.factory = factory;
    }

	protected Object[] createPool() throws Exception {
		int size;
		if (first)
			size = initial;
		else
			size = increment;
		Connection[] newPool = new Connection[size];
		for (int index = 0; index < size; index++)
			newPool[index] = factory.createConnection();
		first = false;
		return newPool;
	}
	
	/**
	 * Return an object to the pool so it can be reused.
	 */
	public void free(Object pooledObject) throws Exception {
		//System.out.println("Freeing connection=" + pooledObject);
		((Connection) pooledObject).commit();
		super.free(pooledObject);
	}

}
