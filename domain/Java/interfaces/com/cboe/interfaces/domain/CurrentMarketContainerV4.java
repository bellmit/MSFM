package com.cboe.interfaces.domain;

import com.cboe.idl.cmiMarketData.CurrentMarketStructV4;

/**
 * User: Beniwalv
 * Date: Jul 30, 2010
 * This container is used by MDX enabled CFIX to handle current market in a fashion consistent with the earlier version of CFIX which was IEC enabled.
 */
public interface CurrentMarketContainerV4 {
    /**
     * @return
     */
    public CurrentMarketStructV4[] getBestMarkets();

    /**
     * @return
     */
    public CurrentMarketStructV4[] getBestPublicMarkets();

    public int getMessageSequence();

    public int getQueueDepth();

    public short getQueueAction();

}
