package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

public interface RemoteCASRecapConsumerHome
{
    public final static String HOME_NAME = "RemoteCASRecapConsumerHome";
    public final static String PUBLISHER_HOME_NAME = "RemoteCASRecapPublisherHome";

    public RemoteCASRecapConsumer find();
    public RemoteCASRecapConsumer create();

}

