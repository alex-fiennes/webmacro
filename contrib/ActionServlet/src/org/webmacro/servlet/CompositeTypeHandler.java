/*
 *    Action Servlet, an extension of the WebMacro library, which enables easy 
 *    mapping of user 'actions' to servlet methods.
 *
 *    Copyright (C) 1999-2000  Petr Toman
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
 *    along with this library; see the file COPYING.  If not, write to
 *    the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 *    Boston, MA 02111-1307, USA.
 */
package org.webmacro.servlet;

/**
 * Handler performing conversion of values of <EM>several parameters</EM> from HTTP request to 
 * a single Java type.
 * 
 * <P>The following example explains how can a composite type handler create <TT>java.util.Date</TT>
 * object from HTTP parameters:<P>
 * 
 * <P><OL>
 * <LI>The type <TT>Date</TT> takes values from three HTML form text fields named: 'day', 
 * 'month' and 'year'. The composite handler should take their values and create an object 
 * of class <TT>java.util.Date</TT>.
 *
 * <LI>Supposing that the method implementing 'Set date' action has only one parameter of 
 * type <TT>java.util.Date</TT>, the configuration file of such action servlet will contain 
 * action definition like:<P>
 *
 * <PRE>
 * action.0 = "Set date" setDate(java.util.Date)
 * </PRE>
 *
 * <LI>Put the composite type definition to the template or HTML file using the INPUT tag:<P>
 *
 * <PRE>
 * &lt;INPUT TYPE="HIDDEN" <B>NAME="Date" VALUE="day,month,year"</B>&gt;
 * </PRE>
 * 
 * <UL><P>
 *
 * <LI>The string <TT>VALUE</TT> describes components of composite parameter. Component 
 * parameters (separated by space and/or commas) will be passed in the <B>specified order</B> 
 * to the composite type handler. 
 *
 * <LI>If you want to allow the component to have more than one value, append array brackets 
 *  <TT>[]</TT> to the component name, for example the 'day' parameter would be: <TT>day[]</TT>.
 * </UL><P>
 *
 * Note: Because of security reasons, no Java types are used in the composite definition in 
 *       the HTML.
 *
 * <P><LI>The source code of <TT>java.util.Date</TT> type handling composite type handler 
 * follows:<P>
 *
 * <PRE>
 * public class DateHandler implements CompositeTypeHandler {
 *     public Object convert(WebContext context, 
 *                           String[] componentNames,
 *                           String[][] componentValues) 
 *     throws ConversionException {
 *         if (parameterValues.length < 3) 
 *             throw new ConversionException("Wrong composite definition for" +
 *                                           " type java.util.Date");
 *         int components[] = new int[3];
 *         int i=0;
 *          
 *         // try to convert components to integer values
 *         try {
 *            for (; i<3; i++)
 *                components[i] = Integer.parseInt(parameterValues[i][0]),
 *         } catch (NumberFormatException e) {
 *            throw new ConversionException("Incorrect format", 
 *                                            componentNames[i], 
 *                                            componentValues[i],
 *                                            e);
 *         }
 *
 *         // use java.util.Calendar class to create java.util.Date object
 *         Calendar calendar = Calendar.getInstance();
 *         calendar.set(year, month, day);
 *         return calendar.getTime();
 *     }
 * }
 * </PRE>
 *
 * <LI>Put a type handler definition to <TT>&lt;servlet&gt;_handlers.properties</TT> 
 *     (as described in the {@link TypeHandler TypeHandler} documentation.
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