package test.com.shinyhosting.formhandler;


import junit.framework.*;
import javax.servlet.http.HttpServletRequest;

import com.shinyhosting.formhandler.*;


public class TestHandlerPath extends TestCase
	{
	public TestHandlerPath(String name)
		{
		super(name);
		}

	/**
	 * Sets up the fixture, for example, open a network connection.
	 * This method is called before a test is executed.
	 */
	protected void setUp() throws Exception {
		org.apache.log4j.BasicConfigurator.configure();
	}

	/**
	 * Override to run the test and assert its state.
	 * @exception Throwable if any exception is thrown
	 */
	//protected void runTest() throws Throwable {
	//}

	/**
	 * Tears down the fixture, for example, close a network connection.
	 * This method is called after a test is executed.
	 */
	protected void tearDown() throws Exception {
	}

	public void testURLReturns() throws Exception
	{

		MyHttpRequest request = new MyHttpRequest();

		request._parameters.put(HandlerPath.DEFAULT_HANDLERPATH_NAME, "test.handlerPath.example");
		request._parameters.put(HandlerPath.DEFAULT_INNERHANDLERPATH_NAME, "test.innerHandlerPath.example");


		HandlerPath hPath = new HandlerPath(request);

		String expectedPrefix = "://servername?";

		assertEquals("test1",expectedPrefix+"handlerPath=test.handlerPath.example&innerHandlerPath=test.innerHandlerPath.example&", hPath.getURL().toString());
		//assertEquals("test2",expectedPrefix+"handlerPath=test.handlerPath.example&innerHandlerPath=test.innerHandlerPath",hPath.getSuperInnerHandlerPathURL().toString());
		//assertEquals("test3",expectedPrefix+"handlerPath=test.handlerPath.example&innerHandlerPath=test.innerHandlerPath.example.extra", hPath.getAppendedInnerHandlerPathURL("extra").toString());
		assertEquals("test4",expectedPrefix+"handlerPath=handler_path&innerHandlerPath=inner_handler_path&", hPath.returnURL("://servername", HandlerPath.DEFAULT_HANDLERPATH_NAME, "handler_path", HandlerPath.DEFAULT_INNERHANDLERPATH_NAME, "inner_handler_path").toString());
	}


	}
