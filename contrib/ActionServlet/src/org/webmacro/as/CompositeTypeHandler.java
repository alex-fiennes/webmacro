/*
 *    Action Servlet is an extension of the WebMacro servlet framework, which 
 *    provides an easy mapping of HTTP requests to methods of Java components.
 *
 *    Copyright (C) 1999-2001  Petr Toman
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Library General Public
 *    License as published by the Free Software Foundation; either
 *    version 2 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Library General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this library.  If not, write to the Free Software Foundation, 
 *    Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package org.webmacro.as;

import org.webmacro.servlet.WebContext;

/**
 * Handler performing conversion of values of <EM>several parameters</EM> from HTTP request to 
 * a single Java type.
 * 
 * <P><B>Example</B>: composite type handler for <TT>java.util.Date</TT>
 * <EM>(see examples/DateHandler)</EM><P>
 * 
 * <UL>
 * <LI>Parameter <TT>date</TT> will take values from three INPUT text fields named: 'day', 'month' 
 * and 'year'. The <EM>composite type definition</EM> in the template file (.wm) looks like this:
 *
 * <P><TT>&lt;INPUT TYPE="Hidden" NAME="<B>date</B>" VALUE="<B>day,month,year</B>"&gt;</TT><P>
 *
 * <UL>
 * The <TT>VALUE</TT> describes components of composite parameter. Component parameters (separated 
 * by space and/or commas) will be passed in the <B>specified order</B> to the composite type handler. 
 *
 * <P>Note: If you want to allow the component to have more than one value, append array brackets 
 * <TT>[]</TT> to the component name, for example the 'day' parameter would be: <TT>day[]</TT>.
 * </UL><P>
 *
 * <LI>The template <TT>&lt;FORM&gt;</TT> will also include text fields mentioned above:
 *
 * <PRE>
 * &lt;INPUT TYPE="Text" NAME="<B>day</B>"&gt;
 * &lt;INPUT TYPE="Text" NAME="<B>month</B>"&gt;
 * &lt;INPUT TYPE="Text" NAME="<B>year</B>"&gt;
 * </PRE>
 *
 * <LI>The source code of <TT>java.util.Date</TT> type handling composite type handler 
 * follows:<P>
 *
 * <PRE>
 * public class DateHandler <B>implements CompositeTypeHandler</B> {
 *     private static Calendar calendar = Calendar.getInstance();
 *
 *     public Object convert(WebContext context, 
 *                           String[] componentNames,
 *                           String[][] componentValues) 
 *     throws ConversionException {
 *         if (componentNames.length < 3) 
 *             throw new ConversionException("Wrong composite definition for" +
 *                                           " type java.util.Date");
 *         int components[] = new int[3];
 *         int i=0;
 *          
 *         // try to convert components to integer values
 *         try {
 *            for (; i<3; i++)
 *                components[i] = Integer.parseInt(componentValues[i][0]);
 *         } catch (NumberFormatException e) {
 *            throw new ConversionException("Incorrect format", 
 *                                          componentNames[i], 
 *                                          componentValues[i][0],
 *                                          e);
 *         }
 *
 *         // use java.util.Calendar class to create java.util.Date object
 *         calendar.set(components[2], components[1], components[0]);
 *         return calendar.getTime();
 *     }
 * }
 * </PRE>
 *
 * <LI>Put a type handler definition to the configuration file as for any 
 * {@link TypeHandler TypeHandler}.
 *
 * @see ConversionException
 * @see SimpleTypeHandler
 */
public interface CompositeTypeHandler extends TypeHandler {
    /**
     * Method called when a parameter type conversion is needed.
     *
     * @param context context from {@link ActionServlet#handle(WebContext) handle()} method
     * @param componentNames names of components
     * @param componentValues values of components from HTTP request. First index controls
     *                        order of component in the composite definition, the second
     *                        can be used to access values of components (if there is only
     *                        one value for the component, only 'componentValues[i][0]'
     *                        exists).
     * @exception ConversionException on conversion error
     * @return parameter value of the appropriate Java type
     */
    public Object convert(WebContext context, 
                          String[] componentNames, 
                          String[][] componentValues) 
    throws ConversionException;
}