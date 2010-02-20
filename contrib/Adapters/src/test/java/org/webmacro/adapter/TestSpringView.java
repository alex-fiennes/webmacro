package org.webmacro.adapter;

import junit.framework.TestCase;
import org.webmacro.adapter.spring.WebMacroView;
import org.webmacro.WM;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Marc Palmer (marc@anyware.co.uk)
 */
public class TestSpringView extends TestCase
{
    public TestSpringView(String s)
    {
        super(s);
    }


    public void testViewRender() throws Exception
    {
        WM wm = new WM();
        WebMacroView v = new WebMacroView();
        v.setWebMacro(wm);

        // Setup dummy Spring context and provide a WM instance so that
        // It doesn't try to resolve test template from Servletcontext
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("WebMacro", wm);

        GenericWebApplicationContext springContext =
            new GenericWebApplicationContext(beanFactory);

        v.setUrl( "org/webmacro/adapter/testview.wm");

        v.setApplicationContext( springContext );

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        Map model = new HashMap();
        model.put( "location", "Cleveland");

        v.render(model, request, response);

        assertEquals( "Hello Cleveland, hello Cleveland!",
            response.getContentAsString());
    }
}
