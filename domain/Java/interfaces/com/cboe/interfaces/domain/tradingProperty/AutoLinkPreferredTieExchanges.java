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

public interface AutoLinkPreferredTieExchanges extends TradingProperty
{
    void setAutoLinkTieExchangePreferredSeq(int exchangeId, int exchangePreferredSeq);
    void setAutoLinkTieExchangePreferredSeq(String exchange, int exchangePreferredSeq);
    int getAutoLinkTieExchangeId();
    String getAutoLinkTieExchange();
    int getAutoLinkTieExchangePreferredSeq();
}