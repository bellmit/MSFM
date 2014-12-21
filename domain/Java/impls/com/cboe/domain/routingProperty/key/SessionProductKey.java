package com.cboe.domain.routingProperty.key;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

import com.cboe.domain.routingProperty.common.TradingSessionName;

public class SessionProductKey extends AbstractBasePropertyKey
{
    public static final String PRODUCT_KEY_PROPERTY_NAME = "productKey";

    private static final int PRODUCT_KEY_PROPERTY_KEY_POSITION = 0;

    protected int productKey;

    public int getProductKey(){
        return productKey;
    }

    public SessionProductKey(BasePropertyType type)
    {
        super(type);
    }

    public SessionProductKey(String propertyName,String sessionName,int productKey)
    {
        super(propertyName, sessionName, "", "");
        this.productKey = productKey;
    }

    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {getSessionName(),
                getProductKey(), getPropertyName()};

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

        this.tradingSessionName = new TradingSessionName(getKeyElement(keyElements, 0));
        this.productKey = Integer.getInteger(getKeyElement(keyElements, 1));

        this.propertyName = getKeyElement(keyElements, keyElements.length-1);

        return 1;
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
        if(keyElement.equalsIgnoreCase(PRODUCT_KEY_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + PRODUCT_KEY_PROPERTY_KEY_POSITION;
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
            case PRODUCT_KEY_PROPERTY_KEY_POSITION:
                fieldName = PRODUCT_KEY_PROPERTY_NAME;
                break;
            default:
                fieldName = super.getKeyComponentName(maskIndex);
                break;
        }
        return fieldName;
    }
}
