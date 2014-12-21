//
// -----------------------------------------------------------------------------------
// Source file: BookDepthUpdatePrice.java
//
// PACKAGE: com.cboe.interfaces.presentation.bookDepth
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.bookDepth;

/*
 * Provides a contract that provides book depth updates for prices and qty's
 */
public interface BookDepthUpdatePrice extends OrderBookPrice
{
    /**
     * Gets the update type
     * @param char as defined in com.cboe.idl.cmiConstants.BookDepthUpdateType
     */
    public char getUpdateType();
}
