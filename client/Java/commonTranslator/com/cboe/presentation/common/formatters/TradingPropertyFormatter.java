//
// -----------------------------------------------------------------------------------
// Source file: TradingPropertyFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.domain.property.PropertyDefinition;
import com.cboe.interfaces.domain.tradingProperty.TradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyGroup;
import com.cboe.interfaces.presentation.common.formatters.TradingPropertyFormatStrategy;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

public class TradingPropertyFormatter extends Formatter implements TradingPropertyFormatStrategy
{
    public TradingPropertyFormatter()
    {
        addStyle(PROPERTY_PROPER_STYLE_NAME, PROPERTY_PROPER_STYLE_DESCRIPTION);
        addStyle(NAME_VALUE_PAIR_STYLE_NAME, NAME_VALUE_PAIR_STYLE_DESCRIPTION);
        addStyle(PROPER_NAME_VALUE_PAIR_STYLE_NAME, PROPER_NAME_VALUE_PAIR_STYLE_DESCRIPTION);
        addStyle(PROPERTY_DEFINITION_NAME_VALUE_STYLE_NAME, PROPERTY_DEFINITION_NAME_VALUE_STYLE_DESCRIPTION);
        addStyle(PROPERTY_DEFINITION_PROPER_NAME_STYLE_NAME, PROPERTY_DEFINITION_PROPER_NAME_STYLE_DESCRIPTION);
        addStyle(PROPERTY_DEFINITION_VALUE_STYLE_NAME, PROPERTY_DEFINITION_VALUE_STYLE_DESCRIPTION);

        setDefaultStyle(NAME_VALUE_PAIR_STYLE_NAME);
    }

    /**
     * Determines the default style when calling format for PropertyDescriptor's
     * @return default style for PropertyDescriptor's
     */
    public String getDefaultStyleForPropertyDescriptor()
    {
        return getDefaultStyle();
    }

    /**
     * Determines the default style when calling format for TradingProperty's
     * @return default style for TradingProperty's
     */
    public String getDefaultStyleForTradingProperty()
    {
        return getDefaultStyle();
    }

    /**
     * Determines the default style when calling format for TradingPropertyGroup's
     * @return default style for TradingPropertyGroup's
     */
    public String getDefaultStyleForTradingPropertyGroup()
    {
        return getDefaultStyle();
    }

    /**
     * Attempts to format the passed PropertyDescriptor using the default style returned by
     * getDefaultStyleForPropertyDescriptor().
     * @param propertyDescriptor to format
     * @param tradingProperty that will be needed to resolve certain style formats.
     * @return String representation of the PropertyDescriptor
     */
    public String format(PropertyDescriptor propertyDescriptor, TradingProperty tradingProperty)
    {
        return format(propertyDescriptor, tradingProperty, getDefaultStyleForPropertyDescriptor());
    }

