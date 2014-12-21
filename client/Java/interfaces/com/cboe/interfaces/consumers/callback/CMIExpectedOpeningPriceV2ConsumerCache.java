//
// -----------------------------------------------------------------------------------
// Source file: CMIExpectedOpeningPriceV2ConsumerCache.java
//
// PACKAGE: com.cboe.interfaces.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.consumers.callback;

import com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer;

import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.presentation.product.SessionProductClass;

public interface CMIExpectedOpeningPriceV2ConsumerCache extends CallbackConsumerCache
{
    public CMIExpectedOpeningPriceConsumer getExpectedOpeningPriceConsumer(SessionKeyWrapper key);
    public CMIExpectedOpeningPriceConsumer getExpectedOpeningPriceConsumer(SessionProductClass productClass);
    public CMIExpectedOpeningPriceConsumer getExpectedOpeningPriceConsumer(String sessionName, int classKey);
    public CMIExpectedOpeningPriceConsumer getExpectedOpeningPriceConsumerForProduct(String sessionName, int productKey);
}