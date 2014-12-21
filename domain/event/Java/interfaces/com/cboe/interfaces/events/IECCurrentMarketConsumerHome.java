package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.util.event.*;

/**
 * This is the common interface for the Current Market Home
 * @author Jeff Illian
 */
public interface IECCurrentMarketConsumerHome extends CurrentMarketConsumerHome, EventChannelConsumerManager
{
    /**
     * Indicate whether to accept messages on CurrentMarket channel.
     * @param on true to accept messages, false to disable messages.
     */
    void activateSubscription(boolean on);
}

