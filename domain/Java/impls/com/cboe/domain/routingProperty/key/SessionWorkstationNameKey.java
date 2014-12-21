package com.cboe.domain.routingProperty.key;

import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

public class SessionWorkstationNameKey extends SessionKey
{
    public static final String WORKSTATION_NAME_PROPERTY_NAME = "WorkstationName";

    private static final int WORKSTATION_NAME_PROPERTY_KEY_POSITION = 0;

    protected String workstationName;

    public SessionWorkstationNameKey(BasePropertyType type)
    {
        super(type);
        workstationName = "";
    }

    public SessionWorkstationNameKey(String propertyName, String sessionName, String workstationName)
    {
        super(propertyName, sessionName);
        this.workstationName = workstationName;
    }

    public SessionWorkstationNameKey(String propertyKey)
            throws DataValidationException
    {
        super(propertyKey);
    }

    public Object clone() throws CloneNotSupportedException
    {
        SessionWorkstationNameKey obj = (SessionWorkstationNameKey)super.clone();
        obj.workstationName = getWorkstationName();
        return obj;
    }

    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {getSessionName(),
                getWorkstationName(), getPropertyName()};

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
        this.workstationName = getKeyElement(keyElements, ++index);

        return index;
    }

    public String getWorkstationName()
    {
        return workstationName;
    }

    public void setWorkstationName(String workstationName)
    {
        this.workstationName = workstationName;
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
        if(keyElement.equalsIgnoreCase(WORKSTATION_NAME_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + WORKSTATION_NAME_PROPERTY_KEY_POSITION;
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
            case WORKSTATION_NAME_PROPERTY_KEY_POSITION:
                fieldName = WORKSTATION_NAME_PROPERTY_NAME;
                break;
            default:
                fieldName = super.getKeyComponentName(maskIndex);
                break;
        }
        return fieldName;
    }
}
