package com.cboe.domain.routingProperty.key;

import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

public class SessionRouterVendorKey extends SessionKey
{
    public static final String ROUTER_VENDOR_PROPERTY_NAME = "RouterVendor";

    private static final int ROUTER_VENDOR_PROPERTY_KEY_POSITION = 0;

    protected String routerVendor;

    public SessionRouterVendorKey(BasePropertyType p_type)
    {
        super(p_type);
        routerVendor = "";
    }

    public SessionRouterVendorKey(String propertyName, String sessionName, String routerVendorName)
    {
        super(propertyName, sessionName);
        routerVendor = routerVendorName;
    }

    public SessionRouterVendorKey(String p_propertyKey) throws DataValidationException
    {
        super(p_propertyKey);
       
    }
    
    public Object clone() throws CloneNotSupportedException
    {
        SessionRouterVendorKey obj = (SessionRouterVendorKey)super.clone();
        obj.routerVendor = getRouterVendor();
        return obj;
    }

    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {getSessionName(),
                getRouterVendor(), getPropertyName()};

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
        routerVendor = getKeyElement(keyElements, ++index);

        return index;
    }
    
    public String getRouterVendor()
    {
        return routerVendor;
    }

    public void setRouterVendor(String p_routerVendor)
    {
        routerVendor = p_routerVendor;
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
        if(keyElement.equalsIgnoreCase(ROUTER_VENDOR_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + ROUTER_VENDOR_PROPERTY_KEY_POSITION;
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
            case ROUTER_VENDOR_PROPERTY_KEY_POSITION:
                fieldName = ROUTER_VENDOR_PROPERTY_NAME;
                break;
            default:
                fieldName = super.getKeyComponentName(maskIndex);
                break;
        }
        return fieldName;
    }
    
    
}
