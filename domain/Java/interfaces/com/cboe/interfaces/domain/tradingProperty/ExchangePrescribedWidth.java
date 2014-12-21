//
// -----------------------------------------------------------------------------------
// Source file: ExchangePrescribedWidth.java
//
// PACKAGE: com.cboe.interfaces.domain.tradingProperty;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.tradingProperty;

public interface ExchangePrescribedWidth extends TradingProperty
{
    void setMinimumBidRange(double minBid);
    void setMaximumBidRange(double maxBid);
    void setMaximumAllowableSpread(double maxSpread);
    double getMinimumBidRange();
    double getMaximumBidRange();
    double getMaximumAllowableSpread();
}
