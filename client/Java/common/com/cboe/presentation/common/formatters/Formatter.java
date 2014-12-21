//
// -----------------------------------------------------------------------------------
// Source file: Formatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import java.util.*;

import com.cboe.interfaces.presentation.common.formatters.FormatStrategy;
import com.cboe.presentation.common.formatters.DuplicateStyleException;

/**
 * Starts a common implementation of a FormatStrategy.
 */
public abstract class Formatter implements FormatStrategy
{
    private Map stylesMap = null;
    private String defaultStyle = "";
/**
 * Formatter constructor comment.
 */
public Formatter()
{
    super();
    stylesMap = new HashMap();
}
    protected void addField(StringBuffer buffer, String fieldText, int maxLength, int padding, boolean splitWords, boolean appendSpace)
    {
        int sizeFieldAdded = addFieldWithSizeLimit(buffer, fieldText, maxLength, false);
        if(appendSpace)
        {
            appendSpace(buffer, maxLength + padding - sizeFieldAdded);
        }
    }
/**
 * Adds the contents of the fieldText.
 * @param buffer to add fieldText to.
 * @param fieldText to add
 * @param maxLength of characters to add. If fieldText is longer than
 * maxLength, the field will be truncated.
 * @param splitWords If fieldText is greater than maxLength, and true, truncation
 * will occur between words at maxlength or less, if false truncation will occur exactly
 * at maxLength.
 * @return number of characters that were added.
 */
protected int addFieldWithSizeLimit(StringBuffer buffer, String fieldText, int maxLength, boolean splitWords)
{
    int charAdded = 0;

    if(fieldText.length() > maxLength)
    {
        int charToAdd = 0;

        if(splitWords)
        {
            char[] characters = fieldText.substring(0, maxLength).toCharArray();

            for(int i = characters.length - 1; i > 0; i--)
            {
                if(Character.isWhitespace(characters[i]))
                {
                    charToAdd = i;
                    break;
                }
            }
        }

        if(charToAdd > 0)
        {
            buffer.append(fieldText.substring(0, charToAdd));
            charAdded = charToAdd;
        }
        else
        {
            buffer.append(fieldText.substring(0, maxLength));
            charAdded = maxLength;
        }
    }
    else
    {
        charAdded = fieldText.length();
        buffer.append(fieldText);
    }

    return charAdded;
}
/**
 * Adds a new style if the styleName did not already exist.
 * @param styleName
 * @param styleDescription
 * @exception java.lang.IllegalArgumentException Either styleName or styleDescription was null.
 * @exception com.cboe.presentation.common.formatters.DuplicateStyleException You tried to create a style that already exists.
 */
protected void addStyle(String styleName, String styleDescription) throws IllegalArgumentException, DuplicateStyleException
{
    if(styleName == null || styleDescription == null)
    {
        throw new IllegalArgumentException("styleName or styleDescription not allowed to be null.");
    }

    if(containsStyle(styleName))
    {
        DuplicateStyleException exception = new DuplicateStyleException("Style already exists.");
        exception.styleName = styleName;
        exception.styleDescription = (String)stylesMap.get(styleName);

        throw exception;
    }

    stylesMap.put(styleName, styleDescription);
}
/**
 * Appends spaces to the StringBuffer.
 * @param buffer to add spaces to
 * @param count of number of spaces to add
 * @return StringBuffer original StringBuffer returned with added spaces.
 */
protected StringBuffer appendSpace(StringBuffer buffer, int count)
{
    if(count > 0)
    {
        buffer.ensureCapacity(buffer.length() + count);

        char[] spaces = new char[count];
        for(int i = 0; i < count; i++)
        {
            spaces[i] = ' ';
        }
        buffer.append(spaces);
    }

    return buffer;
}
/**
 * Determines if the passed style is already present.
 * @param styleName to test.
 */
public boolean containsStyle(String styleName)
{
    return stylesMap.containsKey(styleName);
}

/**
 * Gets the default formatting style.
 * @return default style.
 */
public String getDefaultStyle()
{
    return defaultStyle;
}

/**
 * Returns a map of format styles.
 * The map keys will be the format styles, the map values will
 * be descriptions of the format styles. This map is unmodifiable.
 * @return java.util.Map
 */
public Map getFormatStyles()
{
    return Collections.unmodifiableMap(stylesMap);
}
/**
 * Removes a style.
 * @param styleName to remove.
 * @exception java.lang.IllegalArgumentException styleName was null.
 */
protected void removeStyle(String styleName) throws IllegalArgumentException
{
    if(styleName == null)
    {
        throw new IllegalArgumentException("styleName not allowed to be null.");
    }

    if(getDefaultStyle().equalsIgnoreCase(styleName))
    {
        throw new IllegalStateException("Cannot remove default style.");
    }

    stylesMap.remove(styleName);
}

/**
 * Sets the default formatting style.
 * @param styleName of style to use as default style from this point on.
 */
protected void setDefaultStyle(String styleName) throws IllegalArgumentException
{
    if(styleName == null || !containsStyle(styleName))
    {
        throw new IllegalArgumentException("Invalid styleName : "+styleName);
    }

    defaultStyle = styleName;
}
/**
 * Validates that style is not null and contained in the Style Map
 * @param styleName of style to validate
 * @throws java.lang.IllegalArgumentException if style is invalid
 */
protected void validateStyle(String styleName) throws IllegalArgumentException
{
    if(styleName == null || !containsStyle(styleName))
    {
        throw new IllegalArgumentException("Invalid styleName : "+styleName);
    }
}

}
