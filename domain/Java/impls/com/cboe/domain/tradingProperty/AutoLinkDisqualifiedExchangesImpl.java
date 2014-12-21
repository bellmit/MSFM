/**
 * 
 */
package com.cboe.domain.tradingProperty;

import java.util.Comparator;

import com.cboe.exceptions.DataValidationException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.tradingProperty.AutoLinkDisqualifiedExchanges;
import com.cboe.interfaces.domain.tradingProperty.LinkageExchanges;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

/**
 * Represents a TradingProperty for an Auto Link Disqualified Exchanges
 * 
 * @author misbahud
 *
 */
public class AutoLinkDisqualifiedExchangesImpl extends AbstractTradingProperty
        implements AutoLinkDisqualifiedExchanges
{
    private TradingPropertyType tradingPropertyType;

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told
     * which type this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public AutoLinkDisqualifiedExchangesImpl(TradingPropertyType tradingPropertyType,
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
    public AutoLinkDisqualifiedExchangesImpl(TradingPropertyType tradingPropertyType, String sessionName, int classKey,
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
     * @param newAutoLinkDisqualifiedExchanges
     * @param newAutoLinkDisqualifiedExchangesFlag
     */
    public AutoLinkDisqualifiedExchangesImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey,
                                       int newAutoLinkDisqualifiedExchanges, int newAutoLinkDisqualifiedExchangesFlag)
    {
        this(tradingPropertyType, sessionName, classKey);
        setAutoLinkDisqualifiedExchangesId(newAutoLinkDisqualifiedExchanges);
        setAutoLinkDisqualifiedExchangesFlag(newAutoLinkDisqualifiedExchangesFlag);
    }

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told
     * which type this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param value String to parse field values from
     */
    public AutoLinkDisqualifiedExchangesImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey, String value)
    {
        super(tradingPropertyType.getName(), sessionName, classKey, value);
        this.tradingPropertyType = tradingPropertyType;
    }

    /**
     * Compares based on getAutoLinkDisqualifiedExchanges()
     */
    public int compareTo(Object object)
    {
        int result;
        int myValue = getAutoLinkDisqualifiedExchangesId();
        int theirValue = ((AutoLinkDisqualifiedExchanges) object).getAutoLinkDisqualifiedExchangesId();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    /**
     * Overriden to return the getAutoLinkDisqualifiedExchanges(), converted to a String, then the hashCode obtained of the
     * resulting String.
     */ 
    public int hashCode()
    {
        return Integer.toString(getAutoLinkDisqualifiedExchangesId()).hashCode();
    }

    /**
     * @see com.cboe.interfaces.domain.tradingProperty.TradingProperty#getTradingPropertyType()
     */
    public TradingPropertyType getTradingPropertyType()
    {
        return tradingPropertyType;
    }

    /**
     * Returns the getAutoLinkDisqualifiedExchanges() as a String.
     */
    public String getPropertyName()
    {
        return Integer.toString(getAutoLinkDisqualifiedExchangesId());
    }

    /**
     * Returns the integer1 attribute
     */
    public int getAutoLinkDisqualifiedExchangesId()
    {
        return getInteger1();
    }

    /**
     * Sets the integer1 attribute
     */
    public void setAutoLinkDisqualifiedExchangesId(int newAutoLinkDisqualifiedExchanges)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AutoLinkDisqualifiedExchangesImpl>> Saving Auto Link Disqualified Exchange value = " + newAutoLinkDisqualifiedExchanges);
        }

        setInteger1(newAutoLinkDisqualifiedExchanges);
    }
    
    /**
     * Returns the String attribute
     */
    public String getAutoLinkDisqualifiedExchanges()
    {
        LinkageExchanges linkageExch = LinkageExchanges.findLinkageExchange(getAutoLinkDisqualifiedExchangesId());
        if(linkageExch != null)
        {
            return linkageExch.toString();
        }
        return LinkageExchanges.UNSPECIFIED.toString();
    }

    /**
     * Sets the String attribute
     */
    public void setAutoLinkDisqualifiedExchanges(String newAutoLinkDisqualifiedExchanges)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AutoLinkDisqualifiedExchangesImpl>> Saving Auto Link Disqualified Exchange value = " + newAutoLinkDisqualifiedExchanges);
        }
        LinkageExchanges linkageExch = LinkageExchanges.findLinkageExchange(newAutoLinkDisqualifiedExchanges);
        if(linkageExch != null && !LinkageExchanges.UNSPECIFIED.name().equalsIgnoreCase(linkageExch.name()))
        {
            if(Log.isDebugOn())
            {
                Log.debug("AutoLinkDisqualifiedExchangesImpl>> Saving converted Auto Link Disqualified Exchange value = " + linkageExch.getExchangeId());
            }
            setInteger1(linkageExch.getExchangeId());
        }
        else
        {
            Log.alarm("AutoLinkDisqualifiedExchangesImpl>> Saving failed, not able to find Auto Link Disqualified Exchange value = " + newAutoLinkDisqualifiedExchanges + " in the Enum"); 
        }
    }

    /**
     * Returns the integer 2 attribute
     */
    public int getAutoLinkDisqualifiedExchangesFlag()
    {
        return getInteger2();
    }

    /**
     * Sets the integer 2 attribute
     */
    public void setAutoLinkDisqualifiedExchangesFlag(int newAutoLinkDisqualifiedExchangesFlag)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AutoLinkDisqualifiedExchangesImpl>> Saving Auto Link Disqualified Exchange Flag value = " + newAutoLinkDisqualifiedExchangesFlag);
        }

        setInteger2(newAutoLinkDisqualifiedExchangesFlag);
    }
    
    /**
     * Returns the boolean attribute
     */
    public boolean isAutoLinkDisqualifiedExchangesFlag()
    {
        return (getAutoLinkDisqualifiedExchangesFlag() == 1 ? true : false);
    }

    /**
     * Sets the boolean attribute
     */
    public void setAutoLinkDisqualifiedExchangesFlag(boolean newAutoLinkDisqualifiedExchangesFlag)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AutoLinkDisqualifiedExchangesImpl>> Saving AutoLinkDisqualifiedExchangesFlag value = " + newAutoLinkDisqualifiedExchangesFlag);
        }
        int enabledFlag = (newAutoLinkDisqualifiedExchangesFlag ? 1 : 0);
        setAutoLinkDisqualifiedExchangesFlag(enabledFlag);
    }


    /**
     * Allows the Trading Property to determine the order of the PropertyDescriptor's. This implementation will just
     * return a comparator that will sort by the desired property descriptor order from the BeanInfo.
     * @return comparator to use for sorting the returned PropertyDescriptor's from getPropertyDescriptors().
     */
    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {"autoLinkDisqualifiedExchanges", "autoLinkDisqualifiedExchangesFlag"};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }

}
