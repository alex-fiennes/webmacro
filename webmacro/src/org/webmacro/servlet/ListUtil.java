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

package org.webmacro.servlet;

import java.util.*;

/**
 * A utility class for templates loaded into the context as "List" by ListTool.
 * It allows template designers to work with Java arrays and lists using
 * without having to distinguish between them.
 *
 * @author Keats Kirsch
 * @author Zeljko Trogrlic
 * @version $Revision$
 * @since Oct. 2000
 * @see ListTool
 */
public class ListUtil
{

    /**
     * Private constructor for a singleton class
     */
    private ListUtil ()
    {
    }

    private static ListUtil _singleton = new ListUtil();

    /**
     * @return the singleton instance of this class
     */
    public static ListUtil getInstance ()
    {
        return _singleton;
    }

    /**
     *
     * @param o
     * @return true if the argument implements the java.util.List interface, false otherwise.
     */
    public boolean isList (Object o)
    {
        if (o == null) return false;
        return o instanceof List;
    }

    /**
     * @param o
     * @return true if the argument is an array, otherwise false.
     */
    public boolean isArray (Object o)
    {
        if (o == null) return false;
        return o.getClass().isArray();
    }

    /**
     * @param arg
     * @return false if the argument is a list or an array with at least one element,
     * true otherwise.
     */
    public static boolean isEmpty (Object arg)
    {
        if (arg == null) return true;
        if (arg instanceof List) return ((List) arg).isEmpty();
        if (arg instanceof Object[]) return ((Object[]) arg).length == 0;
        if (arg instanceof Iterator) return ((Iterator) arg).hasNext();
        if (arg instanceof Enumeration)
            return ((Enumeration) arg).hasMoreElements();
        // check for primitive arrays
        if (arg.getClass().isArray())
        {
            return java.lang.reflect.Array.getLength(arg) == 0;
        }
        return true;
    }

    /**
     * Returns a List for any Object.  The action taken depends on the
     * type of the argument:
     * <ul>
     * <li>arg implements List: return the argument unchanged.</li>
     * <li>arg is an object array: wrap using Arrays.asList().</li>
     * <li>arg is an Iterator or Enumeration: all the
     * elements are copied into a new ArrayList.</li>
     * <li>arg is an Array of primitives: elements are copied into an ArrayList.</li>
     *   <ul>
     *     <li>If the Iterator is a ListIterator it is reset to the beginning</li>
     *     <li>Otherwise Iterators and Enumerations are exhausted by this method.</li>
     *   </ul>
     * <li>arg is null: a new empty List is returned</li>
     * <li>arg is anything else: it is added to a new list</li>
     * </ul>
     *
     * @param arg
     */
    public static List toList (Object arg)
    {
        List list = null;
        if (arg instanceof List)
        {
            list = (List) arg;
        }
        else if (arg == null)
        { // return an empty list
            list = Arrays.asList(new Object[0]);
        }
        else if (arg instanceof Object[])
        {
            list = Arrays.asList((Object[]) arg);
        }
        else if (arg instanceof Iterator)
        {
            list = iteratorToList((Iterator) arg);
        }
        else if (arg instanceof Enumeration)
        {
            list = iteratorToList(new org.webmacro.util.EnumIterator((Enumeration) arg));
        }
        else if (arg.getClass().isArray())
        {
            // array of primitives
            list = iteratorToList(new org.webmacro.util.PrimitiveArrayIterator(arg));
        }
        else
        {
            // put the object into a single element list
            Object[] oa = {arg};
            list = Arrays.asList(oa);
        }
        return list;
    }

    static private List iteratorToList (Iterator iter)
    {
        List list = new ArrayList();
        while (iter.hasNext())
        {
            list.add(iter.next());
        }
        // rewind the Iterator if it is a ListIterator
        if (iter instanceof ListIterator)
        {
            ListIterator li = (ListIterator) iter;
            while (li.hasPrevious()) li.previous();
        }
        return list;
    }

