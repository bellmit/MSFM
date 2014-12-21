package com.cboe.domain.routingProperty.key;

import java.util.Comparator;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.routingProperty.common.SimpleComplexProductClass;
import com.cboe.domain.routingProperty.common.TradingSessionName;
import com.cboe.domain.routingProperty.common.ExternalExchange;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.util.ExceptionBuilder;

public class ExternalExchangeFirmClassKey extends AbstractBasePropertyKey
{
    public static final String PRODUCT_CLASS_PROPERTY_NAME = "simpleComplexProductClass";
    public static final String EXTERNAL_EXCHANGE_PROPERTY_NAME = "externalExchange";
    public static final String EXCHANGE_FIRM_PROPERTY_NAME = "exchangeFirm";

    private static final int EXTERNAL_EXCHANGE_PROPERTY_KEY_POSITION = 1;
    public static final int SESSION_KEY_POSITION  = RoutingKeyHelper.SESSION_KEY_POSITION;
    public static final int FIRM_KEY_POSITION     = 3;
    public static final int EXCHANGE_KEY_POSITION = 2;
    private static final int PRODUCT_CLASS_PROPERTY_KEY_POSITION = 4;
    
    protected SimpleComplexProductClass productClass;
    protected ExternalExchange externalExchange;

    public ExternalExchangeFirmClassKey(BasePropertyType type)
    {
        super(type);
    }
    
    public ExternalExchangeFirmClassKey(String propertyName, String sessionName, String destExchange, String exchangeAcronym, String firmAcronym,  int classKey)
    {
        super(propertyName, sessionName, firmAcronym, exchangeAcronym);
        this.externalExchange = new ExternalExchange(destExchange);
        this.productClass = new SimpleComplexProductClass(sessionName, classKey);        
    }
    
    public ExternalExchangeFirmClassKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }
    
    public ExternalExchange getExternalExchange()
    {
        return externalExchange;
    }

    public void setExternalExchange(ExternalExchange p_externalExchange)
    {
        externalExchange = p_externalExchange;
        resetPropertyKey();
    }

    public int getClassKey()
    {
        return productClass.getClassKey();
    }
    
    public Object clone() throws CloneNotSupportedException
    {
        ExternalExchangeFirmClassKey newKey = (ExternalExchangeFirmClassKey) super.clone();
        newKey.productClass = new SimpleComplexProductClass(getTradingSession().sessionName, getClassKey());
        newKey.externalExchange = new ExternalExchange(getExternalExchange().externalExchange);
        return newKey;
    }
    
    public void setClassKey(int classKey)
    {
        this.productClass = new SimpleComplexProductClass(getTradingSession().sessionName, classKey);
        resetPropertyKey();
    }
    public void setSimpleComplexProductClass(SimpleComplexProductClass productClass)
    {
        this.productClass = productClass;
        resetPropertyKey();
    }

    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {  getSessionName(), new String(getExternalExchange().externalExchange),
                getExchangeAcronym(), getFirmNumber(), new Integer(getClassKey()),
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
        
        try
        {
            this.productClass = new SimpleComplexProductClass(tradingSessionName.sessionName,
                                                              Integer.parseInt(getKeyElement(keyElements, 4)));
        }
        catch (NumberFormatException e)
        {
            String detailMsg = buildNumberFormatExceptionMessage(propertyKey, "classKey",
                                                                 getKeyElement(keyElements, 4));
            throw ExceptionBuilder.dataValidationException(detailMsg, DataValidationCodes.INVALID_PRODUCT_CLASS);
        }
        
        this.propertyName = getKeyElement(keyElements, keyElements.length-1);
        
        return 4;
    }

    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptors.
     * @return comparator to use for sorting the returned PropertyDescriptors from getPropertyDescriptors().
     */
    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {TRADING_SESSION_PROPERTY_NAME,EXTERNAL_EXCHANGE_PROPERTY_NAME, FIRM_PROPERTY_NAME, PRODUCT_CLASS_PROPERTY_NAME };
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
    
    public SimpleComplexProductClass getSimpleComplexProductClass()
    {
        return productClass;
    }

    public String getExchangeFirm()
    {
        return getFirmNumber() + "(" + getExchangeAcronym() + ")";
    }
    
    @Override
    protected int getMaskSize()
    {
        return super.getMaskSize() + 2;
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
        else if (keyElement.equalsIgnoreCase(PRODUCT_CLASS_PROPERTY_NAME))
        {
            index = PRODUCT_CLASS_PROPERTY_KEY_POSITION;
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
            case PRODUCT_CLASS_PROPERTY_KEY_POSITION:
                fieldName = PRODUCT_CLASS_PROPERTY_NAME;
                break;
        }
        return fieldName;
    }
    
}
