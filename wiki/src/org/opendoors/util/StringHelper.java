package org.opendoors.util;

import java.util.*;

/**
 * A class with several String utilities.
 * <p>
 * It contains localization utilities, such as
 * static methods for converting strings from and to the default
 * platform representation using a certain character encoding
 * (using JDK codes for the converter).
 * <p>
 * This class also has methods to take lists of supported locales and
 * character encoding names and their corresponding 'JDK codes'. Those codes
 * can be used for setting the encoding of widgets.  There is a method
 * that returns the correct code for a given name (not case sensitive)
 * and a method that returns the name for a given code.
 *
 */
public class StringHelper
{

    /**
     * Never instantiated.
     */
    private StringHelper ()
    {
    }


    /**
     * Splits a list separated by a delimiter into an array.
     */
    public static String[] split (String list, String delimiter)
    {
        StringTokenizer token = new StringTokenizer(list, delimiter);
        int count = token.countTokens();
        String[] fields = new String[count];
        int index = 0;
        while (token.hasMoreTokens())
        {
            fields[index++] = token.nextToken();
        }
        return fields;
    }


    /**
     * Indexes a set of unique strings and returns it as a hash.
     */
    public static Hashtable indexStrings (String[] string)
    {
        Hashtable hash = new Hashtable();
        for (int index = 0; index < string.length; index++)
            hash.put(string[index], new Integer(index));
        return hash;
    }


    /**
     * Determines if a string is a member of an array of strings. Case sensitive.
     * @param item String item.
     * @param list The list possibly containing item.
     */
    public static boolean isMember (String item, String[] list)
    {
        for (int index = 0; index < list.length; index++)
        {
            if (list[index].equals(item))
                return true;
        }
        return false;
    }


    /**
     * Converts byte array to string,
     * using one character per byte.
     * NOTE: this method does NOT use the JDK encoding mechanism!
     */
    public static String bytesToString (byte[] bytes)
    {
        char value[] = new char[bytes.length];
        for (int i = bytes.length; i-- > 0;)
        {
            value[i] = (char) (bytes[i] & 0xff);
        }
        return new String(value).trim();
    }


    /**
     * Converts String to bytes, using one byte per character.
     * NOTE: this method does NOT use the JDK encoding mechanism!
     */
    public static byte[] stringToBytes (String str)
    {
        char value[] = str.trim().toCharArray();
        byte[] bytes = new byte[value.length];
        for (int i = value.length; i-- > 0;)
        {
            bytes[i] = (byte) value[i];
        }
        return bytes;
    }


    /**
     * Replace all occurences of string one with string two.
     * It does not replace if a string has an escape character
     * in front of it ('\').
     * @param org The original string in which strings have to be replaced.
     * @param str1 The string to-be-replaced.
     * @param str2 The new string that replaced 'str1'.
     * @return The newly constructed string.
     */
    public static String stringReplace (String org, String str1, String str2)
    {
        StringBuffer sb = new StringBuffer();
        int oldi = 0;
        int i = org.indexOf(str1);
        while (i >= 0)
        {
            sb.append(org.substring(oldi, i));

            // check escape character
            if (i > 0 && org.charAt(i - 1) == '\\')
            {
                // do not replace
                sb.append(str1);
            }
            else
            {
                // replace now!
                sb.append(str2);
            }
            oldi = i + str1.length();
            i = org.indexOf(str1, oldi);
        }
        sb.append(org.substring(oldi, org.length()));
        return sb.toString();
    }

    /////////////// CONVERSION METHODS ////////////////

    /**
     * Convert a string with the default platform encoding to a
     * string with the given encoding.
     *
     * @param s The string with default encoding that needs to be converted.
     * @param encoding The (IANA) name for the encoding.
     * @return The same string, but in the given encoding.
     * @see #convertToDefault(String, String)
     * @see #convertFromDefault(char, String)
     */
    public static String convertFromDefault (String s, String encoding)
    {
        try
        {
            return new String(s.getBytes(), encoding);
        }
        catch (Throwable e)
        {
            // unknown encoding
            // System.out.println("Unknown encoding: " + encoding);
            return s;
        }
    }


