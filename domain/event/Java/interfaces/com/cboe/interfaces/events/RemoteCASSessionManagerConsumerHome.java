package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

public interface RemoteCASSessionManagerConsumerHome
{
    public final static String HOME_NAME = "RemoteCASSessionManagerConsumerHome";
    public final static String PUBLISHER_HOME_NAME = "RemoteCASSessionManagerPublisherHome";

    public RemoteCASSessionManagerConsumer find();
    public RemoteCASSessionManagerConsumer create();

}

