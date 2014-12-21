package com.cboe.domain.routingProperty.key;
// -----------------------------------------------------------------------------------
// Source file: AbstractBasePropertyKey
//
// PACKAGE: com.cboe.domain.firmRoutingProperty.test.key
// 
// Created: Jul 21, 2006 9:26:00 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.firm.FirmStruct;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.MutableBasePropertyKey;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.routingProperty.BasePropertyTypeImpl;
import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.routingProperty.common.SimpleComplexProductClass;
import com.cboe.domain.routingProperty.common.TradingSessionName;

/**
 * This provides a key for RoutingPropertyGroups.  The "base" elements of the
 * key are trading session name, exchange acronym, and firm number.  The
 * subclasses will add any elements necessary to define their specific unique
 * key.
 *
 * The property name is always the last element in the key.
 */
public abstract class AbstractBasePropertyKey implements MutableBasePropertyKey
{
    public static final String FIRM_PROPERTY_NAME = "firm";
    public static final String TRADING_SESSION_PROPERTY_NAME = "tradingSession";
    public static final String EXCHANGE_NAME = "exchange";  // not a property. part of firm-exchange. needed to find relevant position within mask

    public static final int SESSION_KEY_POSITION  = RoutingKeyHelper.SESSION_KEY_POSITION;
    public static final int FIRM_KEY_POSITION     = RoutingKeyHelper.FIRM_KEY_POSITION;
    public static final int EXCHANGE_KEY_POSITION = RoutingKeyHelper.EXCHANGE_KEY_POSITION;

    protected String propertyName;

    protected String firmNumber;
    protected String exchangeAcronym;
    protected ExchangeFirmStruct exchangeFirmStruct;

    protected String propertyKey;

    private BeanInfo myBeanInfo;
    private PropertyDescriptor[] myPropertyDescriptors;

    protected FirmStruct firmStruct;
    protected TradingSessionName tradingSessionName;

    protected PropertyKeyElementDefinition propertyKeyElementDefinition;

    private Map<String, Object> defaultValues = null;

    protected AbstractBasePropertyKey(BasePropertyType type)
    {
        this(type.getName(), "", "", "");
    }

    protected AbstractBasePropertyKey(BasePropertyType type, String sessionName, String firmNumber, String exchangeAcronym)
    {
        this(type.getName(), sessionName, firmNumber, exchangeAcronym);
    }

    protected AbstractBasePropertyKey(String propertyName, String sessionName, String firmNumber, String exchangeAcronym)
    {
        this.propertyName = propertyName;
        this.tradingSessionName = new TradingSessionName(sessionName);
        this.firmNumber = firmNumber;
        this.exchangeAcronym = exchangeAcronym;

        BasePropertyType type = BasePropertyTypeImpl.findPropertyType(propertyName);
        propertyKeyElementDefinition = new PropertyKeyElementDefinition(this, type);
    }

    protected AbstractBasePropertyKey(String propertyKey) throws DataValidationException
    {
        parsePropertyKey(propertyKey);

        BasePropertyType type = BasePropertyTypeImpl.findPropertyType(propertyName);
        propertyKeyElementDefinition = new PropertyKeyElementDefinition(this, type);
    }

    /**
     * Parses the propertyKey to find the separate key values.
     *
     * Returns the index of the last key value used from the propertyKey's
     * parts (does not count the index of propertyName, which is always the
     * last part of the propertyKey).
     *
     * @param propertyKey
     * @throws DataValidationException
     */
    protected int parsePropertyKey(String propertyKey) throws DataValidationException
    {
        String[] keyElements = splitPropertyKey(propertyKey);

        this.tradingSessionName = new TradingSessionName(getKeyElement(keyElements, RoutingKeyHelper.SESSION_KEY_POSITION));
        this.firmNumber = getKeyElement(keyElements, RoutingKeyHelper.FIRM_KEY_POSITION);
        this.exchangeAcronym = getKeyElement(keyElements, RoutingKeyHelper.EXCHANGE_KEY_POSITION);

        this.propertyName = getKeyElement(keyElements, keyElements.length - 1);

        return 2;
    }

    /**
     * @return size of the mask corresponding to the key components represented by this class. subclasses add to this size
     * based on the number of additional key components that they provide
     */
    protected int getMaskSize()
    {
        return 3;
    }

    /**
     * @param keyElement
     * @return index position within the mask array that corresponds to the keyElement passed in
     */
    public int getMaskIndex(String keyElement)
    {
        int index;
        if (keyElement.equalsIgnoreCase(TRADING_SESSION_PROPERTY_NAME))
        {
            index = SESSION_KEY_POSITION;
        }
        else if (keyElement.equalsIgnoreCase(FIRM_PROPERTY_NAME))
        {
            index = FIRM_KEY_POSITION;
        }
        else if(keyElement.equalsIgnoreCase(EXCHANGE_NAME))
        {
            index = EXCHANGE_KEY_POSITION;
        }
        else
        {
            index = -1;
        }
        return index;
    }

