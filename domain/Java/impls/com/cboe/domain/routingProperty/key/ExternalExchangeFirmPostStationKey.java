package com.cboe.domain.routingProperty.key;

import java.util.Comparator;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.routingProperty.common.ExternalExchange;
import com.cboe.domain.routingProperty.common.SimpleComplexProductClass;
import com.cboe.domain.routingProperty.common.TradingSessionName;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.util.ExceptionBuilder;

public class ExternalExchangeFirmPostStationKey extends AbstractBasePropertyKey
{
    public static final String EXTERNAL_EXCHANGE_PROPERTY_NAME = "externalExchange";
    public static final String EXCHANGE_FIRM_PROPERTY_NAME = "exchangeFirm";
    public static final String POST_PROPERTY_NAME = "post";
    public static final String STATION_PROPERTY_NAME = "station";

    public static final int SESSION_KEY_POSITION  = RoutingKeyHelper.SESSION_KEY_POSITION;
    private static final int EXTERNAL_EXCHANGE_PROPERTY_KEY_POSITION = 1;
    public static final int EXCHANGE_KEY_POSITION = 2;    
    public static final int FIRM_KEY_POSITION     = 3;
    private static final int POST_PROPERTY_KEY_POSITION    = 4;
    private static final int STATION_PROPERTY_KEY_POSITION = 5;
    
    protected ExternalExchange externalExchange;
    private int post;
    private int station;
    
    public ExternalExchangeFirmPostStationKey(BasePropertyType type)
    {
        super(type);
    }
    
    public ExternalExchangeFirmPostStationKey(String propertyName, String sessionName, String destExchange, String exchangeAcronym, String firmAcronym,  int post, int station)
    {
        super(propertyName, sessionName, firmAcronym, exchangeAcronym);
        this.externalExchange = new ExternalExchange(destExchange);
        this.post = post;
        this.station = station;     
    }
    
    public ExternalExchangeFirmPostStationKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }
    
    public ExternalExchange getExternalExchange()
    {
        return externalExchange;
    }

    public int getPost()
    {
        return this.post;
    }

    public int getStation()
    {
        return this.station;
    }
    
    public void setPost(int post)
    {
        this.post = post;
        resetPropertyKey();
    }

    public void setStation(int station)
    {        
        this.station = station;
        resetPropertyKey();
    }
    
    public void setExternalExchange(ExternalExchange p_externalExchange)
    {
        externalExchange = p_externalExchange;
        resetPropertyKey();
    }
        
    public Object clone() throws CloneNotSupportedException
    {
        ExternalExchangeFirmPostStationKey newKey = (ExternalExchangeFirmPostStationKey) super.clone();
        newKey.externalExchange = new ExternalExchange(getExternalExchange().externalExchange);
        newKey.post = getPost();
        newKey.station = getStation();        
        return newKey;
    }
    
    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {  getSessionName(), new String(getExternalExchange().externalExchange),
                getExchangeAcronym(), getFirmNumber(), new Integer(getPost()), new Integer(getStation()),
                getPropertyName() };

        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
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
        int index = super.parsePropertyKey(propertyKey);

        String[] keyElements = splitPropertyKey(propertyKey);

        this.tradingSessionName = new TradingSessionName(getKeyElement(keyElements, 0));
        this.externalExchange   = new ExternalExchange(getKeyElement(keyElements, 1));
        this.exchangeAcronym = getKeyElement(keyElements, 2);
        this.firmNumber = getKeyElement(keyElements, 3);        
        
        index = 3;
        try
        {
            this.post = Integer.parseInt(getKeyElement(keyElements, ++index));
            this.station = Integer.parseInt(getKeyElement(keyElements, ++index));
        }
        catch (NumberFormatException e)
        {
            String detailMsg = buildNumberFormatExceptionMessage(propertyKey, "post or station", getKeyElement(keyElements, index));
            throw ExceptionBuilder.dataValidationException(detailMsg, DataValidationCodes.INVALID_PRODUCT_CLASS);
        }
        
        this.propertyName = getKeyElement(keyElements, keyElements.length-1);
        
        return index;
    }
    
    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptors.
     * @return comparator to use for sorting the returned PropertyDescriptors from getPropertyDescriptors().
     */
    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {TRADING_SESSION_PROPERTY_NAME,EXTERNAL_EXCHANGE_PROPERTY_NAME, FIRM_PROPERTY_NAME, POST_PROPERTY_NAME, STATION_PROPERTY_NAME};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
    
    public String getExchangeFirm()
    {
        return getFirmNumber() + "(" + getExchangeAcronym() + ")";
    }
    
    @Override
    protected int getMaskSize()
    {
        return super.getMaskSize() + 3;
    }
    
    @Override
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
        else if (keyElement.equalsIgnoreCase(EXTERNAL_EXCHANGE_PROPERTY_NAME))
        {
            index = EXTERNAL_EXCHANGE_PROPERTY_KEY_POSITION;
        }
        else if(keyElement.equalsIgnoreCase(EXCHANGE_NAME))
        {
            index = 2;
        }
        else if (keyElement.equalsIgnoreCase(FIRM_PROPERTY_NAME))
        {
            index = 3;
        }
        else if(keyElement.equalsIgnoreCase(POST_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = POST_PROPERTY_KEY_POSITION;
        }
        else if(keyElement.equalsIgnoreCase(STATION_PROPERTY_NAME))
        {
            index = STATION_PROPERTY_KEY_POSITION;
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
            case EXTERNAL_EXCHANGE_PROPERTY_KEY_POSITION:
                fieldName = EXTERNAL_EXCHANGE_PROPERTY_NAME;
                break;
            case 2:
            case 3:
                fieldName = FIRM_PROPERTY_NAME;
                break;
            case POST_PROPERTY_KEY_POSITION:
                fieldName = POST_PROPERTY_NAME;
                break;
            case STATION_PROPERTY_KEY_POSITION:
                fieldName = STATION_PROPERTY_NAME;
                break;
        }
        return fieldName;
    }
    
}
