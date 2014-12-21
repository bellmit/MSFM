package com.cboe.consumers.internalPresentation;

import com.cboe.application.shared.*;
import com.cboe.util.event.*;
import com.cboe.idl.internalConsumers.AlertConsumerHelper;
import com.cboe.idl.internalConsumers.POA_AlertConsumer_tie;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.delegates.internalCallback.AlertConsumerDelegate;

public class AlertConsumerFactory
{
    public AlertConsumerFactory()
    {
        super();
    }

    public static com.cboe.idl.internalConsumers.AlertConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            com.cboe.interfaces.events.AlertConsumer eventStateConsumer = new AlertConsumerImpl(eventProcessor);
            POA_AlertConsumer_tie evenStatePOAObj = new AlertConsumerDelegate(eventStateConsumer);
            com.cboe.idl.internalConsumers.AlertConsumer callbackConsumer =
                 AlertConsumerHelper.narrow(ServicesHelper.connectToOrb(evenStatePOAObj));
            return callbackConsumer;
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "AlertConsumerFactory.create");
            return null;
        }
    }
}
