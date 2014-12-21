// -----------------------------------------------------------------------------------
// Source file: AdjustmentTypeImpl.java
//
// PACKAGE: com.cboe.presentation.AdjustmentTypeImpl;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.interfaces.presentation.product.AdjustmentType;

/**
 * Adjustment Type implementation
 */
public class AdjustmentTypeImpl implements AdjustmentType
{
    private String description;
    private short adjType;

    private AdjustmentTypeImpl()
    {
        super();
    }

    public AdjustmentTypeImpl(String desc, short type)
    {
        this();
        this.description = desc;
        this.adjType = type;
    }

    public short getType()
    {
        return this.adjType;
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
