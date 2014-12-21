package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

public interface RemoteCASBookDepthUpdateConsumerHome
{
    public final static String HOME_NAME = "RemoteCASBookDepthUpdateConsumerHome";
    public final static String PUBLISHER_HOME_NAME = "RemoteCASBookDepthUpdatePublisherHome";

    public RemoteCASBookDepthUpdateConsumer find();
    public RemoteCASBookDepthUpdateConsumer create();

}