    public String getKeyComponentName(int maskIndex)
    {
        String fieldName = "Unknown Field Name";
        switch (maskIndex)
        {
            case SESSION_KEY_POSITION:
                fieldName = TRADING_SESSION_PROPERTY_NAME;
                break;
            case FIRM_KEY_POSITION:
            case EXCHANGE_KEY_POSITION:
                fieldName = FIRM_PROPERTY_NAME;
                break;
        }
        return fieldName;
    }

    public Object clone() throws CloneNotSupportedException
    {
        AbstractBasePropertyKey key = (AbstractBasePropertyKey) super.clone();
        key.tradingSessionName = new TradingSessionName(this.tradingSessionName.sessionName);
        key.firmNumber= this.firmNumber;
        key.exchangeAcronym = this.exchangeAcronym;

        key.propertyKeyElementDefinition = (PropertyKeyElementDefinition) propertyKeyElementDefinition.clone();

        return key;
    }

    public String getPropertyName()
    {
        return propertyName;
    }

    public String getFirmNumber()
    {
        return firmNumber;
    }

    public String getSessionName()
    {
        return tradingSessionName == null ? "" : tradingSessionName.sessionName;
    }

    public String getExchangeAcronym()
    {
        return exchangeAcronym;
    }

    public ExchangeFirmStruct getExchangeFirmStruct()
    {
        if(exchangeFirmStruct == null)
        {
            exchangeFirmStruct = new ExchangeFirmStruct(getExchangeAcronym(), getFirmNumber());
        }
        return exchangeFirmStruct;
    }

