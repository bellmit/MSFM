package com.cboe.interfaces.presentation.common.exchange;

//
// -----------------------------------------------------------------------------------
// Source file: ExchangeFactory.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.exchange
//
// This ExchangeFactory interface is an extraction and consolidation of two, originally
// mutually exchusive, ExchangeFactory classes:
//
//    com.cboe.interfaces.presentation.user.ExchangeFactory, now ExchangeFactoryImpl
//    com.cboe.interfaces.internalPresentation.product.ExchangeFactory, now ExchangeFactoryImpl
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
//
// @author: Joy Kyriakopulos
// @date: Jun 16, 2005
//
public interface ExchangeFactory
{
    /**
     *  Gets the ExchangeList attribute of the ExchangeFactory class
     *
     *@return    The ExchangeList value
     */
    public Exchange[] getExchangeList();

    /**
     *  Finds and returns the exchange with the passed as string name
     *
     *@param  exchange  Name of the exchange to return
     *@return Exchange  The exchange object with the specified name
     */
    public Exchange findExchange(String exchange);

    public Exchange createExchange(String anExchange, String fullName);

    public Exchange findOrCreateExchange(String anExchange);

    public Exchange getUnspecifiedExchange();
}
