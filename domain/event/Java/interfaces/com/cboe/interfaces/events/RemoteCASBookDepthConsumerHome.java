package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

public interface RemoteCASBookDepthConsumerHome
{
    public final static String HOME_NAME = "RemoteCASBookDepthConsumerHome";
    public final static String PUBLISHER_HOME_NAME = "RemoteCASBookDepthPublisherHome";

    public RemoteCASBookDepthConsumer find();
    public RemoteCASBookDepthConsumer create();

}

