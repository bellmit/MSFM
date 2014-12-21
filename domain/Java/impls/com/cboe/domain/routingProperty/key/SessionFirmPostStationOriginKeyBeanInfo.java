package com.cboe.domain.routingProperty.key;
//-----------------------------------------------------------------------------------
//Source file: SessionFirmPostStationOriginKeyBeanInfo
//
//PACKAGE: com.cboe.domain.routingProperty.key
//
//Created: Aug 8, 2006 7:00:26 AM
//-----------------------------------------------------------------------------------
//Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
* Exposes a limited set of BeanInfo for a SimpleBooleanTradingPropertyImpl
*/
public class SessionFirmPostStationOriginKeyBeanInfo extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
    * Returns the property descriptor for the booleanValue method only
    */
    @Override
    @SuppressWarnings({"MethodWithMultipleReturnPoints"})
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if (allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[5];
            try
            {
                allowedDescriptors[0] =
                     new PropertyDescriptor(SessionFirmPostStationOriginKey.TRADING_SESSION_PROPERTY_NAME, SessionFirmPostStationOriginKey.class);

                allowedDescriptors[1] =
                     new PropertyDescriptor(SessionFirmPostStationOriginKey.FIRM_PROPERTY_NAME, SessionFirmPostStationOriginKey.class);

                allowedDescriptors[2] =
                 new PropertyDescriptor(SessionFirmPostStationOriginKey.POST_PROPERTY_NAME, SessionFirmPostStationOriginKey.class);
                
                allowedDescriptors[3] =
                 new PropertyDescriptor(SessionFirmPostStationOriginKey.STATION_PROPERTY_NAME, SessionFirmPostStationOriginKey.class);

                allowedDescriptors[4] =
                 new PropertyDescriptor(SessionFirmPostStationOriginKey.ORIGIN_CODE_PROPERTY_NAME, SessionFirmPostStationOriginKey.class);
            }
            catch (IntrospectionException e)
            {
                Log.exception("Could not create PropertyDescriptor.", e);
                allowedDescriptors = null;
                return super.getPropertyDescriptors();
            }
        }
    //noinspection ReturnOfCollectionOrArrayField
    return allowedDescriptors;
    }

    @Override
    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}
