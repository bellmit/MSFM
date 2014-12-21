package com.cboe.interfaces.events;

public interface RemoteCASRecoveryConsumerHome
{
    public final static String HOME_NAME = "RemoteCASRecoveryConsumerHome";
    public final static String PUBLISHER_HOME_NAME = "RemoteCASRecoveryPublisherHome";

    public RemoteCASRecoveryConsumer find();
    public RemoteCASRecoveryConsumer create();

}

