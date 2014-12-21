//
// -----------------------------------------------------------------------------------
// Source file: AbstractTradingPropertyBeanInfo.java
//
// PACKAGE: com.cboe.domain.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import java.beans.SimpleBeanInfo;
import java.beans.PropertyDescriptor;

/**
 * Exposes a limited set of BeanInfo for a AbstractTradingProperty
 */
public class AbstractTradingPropertyBeanInfo extends SimpleBeanInfo
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