    /**
     * Allows access to elements in an array by position.  Index is zero based.
     *
     * @param oa
     * @param pos
     */
    public static Object getItem (Object[] oa, int pos)
    {
        if ((pos < 0) || ((pos - 1) > oa.length))
            throw new IndexOutOfBoundsException(
                    "Index must be between 0 and " + (oa.length - 1)
                    + ", user specified " + pos);
        return oa[pos];
    }

    /**
     * Allows access to elements in a List by position.  Index is zero based.
     *
     * @param list
     * @param pos
     */
    public static Object getItem (List list, int pos)
    {
        if (pos < 0 || (pos + 1) > list.size())
            throw new IndexOutOfBoundsException(
                    "Index must be between 0 and " + (list.size() - 1)
                    + ", user specified " + pos);
        return list.get(pos);
    }

    /**
     * Allows access to elements in a array of primitives by position.
     * The index is zero based.
     *
     * @param arr the array of primitives
     * @param pos the position (0 based) to retrieve from
     */
    public static Object getItem (Object arr, int pos)
    {
        if (!arr.getClass().isArray())
            throw new IllegalArgumentException(
                    "The first argument must be of List or array type.");
        return java.lang.reflect.Array.get(arr, pos);
    }

    /**
     * @param oa
     * @return number of elements in array argument
     */
    public static int size (Object[] oa)
    {
        return oa.length;
    }

    /**
     * @param list
     * @return number of elements in List argument
     */
    public static int size (List list)
    {
        return list.size();
    }

    public static int size (Object arr)
    {
        if (!arr.getClass().isArray())
            throw new IllegalArgumentException(
                    "The argument must be of List or array type.");
        return java.lang.reflect.Array.getLength(arr);
    }

    /**
     * @param list
     * @param o
     * @return true if List argument contains the object argument, else false
     */
    public static boolean contains (List list, Object o)
    {
        return list.contains(o);
    }

    /**
     * @param oa
     * @param o
     * @return true if Array argument contains the object argument, else false
     */
    public static boolean contains (Object[] oa, Object o)
    {
        return contains(Arrays.asList(oa), o);
    }

    /**
     * @param arr
     * @param o
     * @return true if array argument contains the object argument, else false
     */
    public static boolean contains (Object arr, Object o)
    {
        if (!arr.getClass().isArray())
            throw new IllegalArgumentException(
                    "The argument must be of List or array type.");
        for (int i = 0; i < java.lang.reflect.Array.getLength(arr); i++)
        {
            if (o.equals(java.lang.reflect.Array.get(arr, i))) return true;
        }
        return false;
    }

    /**
     * Splits list into multiple lists of equal size. If list size cannot be divided
     * by column count, last part is padded with nulls.
     * @param arg List to be splitted.
     * @param colCount Number of elements in each split.
     * @return List of list parts.
     */
    public static List split (List arg, int colCount)
    {

        return split(arg, colCount, true, null);
    }

    /**
     * Splits list into multiple lists of equal size. If list size cannot be divided
     * by column count, fill parameter determines should it be padded with nulls
     * or not.
     * @param arg List to be splitted.
     * @param colCount Number of elements in each split.
     * @param pad Last split should be null padded?
     * @return List of list parts.
     */
    public static List split (List arg, int colCount, boolean pad)
    {

        return split(arg, colCount, pad, null);
    }

    /**
     * Splits list into multiple lists of equal size. If list size cannot be divided
     * by column count, it's padded with pad value.
     * @param arg List to be splitted.
     * @param colCount Number of elements in each split.
     * @param padValue Value that will be used for padding.
     * @return List of list parts.
     */
    public static List split (List arg, int colCount, Object padValue)
    {

        return split(arg, colCount, true, padValue);
    }

