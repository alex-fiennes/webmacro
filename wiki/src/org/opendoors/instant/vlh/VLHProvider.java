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
import java.util.*;
import org.opendoors.util.SysEnv;
import org.opendoors.intf.QueueListener;
import org.opendoors.instant.vlh.client.*;

/**
 * Provides convenience methods to create partitions and to 
 * gain access to a vlh either as a Hashtable or as a Store, locally or on a remote server.
 * <p>
 * The properties for gaining access to a vlh are as
 * follows.
 * <ul>
 * <li>ProxyImplementation</li>
 * <li>Server</li>
 * <li>Port</li>
 * <li>PartitionKey</li>
 * <li>ImmutableCertificate</li>
 * <li>MutableCertificate</li>
 * </ul>
 * To create a partition, supply the above properties
 * as well as the following.
 * <ul>
 * <li>Creator</li>
 * </ul>
 * Be sure the properties match case as above.
 * <p>
 * Finally, there is a method providing
 * a Properties object which represents the
 * factory defaults for the VLH. This file is provided
 * with VLH and should be in the jar file or
 * classpath. VLHDefaults.properties is the name.
 * <p>
 * Provider is a singleton.
 * 
 * @author Lane Sharman, Eric Ridge
 */
public class VLHProvider {

	private static VLHProvider instance;
	
	private VLHProvider() {}
	
	public static VLHProvider getInstance() {
		if (instance == null)
			instance = new VLHProvider();
		return instance;
	}
	
	/**
	 * Gets the default properties for connecting
	 * to a vlh server with all fields normally set.
	 * <p>
	 * Change the file in the distribution, 
	 */
	public Properties getClientProperties() throws Exception {
		Properties p = new Properties();
		p.load( SysEnv.getResourceStream("/VLHDefaults.properties") );
		return p;
	}
	
   public VLHProxy createVLHProxy (String classname) throws Exception
   {
      return (VLHProxy) Class.forName (classname).newInstance ();  
   }
   
   
   public Store getAgentAsStore (Properties properties, QueueListener exceptionHandler) throws Exception
   {
      String classname     = properties.getProperty ("ProxyImplementation");
		String server        = properties.getProperty ("Server");
      String port          = properties.getProperty ("Port");
		String partition     = properties.getProperty ("PartitionKey");
		String immut         = properties.getProperty ("ImmutableCertificate");
		String mut           = properties.getProperty ("MutableCertificate");

      VLHProxy proxy = (VLHProxy) Class.forName (classname).newInstance();
      proxy.initConnection(server, (port == null) ? -1 : Integer.parseInt (port));
		proxy.setExceptionHandler(exceptionHandler);
		return proxy.getAgentAsStore(partition, immut, mut);
   }
   
   public Hashtable getAgentAsHashtable (Properties properties, QueueListener exceptionHandler) throws Exception
   {
      String classname     = properties.getProperty ("ProxyImplementation");
		String server        = properties.getProperty ("Server");
      String port          = properties.getProperty ("Port");
		String partition     = properties.getProperty ("PartitionKey");
		String immut         = properties.getProperty ("ImmutableCertificate");
		String mut           = properties.getProperty ("MutableCertificate");

      VLHProxy proxy = (VLHProxy) Class.forName (classname).newInstance();
      proxy.initConnection(server, (port == null) ? -1 : Integer.parseInt (port));
		proxy.setExceptionHandler(exceptionHandler);
		return proxy.getAgentAsHashtable (partition, immut, mut);
   }

	public void createPartition(Properties properties) throws Exception 
   {
      String classname     = properties.getProperty ("ProxyImplementation");
		String server        = properties.getProperty ("Server");
      String port          = properties.getProperty ("Port");
		String partition     = properties.getProperty ("PartitionKey");
		String immut         = properties.getProperty ("ImmutableCertificate");
		String mut           = properties.getProperty ("MutableCertificate");
      String creator       = properties.getProperty ("Creator");

      VLHProxy proxy = (VLHProxy) Class.forName (classname).newInstance();
      proxy.initConnection(server, (port == null) ? -1 : Integer.parseInt (port));
		proxy.createPartitionKey(partition, creator, immut, mut);
		proxy.close();
	}