    /**
     * Attempts to format the passed PropertyDescriptor using the passed style.
     * @param propertyDescriptor to format
     * @param tradingProperty that may be needed to resolve certain style formats. May be null if the style does not
     * require it.
     * @param styleName style to use for formatting
     * @return String representation of a PropertyDescriptor.
     */
    public String format(PropertyDescriptor propertyDescriptor, TradingProperty tradingProperty, String styleName)
    {
        String formattedObject;

        if(propertyDescriptor == null)
        {
            throw new IllegalArgumentException("PropertyDescriptor may not be null.");
        }

        validateStyleName(styleName);

        if(PROPERTY_PROPER_STYLE_NAME.equals(styleName))
        {
            String displayName = propertyDescriptor.getDisplayName();
            displayName = formatEnglishCase(displayName);
            formattedObject = displayName;
        }
        else if(NAME_VALUE_PAIR_STYLE_NAME.equals(styleName) ||
                PROPER_NAME_VALUE_PAIR_STYLE_NAME.equals(styleName))
        {
            if(propertyDescriptor instanceof IndexedPropertyDescriptor)
            {
                formattedObject = format((IndexedPropertyDescriptor)propertyDescriptor, tradingProperty, styleName);
            }
            else
            {
                validateTradingProperty(tradingProperty, styleName);

                StringBuffer buffer = new StringBuffer(100);

                String displayName = getPropertyDescriptorName(propertyDescriptor, styleName);
                buffer.append(displayName);

                buffer.append('=');

                Object value = getPropertyDescriptorValue(propertyDescriptor, tradingProperty);
                if(value != null)
                {
                    buffer.append(value.toString());
                }

                formattedObject = buffer.toString();
            }
        }
        else if(PROPERTY_DEFINITION_NAME_VALUE_STYLE_NAME.equals(styleName))
        {
            if(propertyDescriptor instanceof IndexedPropertyDescriptor)
            {
                formattedObject = format((IndexedPropertyDescriptor) propertyDescriptor, tradingProperty, styleName);
            }
            else
            {
                validateTradingProperty(tradingProperty, styleName);

                StringBuffer buffer = new StringBuffer(100);

                String displayName = getPropertyDefinitionName(propertyDescriptor, tradingProperty);
                buffer.append(displayName);

                buffer.append('=');
                buffer.append(format(propertyDescriptor, tradingProperty, PROPERTY_DEFINITION_VALUE_STYLE_NAME));

                formattedObject = buffer.toString();
            }
        }
        else if(PROPERTY_DEFINITION_VALUE_STYLE_NAME.equals(styleName))
        {
            if(propertyDescriptor instanceof IndexedPropertyDescriptor)
            {
                formattedObject = format((IndexedPropertyDescriptor) propertyDescriptor, tradingProperty, styleName);
            }
            else
            {
                validateTradingProperty(tradingProperty, styleName);

                StringBuffer buffer = new StringBuffer(100);

                Object value = getPropertyDescriptorValue(propertyDescriptor, tradingProperty);
                Object displayValue = getPropertyDefinitionDisplayValue(value, tradingProperty,
                                                                        propertyDescriptor.getDisplayName());
                if(displayValue != null)
                {
                    buffer.append(displayValue.toString());
                }
                else
                {
                    buffer.append(value.toString());
                }

                formattedObject = buffer.toString();
            }
        }
        else if(PROPERTY_DEFINITION_PROPER_NAME_STYLE_NAME.equals(styleName))
        {
            validateTradingProperty(tradingProperty, styleName);
            String displayName = getPropertyDefinitionName(propertyDescriptor, tradingProperty);
            displayName = formatEnglishCase(displayName);
            formattedObject = displayName;
        }
        else
        {
            formattedObject = INVALID_STYLE_FORMAT;
        }

        return formattedObject;
    }

    /**
     * Attempts to format the passed TradingProperty using the default style returned by
     * getDefaultStyleForTradingProperty().
     * @param tradingProperty to format
     * @return String representation of the TradingProperty
     */
    public String format(TradingProperty tradingProperty)
    {
        return format(tradingProperty, getDefaultStyleForTradingProperty());
    }

    /**
     * Attempts to format the passed TradingProperty using the passed style.
     * @param tradingProperty to format
     * @param styleName style to use for formatting
     * @return String representation of a PropertyDescriptor.
     */
    public String format(TradingProperty tradingProperty, String styleName)
    {
        String formattedObject;

        if(tradingProperty == null)
        {
            throw new IllegalArgumentException("TradingProperty may not be null.");
        }

        validateStyleName(styleName);

        if(PROPERTY_PROPER_STYLE_NAME.equals(styleName))
        {
            String displayName = tradingProperty.getPropertyName();
            formattedObject = formatEnglishCase(displayName);
        }
        else if(NAME_VALUE_PAIR_STYLE_NAME.equals(styleName) ||
                PROPER_NAME_VALUE_PAIR_STYLE_NAME.equals(styleName) ||
                PROPERTY_DEFINITION_NAME_VALUE_STYLE_NAME.equals(styleName) ||
                PROPERTY_DEFINITION_VALUE_STYLE_NAME.equals(styleName))
        {
            StringBuffer buffer = new StringBuffer(300);

            try
            {
                PropertyDescriptor[] propertyDescriptors = tradingProperty.getPropertyDescriptors();

                for(int i = 0; i < propertyDescriptors.length; i++)
                {
                    PropertyDescriptor propertyDescriptor = propertyDescriptors[i];

                    buffer.append(format(propertyDescriptor, tradingProperty, styleName));
                    if(i < (propertyDescriptors.length - 1))
                    {
                        buffer.append(", ");
                    }
                }
            }
            catch(IntrospectionException e)
            {
                DefaultExceptionHandlerHome.find().process(e, "Exception trying to format TradingProperty.");
                buffer.append("Exception Occurred");
            }

            formattedObject = buffer.toString();
        }
        else
        {
            formattedObject = INVALID_STYLE_FORMAT;
        }

        return formattedObject;
    }