    /**
     * Splits list into multiple lists of equal size. If list size cannot be
     * divided by column count, fill parameter determines should it be padded with
     * pad value or not.
     * @param arg List to be splitted.
     * @param colCount Number of elements in each split.
     * @param pad Last split should be null padded?
     * @param padValue Value that will be used for padding.
     * @return List of list parts.
     */
    public static List split (List arg, int colCount, boolean pad,
                              Object padValue)
    {

        int size = arg.size();
        List rows = new ArrayList(size / colCount + 1);
        int start = 0;
        int end = colCount;
        while (start < size)
        {
            List row;
            // check is this last and uncomplete row
            if (end > size)
            {
                // using sublist directly can cause synchronization problems
                row = new ArrayList(arg.subList(start, size));
                if (pad)
                {
                    for (int i = size; i < end; ++i)
                    {
                        row.add(padValue);
                    }
                }
            }
            else
            {
                row = new ArrayList(arg.subList(start, end));
            }
            rows.add(row);
            start = end;
            end += colCount;
        }
        return rows;
    }

    /**
     * Splits array into multiple arrays of equal size. If list size
     * cannot be divided by column count, last part is padded with
     * nulls.
     * @param arg array to be splitted.
     * @param colCount Number of elements in each split.
     * @return array of list parts.  */
    public static Object[] split (Object[] arg, int colCount)
    {

        return split(arg, colCount, true, null);
    }

    /**
     * Splits array into multiple arrays of equal size. If list size
     * cannot be divided by column count, fill parameter determines
     * should it be padded with nulls or not.
     * @param arg array to be splitted.
     * @param colCount Number of elements in each split.
     * @param pad Last split should be null padded?
     * @return array of list parts.  */
    public static Object[] split (Object[] arg, int colCount, boolean pad)
    {

        return split(arg, colCount, pad, null);
    }

    /**
     * Splits array into multiple arrays of equal size. If list size
     * cannot be divided by column count, it's padded with pad value.
     * @param arg Object[] to be splitted.
     * @param colCount Number of elements in each split.
     * @param padValue Value that will be used for padding.
     * @return Object[] of list parts.  */
    public static Object[] split (Object[] arg, int colCount, Object padValue)
    {

        return split(arg, colCount, true, padValue);
    }

    /**
     * Splits array into multiple arrays of equal size. If array size
     * cannot be divided by column count, fill parameter determines
     * should it be padded with nulls or not.
     * @param arg Array to be splitted.
     * @param colCount Number of elements in each split.
     * @param pad Last split should be null padded?
     * @param padValue Value that will be used for padding.
     * @return Array of array parts.  */
    public static Object[][] split (Object[] arg, int colCount, boolean pad,
                                    Object padValue)
    {

        int size = arg.length;
        int rowCount = size / colCount;
        if ((size % colCount) != 0)
        {
            ++rowCount;
        }
        Object[][] rows = new Object[rowCount][];
        int start = 0;
        int end = colCount;
        for (int rowNo = 0; rowNo < rowCount; ++rowNo)
        {
            Object[] row;
            // check is this last and uncomplete row
            if (end > size)
            {
                int tail = size - start;
                if (pad)
                {
                    row = new Object[colCount];
                    System.arraycopy(arg, start, row, 0, tail);
                    if (padValue != null)
                    {
                        for (int i = tail; i < end; ++i)
                        {
                            row[i] = padValue;
                        }
                    }
                }
                else
                {
                    row = new Object[tail];
                    System.arraycopy(arg, start, row, 0, tail);
                }
            }
            else
            {
                row = new Object[colCount];
                System.arraycopy(arg, start, row, 0, colCount);
            }
            rows[rowNo] = row;
            start = end;
            end += colCount;
        }
        return rows;
    }

    /**
     * Transposes and splits array into multiple arrays of equal size. If array
     * size cannot be divided by column count, it's padded with nulls.
     * @param arg Array to be splitted.
     * @param colCount Number of elements in each split.
     * @return Array of array parts.
     */
    public static Object[][] transposeSplit (Object[] arg, int colCount)
    {

        return transposeSplit(arg, colCount, true, null);
    }

