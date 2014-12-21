/**
 * 
 */
package com.cboe.domain.tradingProperty;

import java.util.Comparator;

import com.cboe.exceptions.DataValidationException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.tradingProperty.AutoLinkOriginCodes;
import com.cboe.interfaces.domain.tradingProperty.OrderOriginType;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

/**
 * Represents a TradingProperty for an Auto Link Origin Codes
 * 
 * @author misbahud
 *
 */
public class AutoLinkOriginCodesImpl extends AbstractTradingProperty
        implements AutoLinkOriginCodes
{
    private TradingPropertyType tradingPropertyType;

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told
     * which type this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public AutoLinkOriginCodesImpl(TradingPropertyType tradingPropertyType,
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
    public AutoLinkOriginCodesImpl(TradingPropertyType tradingPropertyType, String sessionName, int classKey,
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
     * @param sessionName
     * @param classKey
     * @param newAutoLinkOriginCode
     * @param newAutoLinkOriginCodeEnabledFlag
     */
    public AutoLinkOriginCodesImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey,
                                       int newAutoLinkOriginCode, int newAutoLinkOriginCodeEnabledFlag)
    {
        this(tradingPropertyType, sessionName, classKey);
        setAutoLinkOriginCode(newAutoLinkOriginCode);
        setAutoLinkOriginCodeEnabledFlag(newAutoLinkOriginCodeEnabledFlag);
    }

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told
     * which type this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param value String to parse field values from
     */
    public AutoLinkOriginCodesImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey, String value)
    {
        super(tradingPropertyType.getName(), sessionName, classKey, value);
        this.tradingPropertyType = tradingPropertyType;
    }

    /**
     * Compares based on getAutoLinkOriginCode()
     */
    public int compareTo(Object object)
    {
        int result;
        int myValue = getAutoLinkOriginCode();
        int theirValue = ((AutoLinkOriginCodes) object).getAutoLinkOriginCode();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    /**
     * Overriden to return the getAutoLinkOriginCode(), converted to a String, then the hashCode obtained of the
     * resulting String.
     */ 
    public int hashCode()
    {
        return Integer.toString(getAutoLinkOriginCode()).hashCode();
    }

    /**
     * @see com.cboe.interfaces.domain.tradingProperty.TradingProperty#getTradingPropertyType()
     */
    public TradingPropertyType getTradingPropertyType()
    {
        return tradingPropertyType;
    }

    /**
     * Returns the getAutoLinkOriginCode() as a String.
     */
    public String getPropertyName()
    {
        return Integer.toString(getAutoLinkOriginCode());
    }

    /**
     * Returns the integer1 attribute
     */
    public int getAutoLinkOriginCode()
    {
        return getInteger1();
    }

    /**
     * Sets the integer1 attribute
     */
    public void setAutoLinkOriginCode(int newAutoLinkOriginCode)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AutoLinkOriginCodesImpl>> Saving Auto link origin code value = " + newAutoLinkOriginCode);
        }

        setInteger1(newAutoLinkOriginCode);
    }
    
    /**
     * Returns the char attribute
     */
    public char getDisplayAutoLinkOriginCode()
    {
        OrderOriginType originType = OrderOriginType.findOrderOriginTypeEnum(getAutoLinkOriginCode());
        if(originType != null)
        {
            return originType.getOrderOriginCode();
        }
        return ' ';
    }

    /**
     * Sets the char attribute
     */
    public void setDisplayAutoLinkOriginCode(char newAutoLinkOriginCode)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AutoLinkOriginCodesImpl>> Saving Auto link origin code value = " + newAutoLinkOriginCode);
        }
        OrderOriginType originType = OrderOriginType.findOrderOriginTypeEnum(newAutoLinkOriginCode);
        if(originType != null)
        {
            if(Log.isDebugOn())
            {
                Log.debug("AutoLinkOriginCodesImpl>> Saving converted Auto link origin code value = " + originType.getOrderOriginId());
            }
            setInteger1(originType.getOrderOriginId());
        }
        else
        {
            Log.alarm("AutoLinkOriginCodesImpl>> Saving failed, not able to find Auto link origin code value = " + newAutoLinkOriginCode + " in the Enum"); 
        }
    }


    /**
     * Returns the integer 2 attribute
     */
    public int getAutoLinkOriginCodeEnabledFlag()
    {
        return getInteger2();
    }

    /**
     * Sets the integer 2 attribute
     */
    public void setAutoLinkOriginCodeEnabledFlag(int newAutoLinkOriginCodeEnabledFlag)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AutoLinkOriginCodesImpl>> Saving AutoLinkOriginCodeEnabledFlag value = " + newAutoLinkOriginCodeEnabledFlag);
        }

        setInteger2(newAutoLinkOriginCodeEnabledFlag);
    }
    
    /**
     * Returns the boolean attribute
     */
    public boolean getDisplayAutoLinkOriginCodeEnabledFlag()
    {
        return (getAutoLinkOriginCodeEnabledFlag() == 1 ? true : false);
    }

    public boolean isAutoLinkOriginCodeEnabledFlag()
    {
        return getDisplayAutoLinkOriginCodeEnabledFlag();
    }


    /**
     * Sets the boolean attribute
     */
    public void setDisplayAutoLinkOriginCodeEnabledFlag(boolean newAutoLinkOriginCodeEnabledFlag)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AutoLinkOriginCodesImpl>> Saving AutoLinkOriginCodeEnabledFlag value = " + newAutoLinkOriginCodeEnabledFlag);
        }
        int enabledFlag = (newAutoLinkOriginCodeEnabledFlag ? 1 : 0);
        setAutoLinkOriginCodeEnabledFlag(enabledFlag);
    }
    
    public void setAutoLinkOriginCodeEnabledFlag(boolean newAutoLinkOriginCodeEnabledFlag)
    {
        setDisplayAutoLinkOriginCodeEnabledFlag(newAutoLinkOriginCodeEnabledFlag);
    }



    /**
     * Allows the Trading Property to determine the order of the PropertyDescriptor's. This implementation will just
     * return a comparator that will sort by the desired property descriptor order from the BeanInfo.
     * @return comparator to use for sorting the returned PropertyDescriptor's from getPropertyDescriptors().
     */
    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {"autoLinkOriginCode", "autoLinkOriginCodeEnabledFlag"};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}