	public void removePartition(Properties properties) throws Exception 
   {
      String classname     = properties.getProperty ("ProxyImplementation");
		String server        = properties.getProperty ("Server");
      String port          = properties.getProperty ("Port");
		String partition     = properties.getProperty ("PartitionKey");
		String immut         = properties.getProperty ("ImmutableCertificate");
		String mut           = properties.getProperty ("MutableCertificate");
      String creator       = properties.getProperty ("Creator");
      
      VLHProxy proxy = (VLHProxy) Class.forName (classname).newInstance();
      proxy.initConnection(server, (port == null) ? -1 : Integer.parseInt (port));
		proxy.removePartitionKey(partition, creator, immut, mut);
		proxy.close();
	}   

//	/**
//	 * Gets the default implementation of the VLHProxy interface.
//	 */
//	public VLHProxy createVLHProxy() {
//		return new ProxyImpl();
//	}
//	
//	/**
//	 * Gets the default implementation of the VLHProxy interface.
//	 */
//	public VLHLocalProxy createLocalVLHProxy() {
//		return new LocalProxyImpl();
//	}
//	
//	public Store getLocalAgentAsStore(Properties properties,
//								 QueueListener exceptionHandler) throws Exception {
//		LocalProxyImpl proxy = new LocalProxyImpl();
//		String rootDirectory = properties.getProperty("LocalStorageDirectory");
//		String partition = properties.getProperty("PartitionKey");
//		String immut = properties.getProperty("ImmutableCertificate");
//		String mut = properties.getProperty("MutableCertificate");
//		proxy.initConnection(rootDirectory);
//		proxy.setExceptionHandler(exceptionHandler);
//		return proxy.getAgentAsStore(partition, immut, mut);
//
//	}
//   
//   
//	public void createPartition(Properties properties) throws Exception {
//		String server = properties.getProperty("Server");
//		int port = Integer.parseInt(properties.getProperty("Port"));
//		String partition = properties.getProperty("PartitionKey");
//		String immut = properties.getProperty("ImmutableCertificate");
//		String mut = properties.getProperty("MutableCertificate");
//		String creator = properties.getProperty("Creator");
//		VLHProxy proxy = (VLHProxy) new ProxyImpl();
//		proxy.initConnection(server, port);
//		proxy.createPartitionKey(partition, creator, immut, mut);
//		proxy.close();
//	}
//
//	public void removePartition(Properties properties) throws Exception {
//		String server = properties.getProperty("Server");
//		int port = Integer.parseInt(properties.getProperty("Port"));
//		String partition = properties.getProperty("PartitionKey");
//		String immut = properties.getProperty("ImmutableCertificate");
//		String mut = properties.getProperty("MutableCertificate");
//		String creator = properties.getProperty("Creator");
//		VLHProxy proxy = (VLHProxy) new ProxyImpl();
//		proxy.initConnection(server, port);
//		proxy.removePartitionKey(partition, creator, immut, mut);
//		proxy.close();
//	}
//
//   public Store getAgentAsStore(Properties properties,
//								 QueueListener exceptionHandler) throws Exception {
//		return delegate(properties, exceptionHandler);
//	}
//	
//	public Hashtable getAgentAsHashtable(Properties properties,
//								QueueListener exceptionHandler) throws Exception {
//		return delegate(properties, exceptionHandler);
//	}
//      
//	public void createLocalPartition(Properties properties) throws Exception {
//		String rootDirectory = properties.getProperty("LocalStorageDirectory");
//		String partition = properties.getProperty("PartitionKey");
//		String immut = properties.getProperty("ImmutableCertificate");
//		String mut = properties.getProperty("MutableCertificate");
//		String creator = properties.getProperty("Creator");
//		VLHLocalProxy proxy = (VLHLocalProxy) new ProxyImpl();
//		proxy.initConnection(rootDirectory);
//		proxy.createPartitionKey(partition, creator, immut, mut);
//		proxy.close();
//	}
//
//	public void removeLocalPartition(Properties properties) throws Exception {
//		String rootDirectory = properties.getProperty("LocalStorageDirectory");
//		String partition = properties.getProperty("PartitionKey");
//		String immut = properties.getProperty("ImmutableCertificate");
//		String mut = properties.getProperty("MutableCertificate");
//		String creator = properties.getProperty("Creator");
//		VLHLocalProxy proxy = (VLHLocalProxy) new ProxyImpl();
//		proxy.initConnection(rootDirectory);
//		proxy.removePartitionKey(partition, creator, immut, mut);
//		proxy.close();
//	}
//	
//		
//	private AgentImpl delegate (Properties properties,
//								QueueListener exceptionHandler) throws Exception {
//		ProxyImpl proxy = new ProxyImpl();
//		String server = properties.getProperty("Server");
//		int port = Integer.parseInt(properties.getProperty("Port"));
//		String partition = properties.getProperty("PartitionKey");
//		String immut = properties.getProperty("ImmutableCertificate");
//		String mut = properties.getProperty("MutableCertificate");
//		proxy.initConnection(server, port);
//		proxy.setExceptionHandler(exceptionHandler);
//		return (AgentImpl) proxy.getAgentAsHashtable(partition, immut, mut);
//	}
		
}