    /**
     * Transposes and splits array into multiple arrays of equal
     * size. If array size cannot be divided by column count, fill
     * parameter determines should it be padded with nulls or not.
     * @param arg Array to be splitted.
     * @param colCount Number of elements in each split.
     * @param pad Last split should be null padded?
     * @return Array of array parts.  */
    public static Object[][] transposeSplit (Object[] arg, int colCount,
                                             boolean pad)
    {

        return transposeSplit(arg, colCount, pad, null);
    }

    /**
     * Transposes and splits array into multiple arrays of equal size. If array
     * size cannot be divided by column count, it's padded with padValue.
     * @param arg Array to be splitted.
     * @param colCount Number of elements in each split.
     * @param padValue Value that will be used for padding.
     * @return Array of array parts.
     */
    public static Object[][] transposeSplit (Object[] arg, int colCount,
                                             Object padValue)
    {

        return transposeSplit(arg, colCount, true, padValue);
    }

    /**
     * Transposes and splits array into multiple arrays of equal
     * size. If array size cannot be divided by column count, fill
     * parameter determines should it be padded with padValue or not.
     * @param arg Array to be splitted.
     * @param colCount Number of elements in each split.
     * @param pad Last split should be null padded?
     * @param padValue Value that will be used for padding.
     * @return Array of array parts.
     */
    public static Object[][] transposeSplit (Object[] arg, int colCount,
                                             boolean pad, Object padValue)
    {
        int size = arg.length;
        int rowCount = size / colCount;
        Object[][] rows;
        boolean slack = (size % colCount) != 0;
        if (slack)
        {
            ++rowCount;
        }
        if (slack && !pad)
        {
            int tail = size % rowCount;
            rows = new Object[rowCount][];
            for (int rowNo = 0; rowNo < rowCount; ++rowNo)
            {
                if (rowNo < tail)
                {
                    rows[rowNo] = new Object[colCount];
                }
                else
                {
                    rows[rowNo] = new Object[colCount - 1];
                }
            }
        }
        else
        {
            rows = new Object[rowCount][colCount];
        }

        int pos = 0;
        for (int colNo = 0; colNo < colCount; ++colNo)
        {
            for (int rowNo = 0; rowNo < rowCount; ++rowNo)
            {
                if (pos < size)
                {
                    rows[rowNo][colNo] = arg[pos++];
                }
                else if (pad)
                {
                    rows[rowNo][colNo] = padValue;
                }
                else
                {
                    break;
                }
            }
        }
        return rows;
    }

    /**
     * Transposes and splits list into multiple lists of equal size. If list
     * size cannot be divided by column count, it's padded with nulls.
     * @param arg List to be splitted.
     * @param colCount Number of elements in each split.
     * @return List of list parts.
     */
    public static List transposeSplit (List arg, int colCount)
    {

        return transposeSplit(arg, colCount, true, null);
    }

    /**
     * Transposes and splits list into multiple lists of equal size. If
     * list size cannot be divided by column count, fill parameter
     * determines should it be padded with nulls or not.
     * @param arg List to be splitted.
     * @param colCount Number of elements in each split.
     * @param pad Last split should be null padded?
     * @return List of list parts.  */
    public static List transposeSplit (List arg, int colCount,
                                       boolean pad)
    {

        return transposeSplit(arg, colCount, pad, null);
    }

    /**
     * Transposes and splits list into multiple lists of equal size. If list
     * size cannot be divided by column count, it's padded with padValue.
     * @param arg List to be splitted.
     * @param colCount Number of elements in each split.
     * @param padValue Value that will be used for padding.
     * @return List of list parts.
     */
    public static List transposeSplit (List arg, int colCount,
                                       Object padValue)
    {

        return transposeSplit(arg, colCount, true, padValue);
    }

