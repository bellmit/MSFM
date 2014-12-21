// -----------------------------------------------------------------------------------
// Source file: AdjustmentOrderActionImpl.java
//
// PACKAGE: com.cboe.internalPresentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.product;

import com.cboe.interfaces.internalPresentation.product.AdjustmentOrderAction;

/**
 * AdjustmentOrderAction implementation
 */
public class AdjustmentOrderActionImpl implements AdjustmentOrderAction
{
    private String description;
    private short adjAction;

    private AdjustmentOrderActionImpl()
    {
        super();
    }

    public AdjustmentOrderActionImpl(String desc, short action)
    {
        this();
        this.description = desc;
        this.adjAction = action;
    }

    public short getAction()
    {
        return this.adjAction;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String toString()
    {
        return getDescription();
    }
}
