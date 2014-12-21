package com.cboe.domain.routingProperty.properties;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.domain.routingProperty.key.SessionFirmKeyMarketMaker;

public class EtnIndicatorGroupBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    public EtnIndicatorGroupBeanInfo()
    {
    }

    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if (allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[1];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor(SessionFirmKeyMarketMaker.MARKET_MAKER,
                                                               SessionFirmKeyMarketMaker.class);
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

    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}