    /**
     * Transposes and splits list into multiple lists of equal size. If
     * list size cannot be divided by column count, fill parameter
     * determines should it be padded with padValue or not.
     * @param arg List to be splitted.
     * @param colCount Number of elements in each split.
     * @param pad Last split should be null padded?
     * @param padValue Value that will be used for padding.
     * @return List of list parts.  */
    public static List transposeSplit (List arg, int colCount,
                                       boolean pad, Object padValue)
    {

        int size = arg.size();
        int rowCount = size / colCount;
        if ((size % colCount) != 0)
        {
            ++rowCount;
        }
        List rows = new ArrayList(rowCount);
        for (int rowNo = 0; rowNo < rowCount; ++rowNo)
        {
            rows.add(new ArrayList(colCount));
        }
        Iterator it = arg.iterator();
        for (int colNo = 0; colNo < colCount; ++colNo)
        {
            for (int rowNo = 0; rowNo < rowCount; ++rowNo)
            {
                List row = (List) rows.get(rowNo);
                if (it.hasNext())
                {
                    row.add(it.next());
                }
                else if (pad)
                {
                    row.add(padValue);
                }
                else
                {
                    break;
                }
            }
        }
        return rows;
    }

    public static List createRange (int rangeBegin, int rangeEnd)
    {
        return createRange(rangeBegin, rangeEnd, 1);
    }

    public static List createRange (int rangeBegin, int rangeEnd, int incr)
    {
        if (incr > 0)
        {
            if (rangeBegin > rangeEnd)
                throw new IllegalArgumentException("Starting number must be less than ending number");
        }
        else if (incr < 0)
        {
            if (rangeBegin < rangeEnd)
                throw new IllegalArgumentException("Starting number must be greater than ending number");
        }
        else
        { // incr == 0
            throw new IllegalArgumentException("Increment cannot be zero");
        }

        int size = ((rangeEnd - rangeBegin) / incr) + 1;
        Integer[] ia = new Integer[size];
        int i = 0;
        for (int num = rangeBegin; (incr > 0) ? num <= rangeEnd : num >= rangeEnd; num += incr)
        {
            ia[i++] = new Integer(num);
        }
        return Arrays.asList(ia);
    }

    /** create a new ArrayList */
    public static ArrayList create ()
    {
        return new ArrayList();
    }

    /** create a new ArrayList with the specified capacity */
    public static ArrayList create (int capacity)
    {
        return new ArrayList(capacity);
    }

    /** append one list to the end of another and return the expanded list.
     * If the first list is not expandable, return a new expandable list
     * with the elements of each list appended
     */
    public static List append (Object o1, Object o2)
    {
        List l1 = toList(o1);
        List l2 = toList(o2);
        try
        {
            l1.addAll(l2);
            return l1;
        }
        catch (Exception e)
        {
        }

        // create a new list
        List l = new ArrayList(((l1.size() + l2.size()) * 2) + 10);
        l.addAll(l1);
        l.addAll(l2);
        return l;
    }

    /** create a new list (ArrayList) with all the elements in the supplied list */
    public static List copy (Object o)
    {
        List l = toList(o);
        if (l.isEmpty()) return new ArrayList(10);
        return new ArrayList(l);
    }

