package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

public interface RemoteCASTickerConsumerHome
{
    public final static String HOME_NAME = "RemoteCASTickerConsumerHome";
    public final static String PUBLISHER_HOME_NAME = "RemoteCASTickerPublisherHome";

    public RemoteCASTickerConsumer find();
    public RemoteCASTickerConsumer create();

}