    /**
     * Convert a character in the default encoding to the given
     * character encoding.
     *
     * @param character The character with default encoding.
     * @param encoding The encoding of the result.
     * @return the character in the given encoding.
     * @see #convertFromDefault(String, String)
     */
    public static char convertFromDefault (char character, String encoding)
    {
        String str = convertFromDefault("" + character, encoding);
        if (str.length() > 0)
        {
            return str.charAt(0);
        }
        else
        {
            return character;
        }
    }


    /**
     * Converts a string with the given encoding to a string in the
     * default encoding.  You must Make sure to supply the correct
     * encoding for this string.
     *
     * @param s The string with the given encoding that needs to be converted to the default encoding.
     * @param encoding The (IANA) name for the encoding.
     * @return The same string, but in the default platform encoding.
     * @see #convertFromDefault(String, String)
     * @see #convertToDefault(char, String)
     */
    public static String convertToDefault (String s, String encoding)
    {
        try
        {
            return new String(s.getBytes(encoding));
        }
        catch (Throwable e)
        {
            // unknown encoding
            // System.out.println("Unknown encoding: " + encoding);
            return s;
        }
    }


    /**
     * Convert a character in the given encoding to the default
     * platform encoding.
     *
     * @param character The character with the given encoding.
     * @param encoding The encoding of the given character.
     * @return The same character, but in the default platform encoding.
     * @see #convertToDefault(String, String)
     */
    public static char convertToDefault (char character, String encoding)
    {
        String str = convertToDefault("" + character, encoding);
        if (str.length() > 0)
        {
            return str.charAt(0);
        }
        else
        {
            return character;
        }
    }


    /**
     * Compares one byte array to the other and returns true if they
     * are identical in length and value.
     */
    public static boolean compare (byte[] a, byte[] b)
    {
        int length = a.length;
        if (length != b.length)
            return false;
        for (int index = 0; index < length; index++)
        {
            if (a[index] != b[index])
                return false;
        }
        return true;
    }

    /////////////////////////////////////////
    ///// AVAILABLE CHARACTER ENCODINGS /////
    /////////////////////////////////////////

    /**
     * Get the name of the given encoding code.  The case of the
     * given code is ignored.  It returns null if the code is not
     * found.<p> <b>We do not use the IANA names for the encoding
     * names, since these names try to be more descriptive.</b> That
     * is why the encoding of a widget cannot be set using the
     * encoding names; to do that the encoding <i>codes</i> must be
     * used. These encoding codes are the codes for each JDK character
     * converter as developed by JavaSoft.
     * @see #getEncodingCode
     */
    public static String getEncodingName (String code)
    {
        // get the names, go through them and if one returns the same
        // code, return that name as the resulting encoding name.
        Enumeration names = encodingTable.keys();
        while (names.hasMoreElements())
        {
            String name = (String) names.nextElement();
            String currentCode = (String) encodingTable.get(name);
            if (currentCode.equalsIgnoreCase(code))
            {
                return name;
            }
        }
        return null;
    }


    /**
     * Get the code of the given encoding name.  The case of the given
     * name is ignored.  It returns null if the name is not found.
     * <b>The encoding code must be used to set the encoding of a
     * widget properly.</b>
     *
     * @see #getEncodingName
     * @see #getEncodingCodes
     */
    public static String getEncodingCode (String name)
    {
        String code = (String) encodingTable.get(name);

        // try to find it ignoring the case
        if (code == null)
        {
            Enumeration names = encodingTable.keys();
            while (names.hasMoreElements())
            {
                String currentName = (String) names.nextElement();
                if (currentName.equalsIgnoreCase(name))
                {
                    code = (String) encodingTable.get(currentName);
                    break;
                }
            }
        }
        return code;
    }


    /**
     * Get all the available encoding names.
     *
     * @return A list of the encoding names.
     * @see #getEncodingName
     */
    public static String[] getEncodingNames ()
    {
        Vector result = new Vector();
        Enumeration names = encodingTable.keys();
        while (names.hasMoreElements())
        {
            String currentName = (String) names.nextElement();
            result.addElement(currentName);
        }
        String[] array = new String[result.size()];
        result.copyInto(array);
        return array;
    }


