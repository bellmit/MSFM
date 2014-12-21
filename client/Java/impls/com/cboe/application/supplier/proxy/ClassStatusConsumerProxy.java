package com.cboe.application.supplier.proxy;

import com.cboe.application.supplier.ClassStatusSupplierFactory;
import com.cboe.idl.cmiCallback.CMIClassStatusConsumer;
import com.cboe.idl.cmiSession.ClassStateStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;

/**
 * ClassStatusConsumerProxy serves as a SessionManager managed proxy to
 * the LastSaleSummaryConsumer object on the presentation side in
 * com.cboe.presentation.consumer.  The ClassStatusSupplier on the CAS uses
 * this proxy object to communicate to the GUI callback object.  If a connection
 * to the presentation side consumer fails the <CODE>lostConnection</CODE> method
 * will be called letting the SessionManager this consumer reference is no longer
 * valid.
 *
 * @see com.cboe.consumers.internalPresentation.ClassStatusConsumerImpl
 * @see com.cboe.idl.cmiCallback.CMIClassStatusConsumer
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/25/1999
 */

public class ClassStatusConsumerProxy extends InstrumentedConsumerProxy
{

    /**
     * ClassStatusConsumerProxy constructor.
     *
     * @param classStatusConsumer a reference to the proxied implementation object.
     * @param sessionManager a reference to the SessionManager managing subscriptions for this proxy.
     */
    public ClassStatusConsumerProxy(CMIClassStatusConsumer classStatusConsumer, BaseSessionManager sessionManager)
    {
        super(sessionManager, ClassStatusSupplierFactory.find(), classStatusConsumer);
        interceptor = new ClassStatusConsumerInterceptor(classStatusConsumer);
    }

    /**
     * This method is called by ChannelThreadCommand.  It takes the passed
     * EventChannelEvent, parses out the relevant data for the proxied object,
     * and calls the proxied objects callback method passing in the appropriate
     * data.
     *
     * @param event the ChannelEvent containing the data to send the listener.
     */
    public final void channelUpdate(ChannelEvent event)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this,"calling channelUpdate for " + getSessionManager());
        }
        try
        {
            if (event != null)
            {
                ChannelKey key = (ChannelKey) event.getChannel();

                switch (key.channelType)
                {
                    case ChannelType.CB_CLASS_STATE:
                        // Call the proxied method passing the extracted ClassStateStruct[] from the EventChannelEvent.
                        ((ClassStatusConsumerInterceptor)interceptor).acceptClassState((ClassStateStruct[])event.getEventData());
                        break;
                    case ChannelType.CB_PRODUCT_CLASS_UPDATE:
                        // Call the proxied method passing the extracted ClassStruct from the EventChannelEvent.
                        ((ClassStatusConsumerInterceptor)interceptor).updateProductClass((SessionClassStruct)event.getEventData());
                        break;
                    case ChannelType.CB_CLASS_STATE_BY_TYPE:
                        // Call the proxied method passing the extracted ClassStateStruct[] from the EventChannelEvent.
                        ((ClassStatusConsumerInterceptor)interceptor).acceptClassState((ClassStateStruct[])event.getEventData());
                        break;
                    case ChannelType.CB_CLASS_UPDATE_BY_TYPE:
                        // Call the proxied method passing the extracted ClassStruct from the EventChannelEvent.
                        ((ClassStatusConsumerInterceptor)interceptor).updateProductClass((SessionClassStruct)event.getEventData());
                        break;

                    default:
                        break;
                }
            }
            else
            {
                Log.information(this, "Null event");
            }
        }
        catch(Exception e)
        {
            Log.exception(this, "session:" + getSessionManager(), e);
            lostConnection(event);
        }
    }

    public String getMethodName(ChannelEvent event)
    {
        String method = "";
        ChannelKey key = (ChannelKey) event.getChannel();

        switch (key.channelType)
        {
            case ChannelType.CB_CLASS_STATE:
                method = "acceptClassState";
                break;
            case ChannelType.CB_PRODUCT_CLASS_UPDATE:
                method = "updateProductClass";
                break;
            case ChannelType.CB_CLASS_STATE_BY_TYPE:
                method = "acceptClassState";
                break;
            case ChannelType.CB_CLASS_UPDATE_BY_TYPE:
                method = "updateProductClass";
                break;
        }
        return method;
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.CLASS_STATUS;
    }
}
