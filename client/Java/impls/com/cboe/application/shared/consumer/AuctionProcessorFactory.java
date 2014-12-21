package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;

/** Create AuctionProcessor objects. */
public class AuctionProcessorFactory
{
    // Nobody creates objects of this type.
    private AuctionProcessorFactory()
    { }

    /** Create a new AuctionProcessor object.
     * @param parent Object to receive outputs from the new AuctionProcessor.
     */
    public static AuctionProcessor create(AuctionCollector parent)
    {
        AuctionProcessor processor = new AuctionProcessor(parent);
        processor.setParent(parent);
        return processor;
    }

}
