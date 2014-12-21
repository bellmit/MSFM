package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

public interface RemoteCASCallbackRemovalConsumerHome
{
    public final static String HOME_NAME = "RemoteCASCallbackRemovalConsumerHome";
    public final static String PUBLISHER_HOME_NAME = "RemoteCASCallbackRemovalPublisherHome";

    public RemoteCASCallbackRemovalConsumer find();
    public RemoteCASCallbackRemovalConsumer create();

}

