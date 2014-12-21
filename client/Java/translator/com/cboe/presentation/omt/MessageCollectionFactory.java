//
// -----------------------------------------------------------------------------------
// Source file: MessageCollectionFactory.java
//
// PACKAGE: com.cboe.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import com.cboe.interfaces.presentation.omt.MessageCollection;

public class MessageCollectionFactory
{
    private static MessageCollection orderMessagesCollection;
    private static MessageCollection infoMessagesCollection;
    private static MessageCollection dropCopyMessagesCollection;

    private static final Object commonEventProcessingLock = new Object();
    private static final Object dropCopyEventProcessingLock = new Object();
  
    private static boolean initialized = false;

    private MessageCollectionFactory()
    {
    }

    public static synchronized void initialize()
    {
        if(!initialized)
        {
            orderMessagesCollection = new OrderMessageCollection(commonEventProcessingLock);
            //TODO: any reason why InfoMessageCollection uses the same lock as OrderMessageCollection?
            infoMessagesCollection = new InfoMessageCollection(commonEventProcessingLock);
            dropCopyMessagesCollection = new DropCopyMessageCollection(dropCopyEventProcessingLock);
            OMTMarketabilityWorker.getInstance().addListener(new OrderMessageCollectionAdapter(orderMessagesCollection));
            //TODO: purpose of this line below?
            MessageCollection[] subCollections = {orderMessagesCollection, infoMessagesCollection, dropCopyMessagesCollection};
            OMTMarketabilityWorker.getInstance().startMarketabilityCheck();
            initialized = true;
        }
    }

    public static synchronized MessageCollection getOrderMessagesCollection()
    {
        if(orderMessagesCollection == null)
        {
            throw new IllegalStateException("orderMessagesCollection has not been created yet.");
        }
        else
        {
            return orderMessagesCollection;
        }
    }

    public static synchronized MessageCollection getDropCopyMessagesCollection()
    {
        if(dropCopyMessagesCollection == null)
        {
            throw new IllegalStateException("dropCopyMessagesCollection has not been created yet.");
        }
        else
        {
            return dropCopyMessagesCollection;
        }
    }

    public static synchronized MessageCollection getInfoMessagesCollection()
    {
        if(infoMessagesCollection == null)
        {
            throw new IllegalStateException("infoMessagesCollection has not been created yet.");
        }
        else
        {
            return infoMessagesCollection;
        }
    }
}
