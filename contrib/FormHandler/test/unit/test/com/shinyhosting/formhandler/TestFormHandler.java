package test.com.shinyhosting.formhandler;


import org.webmacro.*;
import org.webmacro.servlet.*;
import junit.framework.*;
import javax.servlet.http.HttpServletRequest;

import com.shinyhosting.formhandler.*;

////////////////////////////////////////////////////////////////////////
/*
You don't test for it the action throws an exception...
*/

public class TestFormHandler extends TestCase
	{

	protected WM wm;

	public TestFormHandler(String name)
		{
		super(name);
		}

	/**
	 * Sets up the fixture, for example, open a network connection.
	 * This method is called before a test is executed.
	 */
	protected void setUp() throws Exception {
		org.apache.log4j.BasicConfigurator.configure();


		wm = new WM();
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

	public void testNoDoAction() throws Exception
	{
		// Create mock request, setting any submitted parameters needed.
		MyHttpRequest request = new MyHttpRequest();
		//request._parameters.put("","");

		WebContext c = new WebContext(wm.getBroker());
		c = c.newInstance(request, null);


		FormHandlerProp testProp = new FormHandlerProp()
			{
			};

		Object result = testProp.handle(c);

		assert("FormHandlerProp did not return DEFAULT_VALIDATE_RETURN_VALUE.", result == testProp.DEFAULT_VALIDATE_RETURN_VALUE);
	}

	public void testBasicDoAction() throws Exception
	{
		// Create mock request, setting any submitted parameters needed.
		MyHttpRequest request = new MyHttpRequest();
		request._parameters.put("_doAction","");

		WebContext c = new WebContext(wm.getBroker());
		c = c.newInstance(request, null);

		FormHandlerActionProp testProp = new FormHandlerActionProp();

		Object result = testProp.handle(c);

		assert("doAction was not called.", testProp.actionCalled == true && testProp.whichActionCalled.get(testProp.DO_ACTION));
		assert("FormHandlerProp did not return DEFAULT_VALIDATE_RETURN_VALUE.", result == testProp.DEFAULT_VALIDATE_RETURN_VALUE);

	}

	public void testDoActionReturn() throws Exception
	{
		// Create mock request, setting any submitted parameters needed.
		MyHttpRequest request = new MyHttpRequest();
		request._parameters.put("_doActionThatReturns","");

		WebContext c = new WebContext(wm.getBroker());
		c = c.newInstance(request, null);

		FormHandlerActionProp testProp = new FormHandlerActionProp();

		Object result = testProp.handle(c);

		assert("doAction was not called.", testProp.actionCalled == true && testProp.whichActionCalled.get(testProp.DO_ACTION_THAT_RETURNS));
		assert("FormHandlerProp doActionThatReturns did not return DOACTION_RETURN_VALUE.", result == testProp.DOACTION_RETURN_VALUE);

	}

	public void testDoActionEmptyParameters() throws Exception
	{
		// Create mock request, setting any submitted parameters needed.
		MyHttpRequest request = new MyHttpRequest();
		request._parameters.put("_doActionThatReturnsNull()","");

		WebContext c = new WebContext(wm.getBroker());
		c = c.newInstance(request, null);

		FormHandlerActionProp testProp = new FormHandlerActionProp();

		Object result = testProp.handle(c);

		assert("doAction was not called.", testProp.actionCalled == true && testProp.whichActionCalled.get(testProp.DO_ACTION_THAT_RETURNS_NULL));
		// Look for default value, because the doAction returned null and that leaves the validator's return value...
		assert("FormHandlerProp doActionThatReturnsNull did not return null. ("+result+")", result == testProp.DEFAULT_VALIDATE_RETURN_VALUE);

	}

	public void testDoActionParameters() throws Exception
	{
		// Create mock request, setting any submitted parameters needed.
		MyHttpRequest request = new MyHttpRequest();
		request._parameters.put("_doActionThatReturns(formVar1, formVar2)","");
		request._parameters.put("formVar1","formValue1");
		request._parameters.put("formVar2","formValue2");

		WebContext c = new WebContext(wm.getBroker());
		c = c.newInstance(request, null);

		FormHandlerActionProp testProp = new FormHandlerActionProp();

		Object result = testProp.handle(c);

		assert("doAction was not called.", testProp.actionCalled == true && testProp.whichActionCalled.get(testProp.DO_ACTION_THAT_RETURNS_PARAMETERS));
		assert("FormHandlerProp doActionThatReturns did not return DOACTION_RETURN_VALUE. ("+result.toString()+")", result.equals("formValue1formValue2"));

	}

	public abstract class FormHandlerProp extends FormHandler
		{
		final public Object DEFAULT_VALIDATE_RETURN_VALUE = new Object();
		final public String DOACTION_RETURN_VALUE = new String("doAction return value!");

		public boolean actionCalled = false;

		public Object postvalidate(WebContext c)
			{
			return DEFAULT_VALIDATE_RETURN_VALUE;
			}
		}

	public class FormHandlerActionProp extends FormHandlerProp
		{
		final public java.util.BitSet whichActionCalled = new java.util.BitSet();

		final public int DO_ACTION = 0;
		final public int DO_ACTION_THAT_RETURNS = 1;
		final public int DO_ACTION_THAT_RETURNS_NULL = 2;
		final public int DO_ACTION_THAT_RETURNS_PARAMETERS = 3;

		public void doAction()
			{
			actionCalled = true;
			whichActionCalled.set(DO_ACTION);
			}
		public String doActionThatReturns()
			{
			actionCalled = true;
			whichActionCalled.set(DO_ACTION_THAT_RETURNS);

			return DOACTION_RETURN_VALUE;
			}

		public String doActionThatReturnsNull()
			{
			actionCalled = true;
			whichActionCalled.set(DO_ACTION_THAT_RETURNS_NULL);

			return null;
			}

		public String doActionThatReturns(String str1, String str2)
			{
			actionCalled = true;
			whichActionCalled.set(DO_ACTION_THAT_RETURNS_PARAMETERS);

			return str1 + str2;
			}

		}


	}

