//
//-----------------------------------------------------------------------------------
//Source file: EligibleCOAPostStationGroupBeanInfo.java
//
//PACKAGE: com.cboe.domain.routingProperty.properties
//
//-----------------------------------------------------------------------------------
//Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.properties;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroupBeanInfo;
import com.cboe.domain.routingProperty.key.SessionFirmPostStationOriginKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
* Uses the same PropertyDescriptors as DefaultDestinationGroupBeanInfo.
*/
public class EligibleCOAPostStationGroupBeanInfo extends AbstractRoutingPropertyGroupBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    /**
    * Hides all PropertyDesciptor's for the represented class
    * @return an empty array
    */
    @Override
    @SuppressWarnings({"MethodWithMultipleReturnPoints"})
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[3];
            try
            {
                allowedDescriptors[0] =
                     new PropertyDescriptor(SessionFirmPostStationOriginKey.POST_PROPERTY_NAME, SessionFirmPostStationOriginKey.class);

                allowedDescriptors[1] =
                 new PropertyDescriptor(SessionFirmPostStationOriginKey.STATION_PROPERTY_NAME, SessionFirmPostStationOriginKey.class);

                allowedDescriptors[2] =
                     new PropertyDescriptor(SessionFirmPostStationOriginKey.ORIGIN_CODE_PROPERTY_NAME, SessionFirmPostStationOriginKey.class);
            }
            catch(IntrospectionException e)
            {
                Log.exception("Could not create PropertyDescriptor.", e);
                allowedDescriptors = null;
                return super.getPropertyDescriptors();
            }
        }
        //noinspection ReturnOfCollectionOrArrayField
        return allowedDescriptors;
    }

    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}
