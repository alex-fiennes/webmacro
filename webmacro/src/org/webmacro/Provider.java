
package org.webmacro;
import java.util.Properties;

/**
  * A Provider is an object responsible or loading and managing 
  * instances of a given type. The Provider is used by the Broker 
  * to look up objects on demand. 
  * <p> 
  * By implementing new Provider types and registering them with 
  * the broker via WebMacro.properties you can extend or change
  * WebMacro's behavior.
  */
public interface Provider {

  /**
    * Return an array representing the types this provider serves up
    */
  public String getType();

  /**
    * Initialize this provider based on the specified config.
    */
  public void init(Broker b, Properties config) throws InitException;

  /**
    * Clear any cache this provider may be maintaining
    */
  public void flush();

  /**
    * Close down this provider, freeing any allocated resources.
    */
  public void destroy();

  /**
    * Get the object associated with the specified query
    */
  public Object get(String query) throws NotFoundException; 

}

