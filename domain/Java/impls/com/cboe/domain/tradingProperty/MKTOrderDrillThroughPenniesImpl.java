package com.cboe.domain.tradingProperty;

import java.util.*;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.tradingProperty.MKTOrderDrillThroughPennies;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Represents a TradingProperty that will restrict the fills for a MKT order from occurring too many price points away from the initial NBBO
 *
 * @author montiel
 *
 */
public class MKTOrderDrillThroughPenniesImpl extends AbstractTradingProperty
        implements MKTOrderDrillThroughPennies
{
    private TradingPropertyType tradingPropertyType;

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told
     * which type this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public MKTOrderDrillThroughPenniesImpl(TradingPropertyType tradingPropertyType,
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
    public MKTOrderDrillThroughPenniesImpl(TradingPropertyType tradingPropertyType, String sessionName, int classKey,
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
     * @param minimumNBBORange
     * @param maximumNBBORange
     * @param noOfPennies
     */
    public MKTOrderDrillThroughPenniesImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey,
                                       double minimumNBBORange, double maximumNBBORange,
                                       double noOfPennies )
    {
        this(tradingPropertyType, sessionName, classKey);
        setMinimumNBBORange(minimumNBBORange);
        setMaximumNBBORange(maximumNBBORange);
        setNoOfPennies(noOfPennies);
    }

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told
     * which type this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param value String to parse field values from
     */
    public MKTOrderDrillThroughPenniesImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey, String value)
    {
        super(tradingPropertyType.getName(), sessionName, classKey, value);
        this.tradingPropertyType = tradingPropertyType;
    }

    /**
     * Compares based on getMinimumNBBORange()
     */
    public int compareTo(Object object)
    {
        int result;
        double myValue = getMinimumNBBORange();
        double theirValue = ((MKTOrderDrillThroughPennies) object).getMinimumNBBORange();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    /**
     * Overriden to return the getMinimumNBBORange(), converted to a String, then the hashCode obtained of the
     * resulting String.
     */
    public int hashCode()
    {
        return Double.toString(getMinimumNBBORange()).hashCode();
    }

    /**
     * @see com.cboe.interfaces.domain.tradingProperty.TradingProperty#getTradingPropertyType()
     */
    public TradingPropertyType getTradingPropertyType()
    {
        return tradingPropertyType;
    }

    /**
     * Returns the getMinimumNBBORange() as a String.
     */
    public String getPropertyName()
    {
        return Double.toString(getMinimumNBBORange());
    }

    /**
     * Returns the super's double1 attribute
     */
    public double getMinimumNBBORange()
    {
        return getDouble1();
    }

    /**
     * Sets the super's double1 attribute
     */
    public void setMinimumNBBORange(double minNBBO)
    {
        if(Log.isDebugOn())
        {
            Log.debug("MKTOrderDrillThroughPenniesImpl>> Saving minimumNBBORange value = " + minNBBO);
        }

        setDouble1(minNBBO);
    }

    /**
     * Returns the super's double2 attribute
     */
    public double getMaximumNBBORange()
    {
        return getDouble2();
    }

    /**
     * Sets the super's double2 attribute
     */
    public void setMaximumNBBORange(double maxNBBO)
    {
        if(Log.isDebugOn())
        {
            Log.debug("MKTOrderDrillThroughPenniesImpl>> Saving maximumNBBORange value = " + maxNBBO);
        }

        setDouble2(maxNBBO);
    }

    /**
     * Returns the super's Integer1 attribute
     */
    public double getNoOfPennies()
    {
        return getDouble3();
    }

    /**
     * Sets the super's Double3 attribute
     */
    public void setNoOfPennies(double noOfPennies)
    {
        if(Log.isDebugOn())
        {
            Log.debug("MKTOrderDrillThroughPenniesImpl>> Saving noOfPennies value = " + noOfPennies);
        }

        setDouble3(noOfPennies);
    }

    /**
     * Allows the Trading Property to determine the order of the PropertyDescriptor's. This implementation will just
     * return a comparator that will sort by the desired property descriptor order from the BeanInfo.
     * @return comparator to use for sorting the returned PropertyDescriptor's from getPropertyDescriptors().
     */
    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {"minimumNBBORange", "maximumNBBORange", "noOfPennies"};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}