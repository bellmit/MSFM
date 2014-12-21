package com.cboe.internalPresentation.productStateEventHistory.cache;
// -----------------------------------------------------------------------------------
// Source file: PSEventHistoryCacheEventListener
//
// PACKAGE: com.cboe.internalPresentation.productStateEventHistory
// 
// Created: Mar 13, 2006 1:15:00 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public interface PSEventHistoryCacheEventListener
{
    public void cacheUpdate(PSEventHistoryCacheEvent e);
}
