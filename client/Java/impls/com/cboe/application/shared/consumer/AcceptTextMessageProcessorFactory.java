package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;

/**
 * @author Keith A. Korecky
 */

public class AcceptTextMessageProcessorFactory
{

    /**
    * AcceptTextMessageProcessorFactory constructor comment.
    */
    public AcceptTextMessageProcessorFactory()
    {
        super();
    }

    /**
    * @author Keith A. Korecky
    */
    public static AcceptTextMessageProcessor create(AcceptTextMessageCollector parent)
    {
        AcceptTextMessageProcessor processor = new AcceptTextMessageProcessor();
        processor.setParent(parent);
        return processor;
    }
}
