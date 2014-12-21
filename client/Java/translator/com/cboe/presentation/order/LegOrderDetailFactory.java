//
// -----------------------------------------------------------------------------------
// Source file: LegOrderDetailFactory.java
//
// PACKAGE: com.cboe.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import com.cboe.idl.cmiOrder.LegOrderDetailStruct;

import com.cboe.interfaces.presentation.order.LegOrderDetail;
import com.cboe.interfaces.presentation.order.MutableLegOrderDetail;

public class LegOrderDetailFactory
{
    public static LegOrderDetail createLegOrderDetail(LegOrderDetailStruct legOrderDetailStruct)
    {
        return new LegOrderDetailImpl(legOrderDetailStruct);
    }

    public static LegOrderDetail createLegOrderDetail(int productKey)
    {
        return new LegOrderDetailImpl(productKey);
    }

    public static MutableLegOrderDetail createMutableLegOrderDetail(LegOrderDetailStruct legOrderDetailStruct)
    {
        return (MutableLegOrderDetail)createLegOrderDetail(legOrderDetailStruct);
    }

    public static MutableLegOrderDetail createMutableLegOrderDetail(int productKey)
    {
        return (MutableLegOrderDetail)createLegOrderDetail(productKey);
    }
}