package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

public interface RemoteCASExpectedOpeningPriceConsumerHome
{
    public final static String HOME_NAME = "RemoteCASExpectedOpeningPriceConsumerHome";
    public final static String PUBLISHER_HOME_NAME = "RemoteCASExpectedOpeningPricePublisherHome";

    public RemoteCASExpectedOpeningPriceConsumer find();
    public RemoteCASExpectedOpeningPriceConsumer create();

}

