//
// -----------------------------------------------------------------------------------
// Source file: CBOEId.java
//
// PACKAGE: com.cboe.interfaces.presentation.util;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.util;

import com.cboe.idl.cmiUtil.CboeIdStruct;

/*
 * Defines the contract a wrapper for the CboeIdStruct will provide
 */
public interface CBOEId extends Cloneable, Comparable
{
    /**
     * Gets the representing struct
     */
    public CboeIdStruct getStruct();

    /**
     * Gets the high id
     */
    public int getHighId();

    /**
     * Gets the low id
     */
    public int getLowId();

    /**
     * Implement clone
     */
    public Object clone() throws CloneNotSupportedException;
}