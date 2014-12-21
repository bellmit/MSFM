package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

public interface RemoteCASNBBOConsumerHome
{
    public final static String HOME_NAME = "RemoteCASNBBOConsumerHome";
    public final static String PUBLISHER_HOME_NAME = "RemoteCASNBBOPublisherHome";

    public RemoteCASNBBOConsumer find();
    public RemoteCASNBBOConsumer create();

}

