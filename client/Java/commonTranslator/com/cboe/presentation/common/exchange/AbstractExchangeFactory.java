//
// -----------------------------------------------------------------------------------
// Source file: AbstractExchangeFactory.java
//
// PACKAGE: com.cboe.presentation.common.exchange
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
//
// @author: Joy Kyriakopulos
// @date: Jun 16, 2005
//

package com.cboe.presentation.common.exchange;

import com.cboe.interfaces.presentation.common.exchange.Exchange;
import com.cboe.interfaces.presentation.common.exchange.ExchangeFactory;

/**
 * Implements ExchangeFactory interface, providing a little behaviour
 */
public abstract class AbstractExchangeFactory implements ExchangeFactory
{
    /**
     *  Gets the ExchangeList attribute of the ExchangeFactory class
     *
     *@return    The ExchangeList value
     */
    public abstract Exchange[] getExchangeList();

    public Exchange findExchange(String exchange)
    {
        Exchange found = null;
        Exchange[] exchangeList = getExchangeList();
        for(int x = 0; x < exchangeList.length; x++)
        {
            if(exchangeList[x].getExchange().equals(exchange))
            {
                found = exchangeList[x];
                break;
            }
        }

        return found;
    }

    public abstract Exchange createExchange(String anExchange, String fullName);

    public Exchange findOrCreateExchange(String anExchange)
    {
        Exchange exchange = findExchange(anExchange);
        if(exchange == null)
        {
            exchange = createExchange(anExchange, "Not Found - " + anExchange);
        }
        return exchange;
    }
}
