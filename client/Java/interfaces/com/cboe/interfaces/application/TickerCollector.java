package com.cboe.interfaces.application;

import com.cboe.interfaces.domain.session.SessionBasedCollector;

/**
 *
 * @author Jeff Illian
 *
 */
public interface TickerCollector extends SessionBasedCollector
{
  public void acceptTickersForClass(com.cboe.idl.cmiMarketData.TickerStruct[] tickers) ;
  public void acceptLargeTradeLastSaleForClass(com.cboe.idl.marketData.InternalTickerDetailStruct[] lastSales);
}
