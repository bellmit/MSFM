//
// -----------------------------------------------------------------------------------
// Source file: NBBOV5ConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import org.omg.CORBA.Object;

import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.delegates.callback.NBBOV4ConsumerDelegate;
import com.cboe.idl.cmiCallbackV4.CMINBBOConsumer;
import com.cboe.idl.cmiCallbackV4.CMINBBOConsumerHelper;
import com.cboe.interfaces.callback.NBBOV4Consumer;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.event.EventChannelAdapter;

/**
 * 
 * @author Eric Maheo
 */
public class NBBOV5ConsumerFactory extends AbstractV4ConsumerFactory
{
    public NBBOV5ConsumerFactory()
    {
        super();
    }

    public static CMINBBOConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            NBBOV4Consumer consumer = new NBBOV5ConsumerImpl(eventProcessor);
            NBBOV4ConsumerDelegate delegate = new NBBOV4ConsumerDelegate(consumer);
            Object corbaObject = (Object) RemoteConnectionFactory.find().register_object(delegate, getPOAName());
            return CMINBBOConsumerHelper.narrow(corbaObject);
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(e, "NBBOV5ConsumerFactory.create");
            return null;
        }
    }
}
