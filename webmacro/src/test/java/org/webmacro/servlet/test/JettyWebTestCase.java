package org.webmacro.servlet.test;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import net.sourceforge.jwebunit.junit.WebTestCase;

/**
 *
 * Much thanks to 
 * http://today.java.net/pub/a/today/2007/04/12/embedded-integration-testing-of-web-applications.html
 * 
 * Run as an application and try http://localhost:8080/wmtest/index.html
 * or extend for your htmlunit tests.
 * 
 * @author timp
 * @since 2008/03/18
 * 
 */
public abstract class JettyWebTestCase extends WebTestCase {

  private static Server server;
  private static String contextName = "wmtest";
  private static String webRoot ="examples";

  /**
   * Constructor.
   */
  public JettyWebTestCase() {
    super();
  }

  /**
   * Constructor, with name.
   * @param name
   */
  public JettyWebTestCase(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    // Port 0 means "assign arbitrarily port number"
    server = new Server(0);
    startServer();
  
    // getLocalPort returns the port that was actually assigned
    int actualPort = server.getConnectors()[0].getLocalPort();
    getTestContext().setBaseUrl(
        "http://localhost:" + actualPort + "/" );
  }

  private static void startServer() throws Exception {
    WebAppContext wac = new WebAppContext(
        webRoot, "/" + contextName);
    org.mortbay.resource.FileResource.setCheckAliases(false); 
    server.addHandler(wac);
    server.start();
    wac.dumpUrl();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }
  /**
   * If you don't know by now.
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    server = new Server(8080);
    startServer();
  }
  /**
   * Just to say hello.
   */
  public void testIndex() {
    beginAt("/index.html");
    assertTextPresent("Hello World");
  }
  
   /**
   * {@inheritDoc}
   * @see net.sourceforge.jwebunit.junit.WebTestCase#beginAt(java.lang.String)
   */
  public void beginAt(String url) { 
    super.beginAt(contextUrl(url));
  }
  /**
   * {@inheritDoc}
   * @see net.sourceforge.jwebunit.junit.WebTestCase#gotoPage(java.lang.String)
   */
  public void gotoPage(String url) { 
    super.gotoPage(contextUrl(url));
  }
  protected String contextUrl(String url) { 
    return "/" + contextName  + url;
  }
}