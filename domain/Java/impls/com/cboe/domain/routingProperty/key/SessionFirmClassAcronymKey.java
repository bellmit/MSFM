package com.cboe.domain.routingProperty.key;

//--------------------------------------------------------------------------------

import java.util.Comparator;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

public class SessionFirmClassAcronymKey extends SessionFirmClassKey
{
    public static final String ACRONYM_PROPERTY_NAME = "brokerAcronym";
    private static final int ACRONYM_PROPERTY_KEY_POSITION = 0;

    protected String brokerAcronym;

    public SessionFirmClassAcronymKey(BasePropertyType type)
    {
        super(type);
    }

    public SessionFirmClassAcronymKey(String propertyName, String sessionName, String firmAcronym,
            String exchangeAcronym,  int classKey, String brokerAcronym)
    {
        super(propertyName, firmAcronym, exchangeAcronym, sessionName, classKey);

        this.brokerAcronym = brokerAcronym;
    }

    public SessionFirmClassAcronymKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }

    public Object clone() throws CloneNotSupportedException
    {
        SessionFirmClassAcronymKey newKey = (SessionFirmClassAcronymKey) super.clone();
        newKey.brokerAcronym = getBrokerAcronym();

        return newKey;
    }

    /**
     * Parses the propertyKey to find the separate key values.
     * 
     * Returns the index of the last key value used from the propertyKey's parts (does not count the
     * index of propertyName, which is always the last part of the propertyKey).
     * 
     * @param propertyKey
     * @throws DataValidationException
     */
    protected int parsePropertyKey(String propertyKey) throws DataValidationException
    {
        int index = super.parsePropertyKey(propertyKey);
        String[] keyElements = splitPropertyKey(propertyKey);

        this.brokerAcronym = getKeyElement(keyElements, ++index);

        return index;
    }

    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = { createBasePropertyKey(), new Integer(getClassKey()),
                getBrokerAcronym(), getPropertyName() };

        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
    }

    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptors.
     * 
     * @return comparator to use for sorting the returned PropertyDescriptors from
     * getPropertyDescriptors().
     */
    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = { FIRM_PROPERTY_NAME, PRODUCT_CLASS_PROPERTY_NAME,
                ACRONYM_PROPERTY_NAME };
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }

    public String getBrokerAcronym()
    {
        return brokerAcronym;
    }

    public void setBrokerAcronym(String brokerAcronym)
    {
        this.brokerAcronym = brokerAcronym;
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
        if(keyElement.equalsIgnoreCase(ACRONYM_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + ACRONYM_PROPERTY_KEY_POSITION;
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
            case ACRONYM_PROPERTY_KEY_POSITION:
                fieldName = ACRONYM_PROPERTY_NAME;
                break;
            default:
                fieldName = super.getKeyComponentName(maskIndex);
                break;
        }
        return fieldName;
    }
}