    /**
     * Attempts to format the passed TradingPropertyGroup using the default style returned by
     * getDefaultStyleForTradingPropertyGroup().
     * @param tradingPropertyGroup to format
     * @return String representation of the TradingPropertyGroup
     */
    public String format(TradingPropertyGroup tradingPropertyGroup)
    {
        return format(tradingPropertyGroup, getDefaultStyleForTradingPropertyGroup());
    }

    /**
     * Attempts to format the passed TradingPropertyGroup using the passed style.
     * @param tradingPropertyGroup to format
     * @param styleName style to use for formatting
     * @return String representation of a PropertyDescriptor.
     */
    public String format(TradingPropertyGroup tradingPropertyGroup, String styleName)
    {
        String formattedObject;

        if(tradingPropertyGroup == null)
        {
            throw new IllegalArgumentException("TradingPropertyGroup may not be null.");
        }

        validateStyleName(styleName);

        if(PROPERTY_PROPER_STYLE_NAME.equals(styleName))
        {
            String displayName = tradingPropertyGroup.getTradingPropertyType().getFullName();
            formattedObject = displayName;
        }
        else if(NAME_VALUE_PAIR_STYLE_NAME.equals(styleName) ||
                PROPER_NAME_VALUE_PAIR_STYLE_NAME.equals(styleName) ||
                PROPERTY_DEFINITION_NAME_VALUE_STYLE_NAME.equals(styleName) ||
                PROPERTY_DEFINITION_VALUE_STYLE_NAME.equals(styleName))
        {
            StringBuffer buffer = new StringBuffer(500);

            TradingProperty[] tradingProperties = tradingPropertyGroup.getAllTradingProperties();

            for(int i = 0; i < tradingProperties.length; i++)
            {
                TradingProperty tradingProperty = tradingProperties[i];

                buffer.append(format(tradingProperty, styleName));
                if(i < (tradingProperties.length - 1))
                {
                    buffer.append("; ");
                }
            }

            formattedObject = buffer.toString();
        }
        else
        {
            formattedObject = INVALID_STYLE_FORMAT;
        }

        return formattedObject;
    }

    private String formatEnglishCase(String displayName)
    {
        StringBuffer buffer = new StringBuffer(displayName.length() + 10);
        char[] chars = displayName.toCharArray();
        buffer.append(Character.toUpperCase(chars[0]));
        for(int i = 1; i < chars.length; i++)
        {
            char aChar = chars[i];
            if(i > 0)
            {
                char prevChar = chars[i - 1];
                if(Character.isUpperCase(aChar) && !Character.isUpperCase(prevChar) && prevChar != ' ')
                {
                    buffer.append(' ');
                }
            }
            buffer.append(aChar);
        }

        return buffer.toString();
    }

