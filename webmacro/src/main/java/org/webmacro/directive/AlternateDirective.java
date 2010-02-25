/*
 * Copyright (C) 1998-2000 Semiotek Inc.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted under the terms of either of the following
 * Open Source licenses:
 *
 * The GNU General Public License, version 2, or any later version, as
 * published by the Free Software Foundation
 * (http://www.fsf.org/copyleft/gpl.html);
 *
 *  or
 *
 * The Semiotek Public License (http://webmacro.org/LICENSE.)
 *
 * This software is provided "as is", with NO WARRANTY, not even the
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See www.webmacro.org for more information on the WebMacro project.
 */
package org.webmacro.directive;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.webmacro.Context;
import org.webmacro.FastWriter;
import org.webmacro.Macro;
import org.webmacro.PropertyException;
import org.webmacro.TemplateVisitor;
import org.webmacro.engine.BuildContext;
import org.webmacro.engine.BuildException;
import org.webmacro.engine.Variable;
/**
 * 
 * Use the #alternate directive to create an "alternating" variable, 
 * for creating tables that use a different background color for each line, etc.
 * <p>
 * <pre>
 * 
 *  Syntax:
 * 
 *    #alternate &lt;i&gt;var-reference&lt;/i&gt; through &lt;i&gt;expression&lt;/i&gt;
 * 
 *  Example:
 * 
 *    #alternate $color through [ &quot;red&quot;, &quot;blue&quot;, &quot;green&quot; ]
 * 
 *    #foreach $row in $rows {
 * 
 * <TR BGCOLOR=$color>
 *  ... blah blah ...
 * 
 *    }
 * 
 * </pre>
 * 
 * <p>
 * 
 * <i>expression</i> can be any "list" type understood by WebMacro: Object[],
 * 
 * java.util.List, java.util.Iterator, java.util.Enumeration, or any other
 * 
 * object that has either of the following method signatures:
 * <p>
 * 
 * <pre>
 * 
 *     public Iterator iterator ();
 * 
 *            or
 * 
 *     public Enumeration elements ();
 * 
 * </pre>
 * 
 * <p>
 * 
 * The first time $color is evaluated, it will have the value "red". The next
 * time it is evaluated, it will have the value "blue", and so on through the list. 
 * When it gets to the end of the list, it wraps back around to the beginning.
 * 
 */
public class AlternateDirective extends Directive
{
	private static final int ALTERNATE_TARGET = 1;
	private static final int ALTERNATE_THROUGH = 2;
	private static final int ALTERNATE_LIST = 3;
	private Variable target;
	private Object list;
	private static final ArgDescriptor[]
	myArgs = new ArgDescriptor[] {
	new LValueArg(ALTERNATE_TARGET),
	new KeywordArg(ALTERNATE_THROUGH, "through"),
	new RValueArg(ALTERNATE_LIST)
	};
	private static final DirectiveDescriptor
	myDescr = new DirectiveDescriptor("alternate", null, myArgs, null);
	public static DirectiveDescriptor getDescriptor()
	{
		return myDescr;
	}
	public Object build(DirectiveBuilder builder,
	BuildContext bc)
	throws BuildException
	{
		try
		{
			target = (Variable) builder.getArg(ALTERNATE_TARGET, bc);
		}
		catch (ClassCastException e)
		{
			throw new NotVariableBuildException(myDescr.name, e);
		}
		list = builder.getArg(ALTERNATE_LIST, bc);
		return this;
	}
	public void write(FastWriter out, Context context)
	throws PropertyException, IOException
	{
		Object l = null;
		try
		{
			if (list instanceof Macro)
				l = ((Macro) list).evaluate(context);
			else
				l = list;
			Iterator itr = context.getBroker()._propertyOperators
					.getIterator(l);
			target.setValue(context, new IteratorAlternator(itr));
		}
		catch (Exception e)
		{
			String warning = "#alternate: list argument is not a list: " + l;
			writeWarning(warning, context, out);
			return;
		}
	}
	public void accept(TemplateVisitor v)
	{
		v.beginDirective(myDescr.name);
		v.visitDirectiveArg("AlternateTarget", target);
		v.visitDirectiveArg("AlternateList", list);
		v.endDirective();
	}
}
abstract class Alternator implements Macro
{
	public abstract Object evaluate(Context context);
	public void write(FastWriter out, Context context)
			throws PropertyException, IOException
	{
		Object o = evaluate(context);
		if (o != null)
			out.write(o.toString());
	}
}
class IteratorAlternator extends Alternator
{
	private Iterator<Object> itr;
	private List<Object> list;
	private int index = -1;
	public IteratorAlternator(Iterator<Object> itr)
	{
		this.itr = itr;
		this.list = new ArrayList<Object>();
	}
	public Object evaluate(Context context)
	{
		Object o;
		if (index == -1 && itr.hasNext())
		{
			o = itr.next();
			list.add(o);
		}
		else
		{
			index++;
			if (index == list.size())
				index = 0;
			o = list.get(index);
		}
		return o;
	}
}
