package com.cboe.domain.routingProperty.key;

import java.util.Comparator;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.routingProperty.common.SimpleComplexProductClass;
import com.cboe.domain.routingProperty.common.TradingSessionName;
import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;

public class SessionFirmCorrClassKey extends AbstractBasePropertyKey
{
    private static final int HASH_PRIME = 11;
    
    public static final String CORRESPONDENT_PROPERTY_NAME = "correspondent";
    private static final int CORRESPONDENT_PROPERTY_KEY_POSITION = 0;
    protected String correspondent;
    public static final String PRODUCT_CLASS_PROPERTY_NAME = "simpleComplexProductClass";

    private static final int PRODUCT_CLASS_PROPERTY_KEY_POSITION = 1;

    protected SimpleComplexProductClass productClass;
    
    public SessionFirmCorrClassKey(BasePropertyType type)
    {
        super(type);
        this.correspondent = "";
        this.productClass = new SimpleComplexProductClass(getTradingSession().sessionName, 0);
    }

    public SessionFirmCorrClassKey(String propertyName, String sessionName, String firmAcronym, String exchangeAcronym,
                                 String correspondent, int classKey)
    {
        super(propertyName, sessionName, firmAcronym, exchangeAcronym);
        this.correspondent  = correspondent;
        this.productClass = new SimpleComplexProductClass(sessionName, classKey);
    }

    public SessionFirmCorrClassKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }

    public Object clone() throws CloneNotSupportedException
    {
        SessionFirmCorrClassKey newKey =  (SessionFirmCorrClassKey) super.clone();
        newKey.correspondent = getCorrespondent();
        newKey.productClass = new SimpleComplexProductClass(getTradingSession().sessionName, getClassKey());
        
        return newKey;
    }

    public String getCorrespondent()
    {
        return correspondent;
    }

    public void setCorrespondent(String correspondent)
    {
        this.correspondent = correspondent;
        resetPropertyKey();
    }

    public int getClassKey()
    {
        return productClass.getClassKey();
    }

    public void setClassKey(int classKey)
    {
        this.productClass = new SimpleComplexProductClass(getTradingSession().sessionName, classKey);
        resetPropertyKey();
    }

     public SimpleComplexProductClass getSimpleComplexProductClass()
    {
        return productClass;
    }

    public void setSimpleComplexProductClass(SimpleComplexProductClass productClass)
    {
        setTradingSession(new TradingSessionName(productClass.getTradingSession()));
        this.productClass = productClass;
        resetPropertyKey();
    }


    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {TRADING_SESSION_PROPERTY_NAME, FIRM_PROPERTY_NAME,
                                  CORRESPONDENT_PROPERTY_NAME, PRODUCT_CLASS_PROPERTY_NAME};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
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

        this.correspondent = getKeyElement(keyElements, ++index);

        try
        {
            this.productClass = new SimpleComplexProductClass(tradingSessionName.sessionName,
                                                Integer.parseInt(getKeyElement(keyElements, ++index)));
        }
        catch (NumberFormatException e)
        {
            String detailMsg = buildNumberFormatExceptionMessage(propertyKey, "classKey",
                                                              getKeyElement(keyElements, index));
            throw ExceptionBuilder.dataValidationException(detailMsg, DataValidationCodes.INVALID_PRODUCT_CLASS);
        }

        return index;
    }

    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {createBasePropertyKey(),
                                        getCorrespondent(), getClassKey(), getPropertyName()};

        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
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
        if(keyElement.equalsIgnoreCase(CORRESPONDENT_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + CORRESPONDENT_PROPERTY_KEY_POSITION;
        }
        else if(keyElement.equalsIgnoreCase(PRODUCT_CLASS_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + PRODUCT_CLASS_PROPERTY_KEY_POSITION;
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
            case PRODUCT_CLASS_PROPERTY_KEY_POSITION:
                fieldName = PRODUCT_CLASS_PROPERTY_NAME;
                break;
            case CORRESPONDENT_PROPERTY_KEY_POSITION:
                fieldName = CORRESPONDENT_PROPERTY_NAME;
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
        return (HASH_PRIME + this.tradingSessionName.sessionName.hashCode() + this.firmNumber.hashCode()
                    + this.exchangeAcronym.hashCode() + this.correspondent.hashCode() + this.productClass.getClassKey());
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
       
        final SessionFirmCorrClassKey theKey = (SessionFirmCorrClassKey)anObject;
        if (theKey != null)
        {
            return (this.tradingSessionName.sessionName.equals(theKey.getTradingSession().sessionName) &&
                    this.firmNumber.equals(theKey.getFirmNumber()) &&
                    this.exchangeAcronym.equals(theKey.getExchangeAcronym()) && this.correspondent.equals(theKey.getCorrespondent()) &&
                    this.productClass.getClassKey() == theKey.getSimpleComplexProductClass().getClassKey());
    
        }
        return false;
    }
}
