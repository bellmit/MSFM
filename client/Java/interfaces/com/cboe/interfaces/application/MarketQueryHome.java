package com.cboe.interfaces.application;

import com.cboe.interfaces.floorApplication.LastSaleService;

/**
 * This is the common interface for the Market Query Home
 * @author Jeff Illian
 */
public interface MarketQueryHome {
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "SystemMarketQueryHome";
  /**
   * Creates an instance of the market query service.
   *
   * @return reference to market que
   * ry service
   *
   * @author Jeff Illian
   */
  public MarketQueryV3 createMarketQuery(SessionManager sessionManager);
  
  /**
   * Creates an instance of  last sale service.
   */
  public LastSaleService createLargeTradeLastSale(SessionManager sessionManager);
  
  /**
   * Remove the session from cache
   */
  public void removeSession(SessionManager sessionManager);
}

