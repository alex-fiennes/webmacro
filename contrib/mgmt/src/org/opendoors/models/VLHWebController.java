/*
* Copyright Open Doors Software. 1996-2001.
* All rights reserved.
*
* Software is provided according to the MPL license.
* Open Doors Software provides this
* software on an as-is basis and makes no representation as
* to fitness for a specific purpose.
*
* Direct all questions and comments to support@opendoors.com
*/
package org.opendoors.models;
import java.util.*;
import java.sql.*;
import javax.servlet.http.*;
import javax.servlet.*;
import org.opendoors.instant.vlh.*;

public class VLHWebController {

  private Hashtable store;
  private boolean initialized = false;
  private static EmailAddressController ec = new EmailAddressController();
  private String storagePath;
    
  public VLHWebController() {}
  
  public static EmailAddressController getEmailAddressController() {
    return ec;
  }
  
  /**
   * Initializes the storage controller, setting the path
   * to be {webPath}/WEB-INF/storage
   * @param The initializing servlet which provides the real path.
   */
  public void init(HttpServlet servlet) throws Exception {
    storagePath = servlet.getServletConfig().
                      getServletContext().getRealPath("/") +
                      "WEB-INF/storage";
    init();
  }
  
  /**
   * Initializes the storage path directly to the vlh root.
   * @param storagePath A valid absolute path, eg, /var/vlh/storage.
   */
  public void init(String storagePath) throws Exception {
    this.storagePath = storagePath;
    init();
  }

  /**
   * Initializes the VLH Controller such that
   * a path to the storage root is made and a partition is created/validated.
   * @param vlhStorageRoot The real path to the vlh storage root.
   */
  protected void init() throws Exception {
    store = VLHProvider.instance.getDefaultStore(storagePath, getPartitionName());
    initialized = true;
  }
    
  public boolean isInitialized() { return initialized; }
  
  /**
   * Subclasses return the specific vlh instance
   */
  protected String getPartitionName() throws Exception {
    throw new IllegalStateException(this + 
    " must implement getPartitionName()");
  }
  
  /**
   * Subclasses process an update.
   */
  protected void updateModel(Hashtable store, 
                  HttpServletRequest req)throws Exception {
    throw new IllegalStateException(this + 
    " must implement updateModel()");
  }
  
  
  /**
   * Process a request to store data from the servlet parameters
   * and the servlet.
   */
  public void updateModel(HttpServletRequest req) 
                                                throws Exception {
    updateModel(store, req);
  }

  /**
   * The public static class Email Address with a default constructor
   * for easy initialization with the current Web Controller.
   */
  public static class EmailAddressController extends VLHWebController {
    private String partitionName = "EmailAddresses";

    /**
     * @return The partition name, by default 
     * EmailAddresses is the partition name. 
     */
    protected String getPartitionName() {
      return partitionName;
    }
    
    /**
     * An external initializer may set the partition name.
     * @param partitionName The name of the vlh partition which
     * must be formed to be an acceptable directory name.
     */
    public void setPartitionName(String partitionName) {
      if (partitionName == null)
        throw new IllegalArgumentException("Invalid partition name");
      this.partitionName = partitionName;
    }

    protected void updateModel(Hashtable store, 
          HttpServletRequest req) throws Exception {
      String key = req.getParameter("EmailAddress.value");
      EmailAddress e = (EmailAddress) store.get(key);
      if (e == null) {
        e = new EmailAddress();
        e.value = key;
        e.recorded = new Timestamp(System.currentTimeMillis());
      }
      e.lastValidated = new Timestamp(System.currentTimeMillis());
      //e.observedHostSource = req.getRemoteHost();
      e.applicationMemo = "Provided By: " + this + " on " + e.lastValidated;
      store.put(e.value, e);
    }
  }
  
}
      
    
    
    
  

