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
package org.opendoors.instant.vlh;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Defines a simple interface to an abstract, keyed storage area.<p>
 *
 * @author Eric B. Ridge, Lane Sharman
 * @version 1.1
 */
public interface Store
{
   
   /**
    * Put something into the Store.
    * 
    * @param key what are you calling this 'something'?
    * @param value the actual 'something' you want to put.  
    * 
    * @return value of key if it already existed.  <code>null</code> if it didn't exist
    */
   public Object put (String key, Object value);
   
   /**
    * Get something from the Store
    * 
    * @param key what do you want get?
    * @return what you asked for.  <code>null</code> if it doesn't exist.
    */
   public Object get (String key);
   
   /**
    * Removes something from the Store.
    * 
    * @param key what do you want to remove?
    * @return the value of what you removed.  <code>null</code> if it wan't there
    */
   public Object remove (String key);
   
   /**
    * Indicates if an element with the key is contained in the store.
    * @return true if this Store contains the specified key, false otherwise.
    * @param key the key to use.
    */
   public boolean containsKey (String key);
   
   /**
    * An enumertion of all the keys in the store.
    * @return Enumeration Every key in the store as an enumeration.
    */
   public Enumeration keys();
   
   /**
    * The element count of the store.
    * @return Enumeration Every key in the store as an enumeration.
    */
   public int size();
}
