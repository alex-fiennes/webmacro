package org.webmacro.servlet;

import org.webmacro.Context;
import org.webmacro.ContextTool;
import org.webmacro.PropertyException;

/**
 * @author Zeljko Trogrlic
 */

public class VariableTool implements ContextTool {

	Context context;

	public VariableTool(Context newContext) {
		context = newContext;
    }

    public Object init(Context c) throws PropertyException {
		return new VariableTool(c);
    }

    public void destroy(Object o) {
    }

	public boolean isDefined(Object name) {
		return context.containsKey(name);
	}
}