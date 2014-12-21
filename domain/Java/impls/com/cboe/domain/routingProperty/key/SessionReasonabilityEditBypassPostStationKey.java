package com.cboe.domain.routingProperty.key;

import java.util.Comparator;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.routingProperty.common.TradingSessionName;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.util.ExceptionBuilder;

public class SessionReasonabilityEditBypassPostStationKey extends SessionKey
{
    private static final int HASH_PRIME_POST = 31;
    private static final int HASH_PRIME_STATION = 37;

    public static final String POST_PROPERTY_NAME = "post";
    public static final String STATION_PROPERTY_NAME = "station";

    private static final int POST_PROPERTY_KEY_POSITION    = 0;
    private static final int STATION_PROPERTY_KEY_POSITION = 1;

    private int post;
    private int station;
    
 public SessionReasonabilityEditBypassPostStationKey(BasePropertyType type)
 {
     super(type);
 }

 public SessionReasonabilityEditBypassPostStationKey(String propertyName, String sessionName,int post, int station)
 {
     super(propertyName, sessionName);
     this.post = post;
     this.station = station;
 }

 public SessionReasonabilityEditBypassPostStationKey(String propertyKey) throws DataValidationException
 {
     super(propertyKey);
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
     SessionReasonabilityEditBypassPostStationKey newKey = (SessionReasonabilityEditBypassPostStationKey) super.clone();
     newKey.post = getPost();
     newKey.station = getStation();
     return newKey;
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
     Object[] propertyKeyElements = {getSessionName(),
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
     

     String[] keyElements = splitPropertyKey(propertyKey);
     int index =0;
     try
     {
         this.tradingSessionName = new TradingSessionName(getKeyElement(keyElements, 0));
         this.post = Integer.parseInt(getKeyElement(keyElements, ++index));
         this.station = Integer.parseInt(getKeyElement(keyElements, ++index));
         this.propertyName = getKeyElement(keyElements, keyElements.length-1);
     }
     catch (NumberFormatException e)
     {
         String detailMsg = buildNumberFormatExceptionMessage(propertyKey, "post or station",
                                                              getKeyElement(keyElements, index));
         throw ExceptionBuilder.dataValidationException(detailMsg, DataValidationCodes.INVALID_PRODUCT_CLASS);
     }

     return 0;
 }

 
 
 /**
  * Allows the Routing Property to determine the order of the PropertyDescriptors.
  * @return comparator to use for sorting the returned PropertyDescriptors from getPropertyDescriptors().
  */
 public Comparator getPropertyDescriptorSortComparator()
 {
     String[] forcedEntries = {TRADING_SESSION_PROPERTY_NAME, POST_PROPERTY_NAME, STATION_PROPERTY_NAME };
     return new ForcedPropertyDescriptorComparator(forcedEntries);
 }

    @Override
    protected int getMaskSize()
    {
        return super.getMaskSize() + 2;
    }

    @Override
    public int getMaskIndex(String keyElement)
    {
        int index;
        int parentSize = super.getMaskSize();
        if(keyElement.equalsIgnoreCase(POST_PROPERTY_NAME))
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
    
    @Override
    public int hashCode()
    {
        return (this.tradingSessionName.sessionName.hashCode() + HASH_PRIME_POST * this.post + HASH_PRIME_STATION * this.station);
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
        
        final SessionReasonabilityEditBypassPostStationKey theKey = (SessionReasonabilityEditBypassPostStationKey)anObject;
        if (theKey != null)
        {
            return (this.tradingSessionName.sessionName.equals(theKey.getTradingSession().sessionName) &&
                    this.post == theKey.getPost() && this.station == theKey.getStation());
    
        }
        return false;
    }
}
