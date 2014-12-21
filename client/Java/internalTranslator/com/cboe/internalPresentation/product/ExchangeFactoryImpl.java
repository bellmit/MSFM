package com.cboe.internalPresentation.product;

import com.cboe.idl.exchange.ExchangeStruct;
import com.cboe.interfaces.internalPresentation.product.Exchange;
import com.cboe.presentation.common.exchange.AbstractExchangeFactory;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.internalPresentation.api.SystemAdminAPIFactory;

public class ExchangeFactoryImpl extends AbstractExchangeFactory
{
    com.cboe.interfaces.presentation.common.exchange.Exchange[] exchanges = null;

    public static final com.cboe.interfaces.presentation.common.exchange.Exchange UNSPECIFIED_EXCHANGE = new ExchangeImpl(new ExchangeStruct(0, 0, "", ""));

    /**
     *  Public constructor used by HomeBuilder to create the Exchange Home for SAGUI
     */
    public ExchangeFactoryImpl(){}

    /**
     * Gets the unspecified Exchange for specifying non exchange
     */
    public com.cboe.interfaces.presentation.common.exchange.Exchange getUnspecifiedExchange()
    {
        return UNSPECIFIED_EXCHANGE;
    }

    /**
     *  Gets the ExchangeList attribute of the ExchangeFactory class
     *
     *@return    The ExchangeList value
     */
    public com.cboe.interfaces.presentation.common.exchange.Exchange[] getExchangeList()
    {
        if (exchanges == null || exchanges.length == 0)
        {
            try
            {
                exchanges = SystemAdminAPIFactory.find().getAllExchanges();
            }
            catch(Exception e)
            {
                DefaultExceptionHandlerHome.find().process(e);
                exchanges = new com.cboe.interfaces.presentation.common.exchange.Exchange[0];
            }
        }
        return exchanges;
    }

    /**
     * Our GUI's do not support adding a new exchange from the GUI, so create the object,
     * but don't add it to the Exchange list obtained from the server.
     */
    public com.cboe.interfaces.presentation.common.exchange.Exchange createExchange(String anExchange, String fullName)
    {
        return new ExchangeImpl(new ExchangeStruct(0, 0, anExchange, fullName));
    }

    public com.cboe.interfaces.presentation.common.exchange.Exchange findExchange(String exchange)
    {
        com.cboe.interfaces.presentation.common.exchange.Exchange returnExchange = super.findExchange(exchange);
        if (returnExchange != null && !returnExchange.equals(getUnspecifiedExchange()))
        {
            try
            {
                returnExchange = (com.cboe.interfaces.presentation.common.exchange.Exchange) returnExchange.clone();
            }
            catch (CloneNotSupportedException e)
            {
                e.printStackTrace();
            }
        }
        return returnExchange;
    }

    /**
     * Our GUI's do not support adding a new exchange from the GUI, so create the object,
     * but don't add it to the Exchange list obtained from the server.
     */
    public com.cboe.interfaces.presentation.common.exchange.Exchange findOrCreateExchange(String anExchange)
    {
        com.cboe.interfaces.presentation.common.exchange.Exchange exchange = findExchange(anExchange);
        if(exchange == null)
        {
            exchange = createExchange(anExchange, "Not Found - " + anExchange);
        }
        return exchange;
    }

    /**
     * Creates an instance of an Exchange from a ExchangeStruct.
     * @param ExchangeStruct to wrap in instance of Exchange
     * @return Exchange to represent the ExchangeStruct
     */
    public static Exchange create(ExchangeStruct exchangeStruct)
    {
        if (exchangeStruct == null)
        {
            throw new IllegalArgumentException();
        }
        Exchange exchange;
        exchange = new ExchangeImpl(exchangeStruct);

        return exchange;
    }
}
