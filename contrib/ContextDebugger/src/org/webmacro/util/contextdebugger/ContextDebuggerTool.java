package org.webmacro.util.contextdebugger;

import org.webmacro.InvalidContextException;
import org.webmacro.Context;
import org.webmacro.servlet.WebContext;
import org.webmacro.ContextTool;
import org.webmacro.servlet.WebContext;



public class ContextDebuggerTool implements ContextTool {
    public Object init(Context context) throws InvalidContextException {
        try {
            WebContext wc = (WebContext) context;
            ContextDebugger util = new ContextDebugger(wc);
            return util;
        } catch (ClassCastException ce) {
            throw new InvalidContextException("ContextDebuggerTool only works with WebContext: " + ce);
        }
    }
   public void destroy(Object o){}
}
