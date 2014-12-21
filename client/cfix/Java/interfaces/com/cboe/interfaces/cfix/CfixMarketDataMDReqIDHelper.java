package com.cboe.interfaces.cfix;

/**
 * CfixMarketDataMDReqIDHelper.java
 *
 * @author Dmitry Volpyansky
 *
 */

public interface CfixMarketDataMDReqIDHelper
{
    public boolean isValidMDReqID(String mdReqID);
    public void incSentMDReqID(String mdReqID);
}