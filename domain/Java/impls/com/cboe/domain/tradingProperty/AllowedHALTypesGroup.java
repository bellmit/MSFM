package com.cboe.domain.tradingProperty;

import java.lang.reflect.InvocationTargetException;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.tradingProperty.AllowedHALTypes;
import com.cboe.interfaces.domain.tradingProperty.TradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyFactory;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyGroup;

/**
 * Represents a TradingPropertyGroup for the Minimum and Maximum Order Size for an Auction.
 */
public class AllowedHALTypesGroup extends AbstractTradingPropertyGroup
{
    public static final TradingPropertyType TRADING_PROPERTY_TYPE = TradingPropertyTypeImpl.ALLOWED_HAL_TYPES;

    /**
     * Constructor that initializes with the immutable trading session name and class key. WARNING!! THIS CONSTRUCTOR
     * MUST ALWAYS BE EXPOSED. THE FACTORY WILL USE THIS CONSTRUCTOR TO INSTANTIATE THE OBJECT WITH THE IMMUTABLE
     * SESSION NAME AND CLASS KEY.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public AllowedHALTypesGroup(String sessionName, int classKey)
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
    public AllowedHALTypesGroup(String sessionName, int classKey, PropertyServicePropertyGroup propertyGroup)
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
    public AllowedHALTypesGroup(String sessionName, int classKey, int versionNumber)
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
    public static AllowedHALTypesGroup getGroup(String sessionName, int classKey)
            throws SystemException, NotFoundException, DataValidationException, CommunicationException,
                   InvocationTargetException, AuthorizationException, TransactionFailedException
    {
        TradingPropertyFactory factory = TradingPropertyFactoryHome.find();
        TradingPropertyGroup newGroup = factory.getTradingPropertyGroup(sessionName, classKey,
                                                                        TRADING_PROPERTY_TYPE.getName());
        return (AllowedHALTypesGroup) newGroup;
    }

    /**
     * Create a new implementation specific TradingProperty.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @return new TradingProperty
     */
    public TradingProperty createNewTradingProperty(String sessionName, int classKey)
    {
        return new AllowedHALTypesImpl(sessionName, classKey);
    }

    public TradingPropertyType getTradingPropertyType()
    {
        return TRADING_PROPERTY_TYPE;
    }

    /**
     * Gets all the contained TradingProperty's downcasted for convenience.
     */
    public AllowedHALTypes[] getAllowedHALTypesTradingProperties()
    {
        TradingProperty[] allTPs = getAllTradingProperties();
        AllowedHALTypes[] castedTPs = new AllowedHALTypes[allTPs.length];
        System.arraycopy(allTPs, 0, castedTPs, 0, allTPs.length);
        return castedTPs;
    }

    /**
     * Gets only the AllowedHALTypes for a specific auction type. If one does not exist, null will be returned.
     * @param auctionType to get TradingProperty for
     * @return TradingProperty downcasted for convenience.
     */
    public AllowedHALTypes getAllowedHALTypesTradingProperty(short allowedHALType)
    {
        AllowedHALTypes castedTP =
                (AllowedHALTypes) getTradingPropertyMap().get(new Short(allowedHALType));
        return castedTP;
    }

    /**
     * This method is used to get an Object to be used as the key for adding, obtaining and removing the
     * TradingProperty's from the underlying Map collection returned from getTradingPropertyMap(). This
     * implementation always returns an Integer object representing the auction type. This enforces the cardinality
     * of only one existing per auction type.
     * @param tradingProperty to get key Object for
     * @return Integer object representing the auction type.
     */
    protected Object getMapKeyForTradingProperty(TradingProperty tradingProperty)
    {
        return new Short( ( (AllowedHALTypes)tradingProperty ).getAllowedHALType());
    }
}