    /**
     * Attempts to format the passed IndexedPropertyDescriptor using the passed style. All indices will be visited and
     * formatted.
     * @param propertyDescriptor to format
     * @param tradingProperty that may be needed to resolve certain style formats. May be null if the style does not
     * require it.
     * @param styleName style to use for formatting
     * @return String representation of a PropertyDescriptor.
     */
    private String format(IndexedPropertyDescriptor propertyDescriptor, TradingProperty tradingProperty,
                          String styleName)
    {
        String formattedObject;

        if(propertyDescriptor == null)
        {
            throw new IllegalArgumentException("IndexedPropertyDescriptor may not be null.");
        }

        validateStyleName(styleName);

        if(NAME_VALUE_PAIR_STYLE_NAME.equals(styleName) ||
           PROPER_NAME_VALUE_PAIR_STYLE_NAME.equals(styleName))
        {
            validateTradingProperty(tradingProperty, styleName);

            StringBuffer buffer = new StringBuffer(100);

            String displayName = getPropertyDescriptorName(propertyDescriptor, styleName);
            buffer.append(displayName);

            buffer.append("=(");

            Object values = getPropertyDescriptorValue(propertyDescriptor, tradingProperty);
            if(values != null)
            {
                for(int i = 0; i < Array.getLength(values); i++)
                {
                    if(i > 0)
                    {
                        buffer.append(", ");
                    }

                    Object value = Array.get(values, i);
                    if(value != null)
                    {
                        buffer.append(value.toString());
                    }
                    else
                    {
                        buffer.append("null");
                    }
                }
            }

            buffer.append(')');
            formattedObject = buffer.toString();
        }
        else if(PROPERTY_DEFINITION_NAME_VALUE_STYLE_NAME.equals(styleName))
        {
            validateTradingProperty(tradingProperty, styleName);

            StringBuffer buffer = new StringBuffer(100);

            String displayName = getPropertyDefinitionName(propertyDescriptor, tradingProperty);
            buffer.append(displayName);

            buffer.append("=(");
            buffer.append(format(propertyDescriptor, tradingProperty, PROPERTY_DEFINITION_VALUE_STYLE_NAME));
            buffer.append(')');
            formattedObject = buffer.toString();
        }
        else if(PROPERTY_DEFINITION_VALUE_STYLE_NAME.equals(styleName))
        {
            validateTradingProperty(tradingProperty, styleName);

            StringBuffer buffer = new StringBuffer(100);

            Object values = getPropertyDescriptorValue(propertyDescriptor, tradingProperty);
            if(values != null && Array.getLength(values) > 0)
            {
                for(int i = 0; i < Array.getLength(values); i++)
                {
                    if(i > 0)
                    {
                        buffer.append(", ");
                    }

                    Object value = Array.get(values, i);
                    Object displayValue = getPropertyDefinitionDisplayValue(value, tradingProperty,
                                                                            propertyDescriptor.getDisplayName());
                    if(displayValue != null)
                    {
                        buffer.append(displayValue.toString());
                    }
                    else
                    {
                        buffer.append(value.toString());
                    }
                }
            }
            else
            {
                buffer.append(NO_VALUE);
            }

            formattedObject = buffer.toString();
        }
        else
        {
            formattedObject = INVALID_STYLE_FORMAT;
        }

        return formattedObject;
    }

    private Object getPropertyDescriptorValue(PropertyDescriptor propertyDescriptor, TradingProperty tradingProperty)
    {
        Object returnValue = null;
        try
        {
            returnValue = tradingProperty.getFieldValue(propertyDescriptor);
        }
        catch(IllegalAccessException e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
        catch(InvocationTargetException e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
        return returnValue;
    }

    private String getPropertyDescriptorName(PropertyDescriptor propertyDescriptor, String styleName)
    {
        String displayName = propertyDescriptor.getDisplayName();
        if(PROPER_NAME_VALUE_PAIR_STYLE_NAME.equals(styleName))
        {
            displayName = formatEnglishCase(displayName);
        }
        return displayName;
    }

    private String getPropertyDefinitionName(PropertyDescriptor propertyDescriptor, TradingProperty tradingProperty)
    {
        String displayName = propertyDescriptor.getName();
        try
        {
            PropertyDefinition definition = tradingProperty.getPropertyDefinition(displayName);
            if(definition != null)
            {
                displayName = definition.getDisplayName();
                if(displayName == null || displayName.length() == 0)
                {
                    displayName = propertyDescriptor.getDisplayName();
                }
            }
            else
            {
                displayName = propertyDescriptor.getDisplayName();
            }
        }
        catch(NotFoundException e)
        {
            displayName = propertyDescriptor.getDisplayName();
        }
        return displayName;
    }
    
    private Object getPropertyDefinitionDisplayValue(Object value, TradingProperty tradingProperty,
                                                     String propertyDefinitionFieldName)
    {
        Object returnValue;

        try
        {
            PropertyDefinition definition = tradingProperty.getPropertyDefinition(propertyDefinitionFieldName);
            if(definition != null)
            {
                Object definitionValue = definition.getDisplayValueForPossibleValue(value);
                if(definitionValue != null)
                {
                    returnValue = definitionValue.toString() + " (" + value.toString() + ')';
                }
                else
                {
                    returnValue = value.toString();
                }
            }
            else
            {
                returnValue = value.toString();
            }
        }
        catch(NotFoundException e)
        {
            returnValue = value.toString();
        }
        return returnValue;
    }

    private void validateStyleName(String styleName)
    {
        if(styleName == null || styleName.length() == 0)
        {
            throw new IllegalArgumentException("styleName may not be null or empty.");
        }
    }

    private void validateTradingProperty(TradingProperty tradingProperty, String styleName)
    {
        if(tradingProperty == null)
        {
            throw new IllegalArgumentException("TradingProperty may not be null for the " +
                                               styleName + " style.");
        }
    }
}
