package com.cboe.domain.routingProperty.properties;
//-----------------------------------------------------------------------------------
//Source file: DirectedAIMFirmRegistrationGroupBeanInfo
//
//PACKAGE: com.cboe.domain.routingProperty.properties
//
//Created: Jan 22, 2008
//-----------------------------------------------------------------------------------
//Copyright (c) 2009 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------

import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroupBeanInfo;
import com.cboe.domain.routingProperty.key.SessionFirmCorrBranchKey;
import com.cboe.domain.routingProperty.key.SessionFirmClassKey;
import com.cboe.domain.routingProperty.key.SessionFirmSimpleComplexClassKey;
import com.cboe.domain.routingProperty.key.SessionAffiliatedFirmClassKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/*
    Note that this class is required, even though it is empty. Without it, introspection will return all fields of the
    DirectedAIMFirmRegistrationGroup class to be shown as key columns in the  property table, while with this,
    it will not show any keys, just the value, because the only keys are session and firm which are already
    determined by the parent nodes in the session-firm firm property navigation tree.
 */
@SuppressWarnings({"EmptyClass"})
public class DirectedAIMFirmRegistrationGroupBeanInfo extends AbstractRoutingPropertyGroupBeanInfo
{

    private PropertyDescriptor[] allowedDescriptors;

    public PropertyDescriptor[] getPropertyDescriptors()
    {
        if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[1];
            try
            {
                allowedDescriptors[0] = new PropertyDescriptor(SessionAffiliatedFirmClassKey.PRODUCT_CLASS_PROPERTY_NAME,
                                                               SessionAffiliatedFirmClassKey.class);
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
