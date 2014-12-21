package com.cboe.domain.routingProperty.properties;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroupBeanInfo;
import com.cboe.domain.routingProperty.key.ExternalExchangeFirmClassKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class LinkageRouterAssignmentGroupBeanInfo extends AbstractRoutingPropertyGroupBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
     * Hides all PropertyDesciptor's for the represented class
     * @return an empty array
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if (allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[4];
            try
            {
                allowedDescriptors[0] =
                        new PropertyDescriptor(ExternalExchangeFirmClassKey.TRADING_SESSION_PROPERTY_NAME, ExternalExchangeFirmClassKey.class);

                allowedDescriptors[1] =
                        new PropertyDescriptor(ExternalExchangeFirmClassKey.EXTERNAL_EXCHANGE_PROPERTY_NAME, ExternalExchangeFirmClassKey.class);

                allowedDescriptors[2] =
                        new PropertyDescriptor(ExternalExchangeFirmClassKey.EXCHANGE_FIRM_PROPERTY_NAME, ExternalExchangeFirmClassKey.class, "getExchangeFirm", null);

                allowedDescriptors[3] =
                        new PropertyDescriptor(ExternalExchangeFirmClassKey.PRODUCT_CLASS_PROPERTY_NAME, ExternalExchangeFirmClassKey.class);

            }
            catch (IntrospectionException e)
            {
                Log.exception("Could not create PropertyDescriptor.", e);
                allowedDescriptors = null;
                return super.getPropertyDescriptors();
            }
        }
        return allowedDescriptors;
    }
}
