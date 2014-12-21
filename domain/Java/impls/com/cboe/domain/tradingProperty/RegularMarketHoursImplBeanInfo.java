package com.cboe.domain.tradingProperty;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class RegularMarketHoursImplBeanInfo extends SimpleBeanInfo
{
	private PropertyDescriptor[] allowedDescriptors;

    /**
     * Returns the property descriptors for minimumBidRange, maximumBidRange, and maximumAllowableSpread methods only
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[2];
            try
            {
                allowedDescriptors[0] =
                    new PropertyDescriptor("openTime", RegularMarketHoursImpl.class);
                allowedDescriptors[1] =
                    new PropertyDescriptor("closeTime", RegularMarketHoursImpl.class);
            }
            catch(IntrospectionException e)
            {
                Log.exception("Could not create PropertyDescriptor.", e);
                allowedDescriptors = null;
                return super.getPropertyDescriptors();
            }
        }
        return allowedDescriptors;
    }

    /**
     * Returns the default property index for the minimumBidRange.
     */
    public int getDefaultPropertyIndex()
    {
        return -1;
    }
}