    /** test harness */
    public static void main (String[] args)
    {
        java.io.PrintWriter out =
                new java.io.PrintWriter(System.out, true);
        ListUtil lu = ListUtil.getInstance();

        out.println("createRange(2, 10, 2): " + createRange(2, 10, 2));
        out.println("createRange(-10, 0): " + createRange(-10, 0));
        out.println("createRange(21, 10, -5): " + createRange(21, 10, -5));
        out.println("createRange(21, 21, -5): " + createRange(21, 21, -5));

        Object[] arr = {
            "ant", "bird", "cat", "dog", "elephant", "ferret", "gopher"
        };
        ArrayList l = new ArrayList(Arrays.asList(arr));

        out.println("List/Array results");
        out.print("toList(): ");
        out.println(ListUtil.toList(l) + "/" + ListUtil.toList(arr));
        out.print("size: ");
        out.println(ListUtil.size(l) + "/" + ListUtil.size(arr));
        out.print("contains(\"bird\"): ");
        out.println(ListUtil.contains(l, "bird") + "/" + ListUtil.contains(arr, "bird"));
        out.print("contains(\"fish\"): ");
        out.println(ListUtil.contains(l, "fish") + "/" + ListUtil.contains(arr, "fish"));
        out.print("isArray: ");
        out.println(lu.isArray(l) + "/" + lu.isArray(arr));
        out.print("isList: ");
        out.println(lu.isList(l) + "/" + lu.isList(arr));
        out.print("getItem(5): ");
        out.println(ListUtil.getItem(l, 5) + "/" + ListUtil.getItem(arr, 5));
        out.print("getItem(0): ");
        try
        {
            out.println(ListUtil.getItem(l, 0) + "/" + ListUtil.getItem(arr, 0));
        }
        catch (Exception e)
        {
            out.println(e);
        }
        out.println("toList(null): " + ListUtil.toList(null));
        out.println("toList(\"a string\"): " + ListUtil.toList("a string"));

        StringTokenizer st = new StringTokenizer(
                "This is a bunch of words!");
        List l2 = ListUtil.toList(st);
        out.println("toList(Enumeration): " + l2);
        Iterator iter = l2.listIterator();
        List l3 = ListUtil.toList(iter);
        out.println("toList(Iterator): " + l3 + ", iter.hasNext(): " + iter.hasNext());
        // test split
        out.println("List split with fill");
        List splitList1 = split(l, 3, true);
        for (Iterator it1 = splitList1.iterator(); it1.hasNext();)
        {
            out.print("-: ");
            List part = (List) it1.next();
            for (Iterator it2 = part.iterator(); it2.hasNext();)
            {
                out.print(it2.next() + ", ");
            }
            out.println("*");
        }
        out.println("List transposeSplit");
        List splitList2 = transposeSplit(l, 3, false);
        for (Iterator it1 = splitList2.iterator(); it1.hasNext();)
        {
            out.print("-: ");
            List part = (List) it1.next();
            for (Iterator it2 = part.iterator(); it2.hasNext();)
            {
                out.print(it2.next() + ", ");
            }
            out.println("*");
        }
        out.println("Array split");
        Object[] splitArray1 = split(new String[]{"pero"}, 2, false);
        for (int i = 0; i < splitArray1.length; ++i)
        {
            out.print("-: ");
            Object[] part = (Object[]) splitArray1[i];
            for (int j = 0; j < part.length; ++j)
            {
                out.print(part[j] + ", ");
            }
            out.println("*");
        }
        out.println("Array transposeSplit");
        Object[][] splitArray3 = transposeSplit(
                new String[]{
                    "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                    "11", "12", "14", "15", "16", "17", "18", "19"
                }, 3, false);
        for (int i = 0; i < splitArray3.length; ++i)
        {
            out.print("-: ");
            for (int j = 0; j < splitArray3[i].length; ++j)
            {
                out.print(splitArray3[i][j] + ", ");
            }
            out.println("*");
        }
        // test primitive arrays
        int[] emptyInts = new int[0];
        out.println("Empty array of int: isEmpty=" + isEmpty(emptyInts));
        char[] chars = {'A', 'B', 'C'};
        out.println("Array of char: isEmpty=" + isEmpty(chars) + ", size=" + size(chars));
        out.println("contains 'C'=" + contains(chars, new Character('C')));
        out.println("contains 'Z'=" + contains(chars, new Character('Z')));
        out.println("toList=" + toList(chars));
        float[] f = new float[]{1.1f, 2.2f, 3.3f};
        out.println("getItem(floats, 0)=" + getItem(f, 0));
        List appendList = append(f, chars);
        out.println("append(f, chars)=" + appendList);
        append(appendList, "another thing");
        out.println("append(appendList, \"another thing\")=" + appendList);
    }
}
