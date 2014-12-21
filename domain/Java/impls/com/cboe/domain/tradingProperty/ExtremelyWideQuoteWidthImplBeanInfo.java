package com.cboe.domain.tradingProperty;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class ExtremelyWideQuoteWidthImplBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    public PropertyDescriptor[] getPropertyDescriptors()
    {

       if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[3];
            try
            {
                allowedDescriptors[0] =
                    new PropertyDescriptor("minimumBidRange", ExtremelyWideQuoteWidthImpl.class);
                allowedDescriptors[1] =
                    new PropertyDescriptor("maximumBidRange", ExtremelyWideQuoteWidthImpl.class);
                allowedDescriptors[2] =
                    new PropertyDescriptor("maximumAllowableSpread", ExtremelyWideQuoteWidthImpl.class);
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

    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}