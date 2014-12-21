//
// -----------------------------------------------------------------------------------
// Source file: BasicPropertyParser.java
//
// PACKAGE: com.cboe.domain.property;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.property;

import java.util.*;

import com.cboe.interfaces.domain.Delimeter;

public class BasicPropertyParser 
{
    /**
     *  Parse the value and break out the value into
     *  a list of values.  The list is assumed to
     *  be separated by a delimeter.
     *  @param value The value to parse
     *  @return The list of parsed values
     */
    public static List parseList(String value)
    {
        return Arrays.asList(parseArray(value));
    }

    /**
     * Parse the value and break out the value into an array of values. The value is assumed to be separated by a
     * delimeter.
     * @param value The value to parse
     * @return Array of value elements
     */
    public static String[] parseArray(String value)
    {
        String[] elements = new String[0];

        if(value != null)
        {
            elements = split( value, Delimeter.PROPERTY_DELIMETER );
        }

        return elements;
    }

    /**
     *  Parse out the base name of the definition from the name string.
     *  @param definitionName The name of the defintion to parse
     *  @return The base name of the definition 
     */
    public static String parseDefinitionBaseName(String definitionName)
    {
        String[] nameArray = parseArray(definitionName);
        String[] nameArrayMinusType = new String[nameArray.length - 1];
        System.arraycopy(nameArray, 0, nameArrayMinusType, 0, nameArrayMinusType.length);
        return buildCompoundString(nameArrayMinusType);
    }

    /**
     *  Parse out the defintion type from the name string.
     *  @param definitionName The name of the defintion to parse
     *  @return The type of definition
     */
    public static String parseDefinitionType(String definitionName)
    {
        String[] nameList = parseArray(definitionName);
        return nameList[nameList.length - 1];
    }

    /**
     * Builds a compound String using the delimiter to connect all elements together.
     * @param elements to build into a compound name. StringBuffer.append(Object) will be used to determine the
     * String representation of each element.
     * @return compound name
     */
    public static String buildCompoundString(Object[] elements)
    {
        StringBuffer compoundString = new StringBuffer(25 * elements.length);
        for(int i = 0; i < elements.length; i++)
        {
            Object element = elements[i];
            compoundString.append(element);
            if(i < (elements.length - 1))
            {
                compoundString.append(Delimeter.PROPERTY_DELIMETER);
            }
        }
        return compoundString.toString();
    }

    /**
     * Splits a delimited string into an array of strings base on delimiter
     * NOTE:  modeled on the String.split() method available in jdk 1.4
     *          but not available at this writing.
     */
    public static String[] split( String str, char x )
    {
        ArrayList v = new ArrayList();
        String str1 = new String();

        for(int i = 0; i < str.length(); i++)
        {
            if(str.charAt(i) == x)
            {
                v.add(str1);
                str1 = new String();
            }
            else
            {
                str1 += str.charAt(i);
            }
        }
        v.add(str1);
        String array[];
        array = new String[v.size()];
        for(int i = 0; i < array.length; i++)
        {
            array[i] = new String((String) v.get(i));
        }
        return array;
    }
}
