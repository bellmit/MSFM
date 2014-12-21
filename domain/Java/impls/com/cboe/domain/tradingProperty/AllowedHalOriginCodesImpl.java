//
// -----------------------------------------------------------------------------------
// Source file: ExtremelyWideQuoteWidthImpl.java
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
import com.cboe.interfaces.domain.tradingProperty.AllowedHalOriginCodes;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Represents a TradingProperty for an Exchange Prescribed Width
 */
public class AllowedHalOriginCodesImpl extends AbstractTradingProperty
        implements AllowedHalOriginCodes
{
    private TradingPropertyType tradingPropertyType;

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told
     * which type this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public AllowedHalOriginCodesImpl(TradingPropertyType tradingPropertyType,
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
    public AllowedHalOriginCodesImpl(TradingPropertyType tradingPropertyType, String sessionName, int classKey,
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
     * @param newHalOriginCode
     * @param newHalOriginCodeEnabledFlag
     */
    public AllowedHalOriginCodesImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey,
                                       int newHalOriginCode, int newHalOriginCodeEnabledFlag)
    {
        this(tradingPropertyType, sessionName, classKey);
        setAllowedHalOriginCode(newHalOriginCode);
        setAllowedHalOriginCodeEnabledFlag(newHalOriginCodeEnabledFlag);
    }

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told
     * which type this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param value String to parse field values from
     */
    public AllowedHalOriginCodesImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey, String value)
    {
        super(tradingPropertyType.getName(), sessionName, classKey, value);
        this.tradingPropertyType = tradingPropertyType;
    }

    /**
     * Compares based on getNewHalOriginCode()
     */
    public int compareTo(Object object)
    {
        int result;
        int myValue = getAllowedHalOriginCode();
        int theirValue = ((AllowedHalOriginCodes) object).getAllowedHalOriginCode();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    /**
     * Overriden to return the getMinimumBidRange(), converted to a String, then the hashCode obtained of the
     * resulting String.
     */ 
    public int hashCode()
    {
        return Integer.toString(getAllowedHalOriginCode()).hashCode();
    }

    /**
     * @see com.cboe.interfaces.domain.tradingProperty.TradingProperty#getTradingPropertyType()
     */
    public TradingPropertyType getTradingPropertyType()
    {
        return tradingPropertyType;
    }

    /**
     * Returns the getNewHalOriginCode() as a String.
     */
    public String getPropertyName()
    {
        return Integer.toString(getAllowedHalOriginCode());
    }

    /**
     * Returns the super's double1 attribute
     */
    public int getAllowedHalOriginCode()
    {
        return getInteger1();
    }

    /**
     * Sets the super's double1 attribute
     */
    public void setAllowedHalOriginCode(int newHalOriginCode)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AllowedHalOriginCodesImpl>> Saving allowed hal origin code value = " + newHalOriginCode);
        }

        setInteger1(newHalOriginCode);
    }

    /**
     * Returns the super's integer 2 attribute
     */
    public int getAllowedHalOriginCodeEnabledFlag()
    {
        return getInteger2();
    }

    /**
     * Sets the super's integer 2 attribute
     */
    public void setAllowedHalOriginCodeEnabledFlag(int newHalOriginCodeEnabledFlag)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AllowedHalOriginCodeGroupImpl>> Saving allowedHalOriginCodeEnabledFlag value = " + newHalOriginCodeEnabledFlag);
        }

        setInteger2(newHalOriginCodeEnabledFlag);
    }

    /**
     * Returns the char attribute
     */
    public char getDisplayAllowedHalOriginCode()
    {
        OrderOriginType originType = OrderOriginType.findOrderOriginTypeEnum(getAllowedHalOriginCode());
        if(originType != null)
        {
            return originType.getOrderOriginCode();
        }
        return ' ';
    }

    /**
     * Sets the char attribute
     */
    public void setDisplayAllowedHalOriginCode(char newHalOriginCode)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AllowedHalOriginCodesImpl>> Saving Allowed Hal origin code value = " + newHalOriginCode);
        }
        OrderOriginType originType = OrderOriginType.findOrderOriginTypeEnum(newHalOriginCode);
        if(originType != null)
        {
            if(Log.isDebugOn())
            {
                Log.debug("AllowedHalOriginCodesImpl>> Saving converted Allowed Hal origin code value = " + originType.getOrderOriginId());
            }
            setInteger1(originType.getOrderOriginId());
        }
        else
        {
            Log.alarm("AllowedHalOriginCodesImpl>> Saving failed, not able to find Allowed Hal origin code value = " + newHalOriginCode + " in the Enum"); 
        }
    }
    
    /**
     * Returns the boolean attribute
     */
    public boolean getDisplayAllowedHalOriginCodeEnabledFlag()
    {
        return (getAllowedHalOriginCodeEnabledFlag() == 1 ? true : false);
    }

    public boolean isAllowedHalOriginCodeEnabledFlag()
    {
        return getDisplayAllowedHalOriginCodeEnabledFlag();
    }

    /**
     * Sets the boolean attribute
     */
    public void setDisplayAllowedHalOriginCodeEnabledFlag(boolean newHalOriginCodeEnabledFlag)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AllowedHalOriginCodesImpl>> Saving AllowedHalOriginCodeEnabledFlag value = " + newHalOriginCodeEnabledFlag);
        }
        int enabledFlag = (newHalOriginCodeEnabledFlag ? 1 : 0);
        setAllowedHalOriginCodeEnabledFlag(enabledFlag);
    }

    public void setAllowedHalOriginCodeEnabledFlag(boolean newHalOriginCodeEnabledFlag)
    {
        setDisplayAllowedHalOriginCodeEnabledFlag(newHalOriginCodeEnabledFlag);
    }

    /**
     * Allows the Trading Property to determine the order of the PropertyDescriptor's. This implementation will just
     * return a comparator that will sort by the desired property descriptor order from the BeanInfo.
     * @return comparator to use for sorting the returned PropertyDescriptor's from getPropertyDescriptors().
     */
    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {"allowedHalOriginCode", "allowedHalOriginCodeEnabledFlag"};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}