    /**
     * Get all the available encoding codes.
     *
     * @return A sorted list of the encoding codes.
     * @see #getEncodingCode
     */
    public static String[] getEncodingCodes ()
    {
        String[] names = getEncodingNames();
        String[] result = new String[names.length];
        for (int i = 0; i < names.length; i++)
        {
            result[i] = getEncodingCode(names[i]);
        }
        return result;
    }


    /**
     * Get all supported locale names.
     *
     * @return A list of supported locale names.
     * @see #getEncodingNames
     */
    public static String[] getLocaleNames ()
    {
        Locale[] localeList = java.text.Collator.getAvailableLocales();//(new java.text.resources.LocaleData()).getAvailableLocales(null);
        String[] names = new String[localeList.length];
        for (int i = 0; i < localeList.length; i++)
        {
            names[i] = localeList[i].getDisplayName() + "[" + localeList[i].toString() + "]";
        }
        return names;
    }


    /**
     * The encoding table, with the name for each encoding and
     * for each name the proper encoding code that is the code
     * for the JDK character encoding class.
     */
    private static Hashtable encodingTable = new Hashtable();


    static
    {
        putEncoding("ISO Latin-1", "8859_1");
        putEncoding("ISO Latin-2", "8859_2");
        putEncoding("ISO Latin-3", "8859_3");
        putEncoding("ISO Latin-4", "8859_4");
        putEncoding("ISO Latin/Cyrillic", "8859_5");
        putEncoding("ISO Latin/Arabic", "8859_6");
        putEncoding("ISO Latin/Greek", "8859_7");
        putEncoding("ISO Latin/Hebrew", "8859_8");
        putEncoding("ISO Latin-5", "8859_9");
        putEncoding("Windows East Europe", "Cp1250");
        putEncoding("Windows Cyrillic", "Cp1251");
        putEncoding("Windows West Europe", "Cp1252");
        putEncoding("Windows Greek", "Cp1253");
        putEncoding("Windows Turkish", "Cp1254");
        putEncoding("Windows Hebrew", "Cp1255");
        putEncoding("Windows Arabic", "Cp1256");
        putEncoding("Windows Baltic", "Cp1257");
        putEncoding("Windows Vietnamese", "Cp1258");
        putEncoding("PC Original", "Cp437");
        putEncoding("PC Greek", "Cp737");
        putEncoding("PC Baltic", "Cp775");
        putEncoding("PC Latin-1", "Cp850");
        putEncoding("PC Latin-2", "Cp852");
        putEncoding("PC Cyrillic", "Cp855");
        putEncoding("PC Turkish", "Cp857");
        putEncoding("PC Portuguese", "Cp860");
        putEncoding("PC Icelandic", "Cp861");
        putEncoding("PC Hebrew", "Cp862");
        putEncoding("PC Canadian French", "Cp863");
        putEncoding("PC Arabic", "Cp864");
        putEncoding("PC Nordic", "Cp865");
        putEncoding("PC Russian", "Cp866");
        putEncoding("PC Modern Greek", "Cp869");
        putEncoding("Windows Thai", "Cp874");
        putEncoding("Japanese EUC", "EUCJIS");
        putEncoding("Macintosh Arabic", "MacArabic");
        putEncoding("JIS", "JIS");//"LIS");
        putEncoding("Macintosh Latin-2", "MacCentralEurope");
        putEncoding("Macintosh Croation", "MacCroatian");
        putEncoding("Macintosh Cyrillic", "MacCyrillic");
        putEncoding("Macintosh Dingbat", "MacDingbat");
        putEncoding("Macintosh Greek", "MacGreek");
        putEncoding("Macintosh Hebrew", "MacHebrew");
        putEncoding("Macintosh Iceland", "Iceland");
        putEncoding("Macintosh Roman", "MacRoman");
        putEncoding("Macintosh Romania", "MacRomania");
        putEncoding("Macintosh Symbol", "MacSymbol");
        putEncoding("Macintosh Thai", "MacThai");
        putEncoding("Macintosh Turkish", "MacTurkish");
        putEncoding("Macintosh Ukraine", "MacUkraine");
        putEncoding("PC-Windows Japanese", "SJIS");
        putEncoding("Standard UTF-8", "UTF8");
    }


    /** Add an encoding name/value pair to the encoding hashtable. */
    private static void putEncoding (String s1, String s2)
    {
        encodingTable.put(s1 + " [" + s2 + "]", s2);
    }
}
