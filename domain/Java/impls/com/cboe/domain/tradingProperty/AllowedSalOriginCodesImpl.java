package com.cboe.domain.tradingProperty;

import java.util.Comparator;

import com.cboe.exceptions.DataValidationException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.tradingProperty.AllowedSalOriginCodes;
import com.cboe.interfaces.domain.tradingProperty.OrderOriginType;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

public class AllowedSalOriginCodesImpl extends AbstractTradingProperty
        implements AllowedSalOriginCodes
{
    private TradingPropertyType tradingPropertyType;

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told
     * which type this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public AllowedSalOriginCodesImpl(TradingPropertyType tradingPropertyType,
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
    public AllowedSalOriginCodesImpl(TradingPropertyType tradingPropertyType, String sessionName, int classKey,
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
     * @param newSalOriginCode
     * @param newSalOriginCodeEnabledFlag
     */
    public AllowedSalOriginCodesImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey,
                                       int newSalOriginCode, int newSalOriginCodeEnabledFlag)
    {
        this(tradingPropertyType, sessionName, classKey);
        setAllowedSalOriginCode(newSalOriginCode);
        setAllowedSalOriginCodeEnabledFlag(newSalOriginCodeEnabledFlag);
    }

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told
     * which type this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param value String to parse field values from
     */
    public AllowedSalOriginCodesImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey, String value)
    {
        super(tradingPropertyType.getName(), sessionName, classKey, value);
        this.tradingPropertyType = tradingPropertyType;
    }

    /**
     * Compares based on getNewSalOriginCode()
     */
    public int compareTo(Object object)
    {
        int result;
        int myValue = getAllowedSalOriginCode();
        int theirValue = ((AllowedSalOriginCodes) object).getAllowedSalOriginCode();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    /**
     * Overriden to return the getMinimumBidRange(), converted to a String, then the hashCode obtained of the
     * resulting String.
     */ 
    public int hashCode()
    {
        return Integer.toString(getAllowedSalOriginCode()).hashCode();
    }

    /**
     * @see com.cboe.interfaces.domain.tradingProperty.TradingProperty#getTradingPropertyType()
     */
    public TradingPropertyType getTradingPropertyType()
    {
        return tradingPropertyType;
    }

    /**
     * Returns the getNewSalOriginCode() as a String.
     */
    public String getPropertyName()
    {
        return Integer.toString(getAllowedSalOriginCode());
    }

    /**
     * Returns the super's double1 attribute
     */
    public int getAllowedSalOriginCode()
    {
        return getInteger1();
    }

    /**
     * Sets the super's double1 attribute
     */
    public void setAllowedSalOriginCode(int newSalOriginCode)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AllowedSalOriginCodesImpl>> Saving allowed sal origin code value = " + newSalOriginCode);
        }

        setInteger1(newSalOriginCode);
    }

    /**
     * Returns the super's integer 2 attribute
     */
    public int getAllowedSalOriginCodeEnabledFlag()
    {
        return getInteger2();
    }

    /**
     * Sets the super's integer 2 attribute
     */
    public void setAllowedSalOriginCodeEnabledFlag(int newSalOriginCodeEnabledFlag)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AllowedSalOriginCodeGroupImpl>> Saving allowedSalOriginCodeEnabledFlag value = " + newSalOriginCodeEnabledFlag);
        }

        setInteger2(newSalOriginCodeEnabledFlag);
    }

    /**
     * Returns the char attribute
     */
    public char getDisplayAllowedSalOriginCode()
    {
        OrderOriginType originType = OrderOriginType.findOrderOriginTypeEnum(getAllowedSalOriginCode());
        if(originType != null)
        {
            return originType.getOrderOriginCode();
        }
        return ' ';
    }

    /**
     * Sets the char attribute
     */
    public void setDisplayAllowedSalOriginCode(char newSalOriginCode)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AllowedSalOriginCodesImpl>> Saving Allowed Sal origin code value = " + newSalOriginCode);
        }
        OrderOriginType originType = OrderOriginType.findOrderOriginTypeEnum(newSalOriginCode);
        if(originType != null)
        {
            if(Log.isDebugOn())
            {
                Log.debug("AllowedSalOriginCodesImpl>> Saving converted Allowed Sal origin code value = " + originType.getOrderOriginId());
            }
            setInteger1(originType.getOrderOriginId());
        }
        else
        {
            Log.alarm("AllowedSalOriginCodesImpl>> Saving failed, not able to find Allowed Sal origin code value = " + newSalOriginCode + " in the Enum"); 
        }
    }
    
    /**
     * Returns the boolean attribute
     */
    public boolean getDisplayAllowedSalOriginCodeEnabledFlag()
    {
        return (getAllowedSalOriginCodeEnabledFlag() == 1 ? true : false);
    }

    public boolean isAllowedSalOriginCodeEnabledFlag()
    {
        return getDisplayAllowedSalOriginCodeEnabledFlag();
    }

    /**
     * Sets the boolean attribute
     */
    public void setDisplayAllowedSalOriginCodeEnabledFlag(boolean newSalOriginCodeEnabledFlag)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AllowedSalOriginCodesImpl>> Saving AllowedSalOriginCodeEnabledFlag value = " + newSalOriginCodeEnabledFlag);
        }
        int enabledFlag = (newSalOriginCodeEnabledFlag ? 1 : 0);
        setAllowedSalOriginCodeEnabledFlag(enabledFlag);
    }

    public void setAllowedSalOriginCodeEnabledFlag(boolean newSalOriginCodeEnabledFlag)
    {
        setDisplayAllowedSalOriginCodeEnabledFlag(newSalOriginCodeEnabledFlag);
    }

    /**
     * Allows the Trading Property to determine the order of the PropertyDescriptor's. This implementation will just
     * return a comparator that will sort by the desired property descriptor order from the BeanInfo.
     * @return comparator to use for sorting the returned PropertyDescriptor's from getPropertyDescriptors().
     */
    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {"allowedSalOriginCode", "allowedSalOriginCodeEnabledFlag"};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}
