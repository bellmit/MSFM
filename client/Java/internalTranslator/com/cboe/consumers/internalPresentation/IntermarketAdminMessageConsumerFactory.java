package com.cboe.consumers.internalPresentation;

import com.cboe.idl.consumers.IntermarketAdminMessageConsumer;
import com.cboe.idl.consumers.IntermarketAdminMessageConsumerHelper;
import com.cboe.idl.events.POA_IntermarketAdminMessageEventConsumer;

import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.consumers.eventChannel.IntermarketAdminMessageEventConsumerImpl;
import com.cboe.util.event.EventChannelAdapter;

public class IntermarketAdminMessageConsumerFactory {

    public IntermarketAdminMessageConsumerFactory() {
        super();
    }

    public static IntermarketAdminMessageConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            com.cboe.interfaces.events.IntermarketAdminMessageConsumer adminConsumer = new IntermarketAdminMessageConsumerImpl(eventProcessor);
            POA_IntermarketAdminMessageEventConsumer adminConsumerPOAObj = new IntermarketAdminMessageEventConsumerImpl(adminConsumer);
            IntermarketAdminMessageConsumer callbackConsumer =
            IntermarketAdminMessageConsumerHelper.narrow(ServicesHelper.connectToOrb(adminConsumerPOAObj));
            return callbackConsumer;
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "AdminProductStatusConsumerFactory.create");
            return null;
        }
    }
}
