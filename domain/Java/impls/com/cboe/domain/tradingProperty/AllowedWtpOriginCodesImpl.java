//
// -----------------------------------------------------------------------------------
// Source file: AllowedWtpOriginCodesImpl.java
//
// PACKAGE: com.cboe.domain.tradingProperty;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import java.util.*;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.tradingProperty.OrderOriginType;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;
import com.cboe.interfaces.domain.tradingProperty.AllowedWtpOriginCodes;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Represents a TradingProperty for an Allowed WTP Origin Codes
 */
public class AllowedWtpOriginCodesImpl extends AbstractTradingProperty
        implements AllowedWtpOriginCodes
{
    private TradingPropertyType tradingPropertyType;

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told
     * which type this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public AllowedWtpOriginCodesImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey)
    {
        super(tradingPropertyType.getName(), sessionName, classKey);
        this.tradingPropertyType = tradingPropertyType;
    }

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told
     * which type this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param property to initialize with
     */
    public AllowedWtpOriginCodesImpl(TradingPropertyType tradingPropertyType, String sessionName, int classKey,
                                       Property property)
            throws DataValidationException
    {
        super(sessionName, classKey, property);
        this.tradingPropertyType = tradingPropertyType;
    }

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told
     * which type this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param newWtpOriginCode
     * @param newWtpOriginCodeEnabledFlag
     */
    public AllowedWtpOriginCodesImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey,
                                       int newWtpOriginCode, int newWtpOriginCodeEnabledFlag)
    {
        this(tradingPropertyType, sessionName, classKey);
        setAllowedWtpOriginCode(newWtpOriginCode);
        setAllowedWtpOriginCodeEnabledFlag(newWtpOriginCodeEnabledFlag);
    }

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told
     * which type this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param value String to parse field values from
     */
    public AllowedWtpOriginCodesImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey, String value)
    {
        super(tradingPropertyType.getName(), sessionName, classKey, value);
        this.tradingPropertyType = tradingPropertyType;
    }

    /**
     * Compares based on getNewWtpOriginCode()
     */
    public int compareTo(Object object)
    {
        int result;
        int myValue = getAllowedWtpOriginCode();
        int theirValue = ((AllowedWtpOriginCodes) object).getAllowedWtpOriginCode();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    /**
     * Overriden to return the getAllowedWtpOriginCode(), converted to a String, then the hashCode obtained of the
     * resulting String.
     */ 
    public int hashCode()
    {
        return Integer.toString(getAllowedWtpOriginCode()).hashCode();
    }

    /**
     * @see com.cboe.interfaces.domain.tradingProperty.TradingProperty#getTradingPropertyType()
     */
    public TradingPropertyType getTradingPropertyType()
    {
        return tradingPropertyType;
    }

    /**
     * Returns the getNewWtpOriginCode() as a String.
     */
    public String getPropertyName()
    {
        return Integer.toString(getAllowedWtpOriginCode());
    }

    /**
     * Returns the super's double1 attribute
     */
    public int getAllowedWtpOriginCode()
    {
        return getInteger1();
    }

    /**
     * Sets the super's double1 attribute
     */
    public void setAllowedWtpOriginCode(int newWtpOriginCode)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AllowedWtpOriginCodesImpl>> Saving allowed wtp origin code value = " + newWtpOriginCode);
        }

        setInteger1(newWtpOriginCode);
    }

    /**
     * Returns the super's integer 2 attribute
     */
    public int getAllowedWtpOriginCodeEnabledFlag()
    {
        return getInteger2();
    }

    /**
     * Sets the super's integer 2 attribute
     */
    public void setAllowedWtpOriginCodeEnabledFlag(int newWtpOriginCodeEnabledFlag)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AllowedWtpOriginCodeGroupImpl>> Saving allowedWtpOriginCodeEnabledFlag value = " + newWtpOriginCodeEnabledFlag);
        }

        setInteger2(newWtpOriginCodeEnabledFlag);
    }

    /**
     * Returns the char attribute
     */
    public char getDisplayAllowedWtpOriginCode()
    {
        OrderOriginType originType = OrderOriginType.findOrderOriginTypeEnum(getAllowedWtpOriginCode());
        if(originType != null)
        {
            return originType.getOrderOriginCode();
        }
        return ' ';
    }

    /**
     * Sets the char attribute
     */
    public void setDisplayAllowedWtpOriginCode(char newWtpOriginCode)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AllowedWtpOriginCodesImpl>> Saving Allowed Wtp origin code value = " + newWtpOriginCode);
        }
        OrderOriginType originType = OrderOriginType.findOrderOriginTypeEnum(newWtpOriginCode);
        if(originType != null)
        {
            if(Log.isDebugOn())
            {
                Log.debug("AllowedWtpOriginCodesImpl>> Saving converted Allowed Wtp origin code value = " + originType.getOrderOriginId());
            }
            setInteger1(originType.getOrderOriginId());
        }
        else
        {
            Log.alarm("AllowedWtpOriginCodesImpl>> Saving failed, not able to find Allowed Wtp origin code value = " + newWtpOriginCode + " in the Enum"); 
        }
    }
    
    /**
     * Returns the boolean attribute
     */
    public boolean getDisplayAllowedWtpOriginCodeEnabledFlag()
    {
        return (getAllowedWtpOriginCodeEnabledFlag() == 1 ? true : false);
    }

    public boolean isAllowedWtpOriginCodeEnabledFlag()
    {
        return getDisplayAllowedWtpOriginCodeEnabledFlag();
    }

    /**
     * Sets the boolean attribute
     */
    public void setDisplayAllowedWtpOriginCodeEnabledFlag(boolean newWtpOriginCodeEnabledFlag)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AllowedWtpOriginCodesImpl>> Saving AllowedWtpOriginCodeEnabledFlag value = " + newWtpOriginCodeEnabledFlag);
        }
        int enabledFlag = (newWtpOriginCodeEnabledFlag ? 1 : 0);
        setAllowedWtpOriginCodeEnabledFlag(enabledFlag);
    }

    public void setAllowedWtpOriginCodeEnabledFlag(boolean newWtpOriginCodeEnabledFlag)
    {
        setDisplayAllowedWtpOriginCodeEnabledFlag(newWtpOriginCodeEnabledFlag);
    }

    /**
     * Allows the Trading Property to determine the order of the PropertyDescriptor's. This implementation will just
     * return a comparator that will sort by the desired property descriptor order from the BeanInfo.
     * @return comparator to use for sorting the returned PropertyDescriptor's from getPropertyDescriptors().
     */
    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {"allowedWtpOriginCode", "allowedWtpOriginCodeEnabledFlag"};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}
