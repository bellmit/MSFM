package com.cboe.interfaces.application;

/**
 * This is the common interface for the TradingSessionHome
 * @author Thomas Lynch
 */
public interface TradingSessionHome {
    /** Name that will be used for this home.    */
    public final static String HOME_NAME = "TradingSessionHome";

  /**
   * Creates an instance of the TradingSession service.
   *
   * @return reference to TradingSession service
   * @author Thomas Lynch
   */
  public TradingSession create(SessionManager session);
}
