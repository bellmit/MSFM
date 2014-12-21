package com.cboe.domain.routingProperty.key;

import java.util.*;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;

public class SessionFirmKeyMarketMaker extends SessionFirmKey
{
    public static final String MARKET_MAKER = "marketMaker";

    private static final int MARKET_MAKER_PROPERTY_KEY_POSITION = 0;

    protected String marketmaker;

    public SessionFirmKeyMarketMaker(BasePropertyType type)
    {
        super(type);
        this.marketmaker = "";
       
    }

    public SessionFirmKeyMarketMaker(String propertyName, String sessionName, String firmAcronym,
                                         String exchangeAcronym, String marketMaker)
    {
        super(propertyName, sessionName, firmAcronym, exchangeAcronym);
        this.marketmaker= marketMaker;
    }

    public SessionFirmKeyMarketMaker(String propertyKey)
            throws DataValidationException
    {
        super(propertyKey);
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        SessionFirmKeyMarketMaker newKey = (SessionFirmKeyMarketMaker) super.clone();
        newKey.marketmaker = marketmaker;

        return newKey;
    }

    /**
     * Parses the propertyKey to find the separate key values.
     * <p/>
     * Returns the index of the last key value used from the propertyKey's parts (does not count the index of
     * propertyName, which is always the last part of the propertyKey).
     */
    protected int parsePropertyKey(String propertyKey)
            throws DataValidationException
    {
        int index = super.parsePropertyKey(propertyKey);
        String[] keyElements = splitPropertyKey(propertyKey);

        this.marketmaker = getKeyElement(keyElements, ++index);
       

        return index;
    }

    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {
                createBasePropertyKey(),
                getMarketMaker(),
                getPropertyName()
        };

        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
    }

    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptors.
     * @return comparator to use for sorting the returned PropertyDescriptors from getPropertyDescriptors().
     */
    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {FIRM_PROPERTY_NAME, MARKET_MAKER};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }

    public String getMarketMaker()
    {
        return marketmaker;
    }

    public void setMarketMaker(String marketMaker)
    {
        this.marketmaker = marketMaker;
        resetPropertyKey();
    }

    @Override
    protected int getMaskSize()
    {
        return super.getMaskSize() + 1;
    }

    @Override
    public int getMaskIndex(String keyElement)
    {
        int index;
        int parentSize = super.getMaskSize();
        if(keyElement.equalsIgnoreCase(MARKET_MAKER))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + MARKET_MAKER_PROPERTY_KEY_POSITION;
        }
        else
        {
            index = super.getMaskIndex(keyElement);
        }
        return index;
    }

    @Override
    public String getKeyComponentName(int maskIndex)
    {
        String fieldName;
        switch(maskIndex - super.getMaskSize())
        {
            case MARKET_MAKER_PROPERTY_KEY_POSITION:
                fieldName = MARKET_MAKER;
                break;
            default:
                fieldName = super.getKeyComponentName(maskIndex);
                break;
        }
        return fieldName;
    }
}
