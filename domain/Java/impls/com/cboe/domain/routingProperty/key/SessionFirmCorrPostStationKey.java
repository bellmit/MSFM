package com.cboe.domain.routingProperty.key;

import java.util.Comparator;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.util.ExceptionBuilder;

public class SessionFirmCorrPostStationKey extends AbstractBasePropertyKey
{   
    private static final int HASH_PRIME_POST = 31;
    private static final int HASH_PRIME_STATION = 37;
    
    public static final String CORRESPONDENT_PROPERTY_NAME = "correspondent";
    private static final int CORRESPONDENT_PROPERTY_KEY_POSITION = 0;
    protected String correspondent;
    
    public static final String POST_PROPERTY_NAME = "post";
    public static final String STATION_PROPERTY_NAME = "station";

    private static final int POST_PROPERTY_KEY_POSITION    = 1;
    private static final int STATION_PROPERTY_KEY_POSITION = 2;

    private int post;
    private int station;

    public SessionFirmCorrPostStationKey(BasePropertyType type)
    {
        super(type);
    }

    public SessionFirmCorrPostStationKey(String propertyName, String sessionName, String firmAcronym, String exchangeAcronym,
            String correspondent, int post, int station)
    {
        super(propertyName, sessionName, firmAcronym, exchangeAcronym);    
        this.correspondent = correspondent;
        this.post = post;
        this.station = station;
    }

    public SessionFirmCorrPostStationKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }

    public String getCorrespondent()
    {
        return this.correspondent;
    }
    
    public int getPost()
    {
        return this.post;
    }

    public int getStation()
    {
        return this.station;
    }

    public Object clone() throws CloneNotSupportedException
    {
        SessionFirmCorrPostStationKey newKey = (SessionFirmCorrPostStationKey) super.clone();
        newKey.correspondent = getCorrespondent();
        newKey.post = getPost();
        newKey.station = getStation();
        return newKey;
    }

    public void setCorrespondent(String correspondent)
    {
        this.correspondent = correspondent;
        resetPropertyKey();
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

    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {createBasePropertyKey(), getCorrespondent(),
                                        new Integer(getPost()), new Integer(getStation()), getPropertyName()};

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

        try
        {
            this.correspondent = getKeyElement(keyElements, ++index);
            this.post = Integer.parseInt(getKeyElement(keyElements, ++index));
            this.station = Integer.parseInt(getKeyElement(keyElements, ++index));
        }
        catch (NumberFormatException e)
        {
            String detailMsg = buildNumberFormatExceptionMessage(propertyKey, "post or station",
                                                                 getKeyElement(keyElements, index));
            throw ExceptionBuilder.dataValidationException(detailMsg, DataValidationCodes.INVALID_PRODUCT_CLASS);
        }

        return index;
    }

    @Override
    protected int getMaskSize()
    {
        return super.getMaskSize() + 3;
    }

    @Override
    public int getMaskIndex(String keyElement)
    {
        int index;
        int parentSize = super.getMaskSize();
        if (keyElement.equalsIgnoreCase(CORRESPONDENT_PROPERTY_NAME))
        {
            index = parentSize + CORRESPONDENT_PROPERTY_KEY_POSITION;
        }
        else if(keyElement.equalsIgnoreCase(POST_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + POST_PROPERTY_KEY_POSITION;
        }
        else if(keyElement.equalsIgnoreCase(STATION_PROPERTY_NAME))
        {
            index = parentSize + STATION_PROPERTY_KEY_POSITION;
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
            case CORRESPONDENT_PROPERTY_KEY_POSITION:
                fieldName = CORRESPONDENT_PROPERTY_NAME;
                break;
            case POST_PROPERTY_KEY_POSITION:
                fieldName = POST_PROPERTY_NAME;
                break;
            case STATION_PROPERTY_KEY_POSITION:
                fieldName = STATION_PROPERTY_NAME;
                break;
            default:
                fieldName = super.getKeyComponentName(maskIndex);
                break;
        }
        return fieldName;
    }

    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptors.
     * @return comparator to use for sorting the returned PropertyDescriptors from getPropertyDescriptors().
     */
    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {TRADING_SESSION_PROPERTY_NAME, FIRM_PROPERTY_NAME, 
                CORRESPONDENT_PROPERTY_NAME, POST_PROPERTY_NAME, STATION_PROPERTY_NAME};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
    
    @Override
    public int hashCode()
    {
        return (this.tradingSessionName.sessionName.hashCode() + this.firmNumber.hashCode()
                + this.exchangeAcronym.hashCode() + this.correspondent.hashCode() + HASH_PRIME_POST * this.post + HASH_PRIME_STATION * this.station);
    }

    @Override
    public boolean equals(Object anObject)
    {
        if (this == anObject) {
            return true;
        }
        if (anObject == null) {
            return false;
        }
        if (getClass() != anObject.getClass()) {
            return false;
        }
        
        final SessionFirmCorrPostStationKey theKey = (SessionFirmCorrPostStationKey)anObject;
        if (theKey != null)
        {
            return (this.tradingSessionName.sessionName.equals(theKey.getTradingSession().sessionName) && this.firmNumber.equals(theKey.getFirmNumber()) &&
                    this.exchangeAcronym.equals(theKey.getExchangeAcronym()) && this.correspondent.equals(theKey.getCorrespondent()) &&
                    this.post == theKey.getPost() && this.station == theKey.getStation());
    
        }
        return false;
    }
    
}
