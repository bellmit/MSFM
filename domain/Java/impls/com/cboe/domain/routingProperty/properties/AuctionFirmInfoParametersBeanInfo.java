package com.cboe.domain.routingProperty.properties;

import java.beans.SimpleBeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class AuctionFirmInfoParametersBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    public AuctionFirmInfoParametersBeanInfo()
    {

    }

    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[1];
            try
            {
                allowedDescriptors[0] =
                        new PropertyDescriptor("AuctionFirmInfoParameters", AuctionFirmInfoParameters.class);
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