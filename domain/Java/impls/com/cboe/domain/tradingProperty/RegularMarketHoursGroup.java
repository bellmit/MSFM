package com.cboe.domain.tradingProperty;

import java.lang.reflect.InvocationTargetException;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.tradingProperty.RegularMarketHours;
import com.cboe.interfaces.domain.tradingProperty.TradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyFactory;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyGroup;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

/**
 * @author josephg
 */
public class RegularMarketHoursGroup extends AbstractTradingPropertyGroup
{
	public static final TradingPropertyType TRADING_PROPERTY_TYPE = TradingPropertyTypeImpl.REGULAR_MARKET_HOURS;
	
    private final Object defaultHashForMap = new Object();

    /**
     * Constructor that initializes with the immutable trading session name and class key. WARNING!! THIS CONSTRUCTOR
     * MUST ALWAYS BE EXPOSED. THE FACTORY WILL USE THIS CONSTRUCTOR TO INSTANTIATE THE OBJECT WITH THE IMMUTABLE
     * SESSION NAME AND CLASS KEY.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public RegularMarketHoursGroup(String sessionName, int classKey)
    {
        super(sessionName, classKey);
    }

    /**
     * Constructor that initializes with the immutable trading session name, class key and the
     * PropertyServicePropertyGroup to initialize the sub-classes trading property data with.
     * @param sessionName that this TradingPropertyGroup is for
     * @param classKey that this TradingPropertyGroup is for
     * @param propertyGroup to initialize with
     */
    public RegularMarketHoursGroup(String sessionName, int classKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException
    {
        super(sessionName, classKey, propertyGroup);
    }

    /**
     * Constructor that initializes with the immutable trading session name and class key. WARNING!! THIS CONSTRUCTOR
     * MUST ALWAYS BE EXPOSED. THE FACTORY WILL USE THIS CONSTRUCTOR TO INSTANTIATE THE OBJECT WITH THE IMMUTABLE
     * SESSION NAME AND CLASS KEY.
     * @param sessionName that this TradingProperty is for
     * @param classKey    that this TradingProperty is for
     * @param versionNumber that this TradingProperty is for
     */
    public RegularMarketHoursGroup(String sessionName, int classKey, int versionNumber)
    {
        super(sessionName, classKey, versionNumber);
    }

    /**
     * Provides a getter to get an instance of this TradingPropertyGroup, downcasted for convenience.
     * @param sessionName to get TradingPropertyGroup for
     * @param classKey    to get TradingPropertyGroup for
     * @return instance of TradingPropertyGroup downcasted to this class type.
     * @throws SystemException           forwarded from TradingPropertyFactory
     * @throws AuthorizationException    forwarded from TradingPropertyFactory
     * @throws CommunicationException    forwarded from TradingPropertyFactory
     * @throws NotFoundException         forwarded from TradingPropertyFactory
     * @throws InvocationTargetException forwarded from TradingPropertyFactory
     * @throws DataValidationException   forwarded from TradingPropertyFactory
     */
    public static RegularMarketHoursGroup getGroup(String sessionName, int classKey)
            throws SystemException, NotFoundException, DataValidationException, CommunicationException,
                   InvocationTargetException, AuthorizationException, TransactionFailedException
    {
        TradingPropertyFactory factory = TradingPropertyFactoryHome.find();
        TradingPropertyGroup myGroup = factory.getTradingPropertyGroup(sessionName, classKey,
                                                                       TRADING_PROPERTY_TYPE.getName());
        return (RegularMarketHoursGroup) myGroup;
    }

    /**
     * Create a new implementation specific TradingProperty.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @return new TradingProperty
     */
    public TradingProperty createNewTradingProperty(String sessionName, int classKey)
    {
        return new RegularMarketHoursImpl(sessionName, classKey);
    }

    /**
     * Gets the TradingPropertyType for this group that identifies the type of this group.
     */
    public TradingPropertyType getTradingPropertyType()
    {
        return TRADING_PROPERTY_TYPE;
    }

    /**
     * Should be only one.
     */
    public RegularMarketHours getRegularMarketHoursProperty()
    {
        TradingProperty[] allTPs = getAllTradingProperties();
        return (RegularMarketHours) allTPs[0];
    }    
    
    /**
     * Provides the maximum number of Trading Properties this particular group implementation may allow.
     * @return This implementation will always return 1.
     */
    public int getMaxTradingPropertiesAllowed()
    {
        return 1;
    }

    /**
     * This method is used to get an Object to be used as the key for adding, obtaining and removing the
     * TradingProperty's from the underlying Map collection returned from getTradingPropertyMap(). This implementation
     * always returns the exact same instance of an Object per instance of this group, since this implementation may
     * only have one TradingProperty. This ensures that only one will exist per instance of this group, since a previous
     * one will always get over-written since its key will always hash the same and be equal.
     * @param tradingProperty to get key Object for
     * @return same instance of an Object per instance of this TradingPropertyGroup
     */
    protected Object getMapKeyForTradingProperty(TradingProperty tradingProperty)
    {
        return defaultHashForMap;
    }

}
