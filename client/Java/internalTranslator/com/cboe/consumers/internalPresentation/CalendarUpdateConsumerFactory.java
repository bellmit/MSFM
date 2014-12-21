package com.cboe.consumers.internalPresentation;

import com.cboe.idl.internalConsumers.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CalendarServiceComsumers.
 *
 */

public class CalendarUpdateConsumerFactory
{
    /**
     * CalendarUpdateConsumerFactory constructor.
     *
     */
    public CalendarUpdateConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CalendarServiceConsumer callback Corba object.
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CalendarUpdateConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            com.cboe.interfaces.internalCallback.CalendarUpdateConsumer calendarUpdateConsumer = new CalendarUpdateConsumerImpl(eventProcessor);
            POA_CalendarUpdateConsumer_tie poaObj = new POA_CalendarUpdateConsumer_tie(calendarUpdateConsumer);
            return CalendarUpdateConsumerHelper.narrow(POAHelper.connect(poaObj, null));
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "CalendarUpdateConsumerFactory.create");
            return null;
        }
    }
}
