// -----------------------------------------------------------------------------------
// Source file: AdjustmentType.java
//
// PACKAGE: com.cboe.interfaces.presentation.AdjustmentType;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.product;

/**
 * Adjustment Type Interface
 */
public interface AdjustmentType
{
    public short getType();
    public String getDescription();
    public String toString();
}
