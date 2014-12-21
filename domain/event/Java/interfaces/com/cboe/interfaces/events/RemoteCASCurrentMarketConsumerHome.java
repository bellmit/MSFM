package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

public interface RemoteCASCurrentMarketConsumerHome
{
    public final static String HOME_NAME = "RemoteCASCurrentMarketConsumerHome";
    public final static String PUBLISHER_HOME_NAME = "RemoteCASCurrentMarketPublisherHome";

    public RemoteCASCurrentMarketConsumer find();
    public RemoteCASCurrentMarketConsumer create();

}

