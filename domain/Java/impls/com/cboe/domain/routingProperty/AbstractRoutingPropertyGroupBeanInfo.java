package com.cboe.domain.routingProperty;
// -----------------------------------------------------------------------------------
// Source file: AbstractRoutingPropertyGroupBeanInfo
//
// PACKAGE: com.cboe.domain.routingProperty
// 
// Created: Aug 2, 2006 9:00:24 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * Exposes a limited set of BeanInfo for a AbstractTradingPropertyGroup
 */
public class AbstractRoutingPropertyGroupBeanInfo extends SimpleBeanInfo
{
    /**
     * Hides all PropertyDesciptor's for the represented class
     * @return an empty array
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        return new PropertyDescriptor[0];
    }
}