    public String getPropertyKey()
    {
        if (propertyKey == null)
        {
            propertyKey = createPropertyKey();
        }

        return propertyKey;
    }

    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof BasePropertyKey))
        {
            return false;
        }

        boolean isEqual = super.equals(obj);
        if (!isEqual)
        {
            BasePropertyKey key = (BasePropertyKey) obj;
            isEqual = this.getPropertyKey().equals(key.getPropertyKey());
        }

        return isEqual;
    }

    public int hashCode()
    {
        return getPropertyKey().hashCode();
    }

    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
        resetPropertyKey();
    }

    public void setFirmNumber(String firmNumber)
    {
        this.firmNumber = firmNumber;
        resetPropertyKey();
    }

    public void setExchangeAcronym(String exchangeAcronym)
    {
        this.exchangeAcronym = exchangeAcronym;
        resetPropertyKey();
    }

    /**
     * @return PropertyKey representation of the KeyData
     */
    public String toString()
    {
        return getPropertyKey();
    }

    /**
     * This method returns the original FirmStruct that was passed to this Object(unmodified) and this method is here
     * for Reflection purposes only
     * @return FirmStruct
     */
    public FirmStruct getFirm()
    {
        return firmStruct;
    }

    public void setFirm(FirmStruct firm)
    {
        this.firmStruct = firm;
        setFirmNumber(firm.firmNumber.firmNumber);
        setExchangeAcronym(firm.firmNumber.exchange);
    }

    /**
     * @return TradingSessionName
     */
    public TradingSessionName getTradingSession()
    {
        return tradingSessionName;
    }

    public void setTradingSession(TradingSessionName session)
    {
        this.tradingSessionName = session;
        resetPropertyKey();
    }

    protected String getKeyElement(String propertyKey, int offset) throws DataValidationException
    {
        String[] keyElements = RoutingKeyHelper.parsePropertyKey(propertyKey);
        String element = RoutingKeyHelper.getKeyElement(keyElements, offset);

        if (element == null)
        {
            throw ExceptionBuilder.dataValidationException(getClass().getName() + " Missing BasePropertyKey Element Key="
                                                           + keyElements + " . Could not find element " + offset + " .",
                                                           DataValidationCodes.INVALID_FIELD_LENGTH);
        }

        return element;
    }

    /**
     * offset is one of RoutingKeyHelper.SESSION_KEY_POSITION, RoutingKeyHelper.EXCHANGE_KEY_POSITION, or RoutingKeyHelper.FIRM_KEY_POSITION
     * @param keyElements
     * @param offset
     * @return
     * @throws DataValidationException
     */
    protected String getKeyElement(String[] keyElements, int offset) throws DataValidationException
    {
        String element = RoutingKeyHelper.getKeyElement(keyElements, offset);

        if (element == null)
        {
            throw ExceptionBuilder.dataValidationException(getClass().getName() + " Missing BasePropertyKey Element Key="
                                                           + keyElements + " . Could not find element " + offset + " .",
                                                           DataValidationCodes.INVALID_FIELD_LENGTH);
        }

        return element;
    }

    protected String[] splitPropertyKey(String propertyKey)
    {
        return BasicPropertyParser.parseArray(propertyKey);
    }

    protected abstract String createPropertyKey();

    /**
     * Builds the "base" property key from the sessionName, exchangeAcronym, and firmNumber elements
     */
    protected String createBasePropertyKey()
    {
        Object[] propertyKeyElements = {getSessionName(), getExchangeAcronym(), getFirmNumber()};

        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
    }

    protected void resetPropertyKey()
    {
        this.propertyKey = null;
    }

    /**
     * Gets the actual value for the propertyDescriptor from this Object
     * @param propertyDescriptor for field in this Object
     * @return value returned from read method in propertyDescriptor from this Object
     */
    public Object getFieldValue(PropertyDescriptor propertyDescriptor)
            throws IllegalAccessException, InvocationTargetException
    {
        Object returnValue;
        Method readMethod = propertyDescriptor.getReadMethod();
        returnValue = readMethod.invoke(this, null);
        return returnValue;
    }

    /**
     * Sets the actual value for the propertyDescriptor to this Object
     * @param propertyDescriptor for field in this Object
     * @param value Object value to set with
     */
    public void setFieldValue(PropertyDescriptor propertyDescriptor, Object value)
            throws IllegalAccessException, InvocationTargetException
    {
        Method writeMethod = propertyDescriptor.getWriteMethod();
        Object[] values = {value};
        writeMethod.invoke(this, values);
    }

    public Object getFieldDefaultValue(PropertyDescriptor propertyDescriptor)
    {
        if (defaultValues == null)
        {
        	return null;
        }else
        {
    	    return defaultValues.get(propertyDescriptor.getName());
        }
    }

    public void setFieldDefaultValue(PropertyDescriptor propertyDescriptor, Object value)
    {
    	if(defaultValues == null)
    	{
    		defaultValues = new HashMap<String, Object>(7);
    	}    	
        defaultValues.put(propertyDescriptor.getName(), value);
    }

    public boolean hasFieldDefaultValue(PropertyDescriptor propertyDescriptor)
    {
        return getFieldDefaultValue(propertyDescriptor) != null;
    }

    @SuppressWarnings({"EmptyCatchBlock", "UnusedCatchParameter"})
    public boolean isFieldDefaultValue(PropertyDescriptor propertyDescriptor)
    {
        boolean isDefault = false;

        try
        {
            Object value = getFieldValue(propertyDescriptor);
            Object defaultValue = getFieldDefaultValue(propertyDescriptor);
            if (value != null  &&  hasFieldDefaultValue(propertyDescriptor))
            {
                if (value instanceof FirmStruct)
                {
                    isDefault = ((FirmStruct) value).firmKey == 0;
                }
                else if (value instanceof SimpleComplexProductClass)
                {
                    isDefault = ((SimpleComplexProductClass) value).isDefaultProductClass();
                }
                else
                {
                    isDefault = value.toString().equals(defaultValue.toString());   // TODO: isDefault = value.equals(defaultValue);
                }
            }
        }
        catch(IllegalAccessException iae)
        {
        }
        catch(InvocationTargetException ite)
        {
        }
        return isDefault;
    }

    /**
     * Gets the PropertyDescriptor's for this BasePropertyGroup.
     * @return The PropertyDescriptor's that are returned from the BeanInfo
     * @throws java.beans.IntrospectionException that could be returned from the Introspector
     */
    public PropertyDescriptor[] getPropertyDescriptors() throws IntrospectionException
    {
        if (myPropertyDescriptors == null)
        {
            BeanInfo myBeanInfo = getMyBeanInfo();

            PropertyDescriptor[] descriptors = myBeanInfo.getPropertyDescriptors();
            if (descriptors != null)
            {
                myPropertyDescriptors = new PropertyDescriptor[descriptors.length];
                System.arraycopy(descriptors, 0, myPropertyDescriptors, 0, descriptors.length);
                Arrays.sort(myPropertyDescriptors, getPropertyDescriptorSortComparator());
            }
            else
            {
                myPropertyDescriptors = new PropertyDescriptor[0];
            }
        }

        PropertyDescriptor[] newArray = new PropertyDescriptor[myPropertyDescriptors.length];
        System.arraycopy(myPropertyDescriptors, 0, newArray, 0, myPropertyDescriptors.length);
        return newArray;
    }

    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptors.
     * @return comparator to use for sorting the returned PropertyDescriptors from getPropertyDescriptors().
     */
    public Comparator getPropertyDescriptorSortComparator()
    {
        return new ForcedPropertyDescriptorComparator(new String[]{ TRADING_SESSION_PROPERTY_NAME, FIRM_PROPERTY_NAME });
    }

    protected String buildNumberFormatExceptionMessage(String propertyKey, String expectedDataDesc,
                                                       String invalidStrValue)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append(" Key=");
        sb.append(propertyKey);
        sb.append(" part '");
        sb.append(invalidStrValue);
        sb.append("' cannot be converted to a ");
        sb.append(expectedDataDesc);
        sb.append(". ");

        return sb.toString();
    }

    /**
     * Finds this class implementations BeanInfo and caches it.
     */
    private BeanInfo getMyBeanInfo() throws IntrospectionException
    {
        if (myBeanInfo == null)
        {
            myBeanInfo = Introspector.getBeanInfo(getClass());
        }
        return myBeanInfo;
    }
}
