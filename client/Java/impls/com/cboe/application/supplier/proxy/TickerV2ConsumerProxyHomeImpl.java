package com.cboe.application.supplier.proxy;

import com.cboe.application.supplier.proxy.flushProxy.TickerV2ConsumerFlushProxy;
import com.cboe.application.supplier.proxy.overlayProxy.TickerV2ConsumerOverlayProxy;
import com.cboe.domain.supplier.proxy.BaseConsumerProxyHomeImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiCallbackV2.CMITickerConsumer;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.interfaces.application.TickerV2ConsumerProxyHome;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelListener;

/**
 * TickerConsumerProxyHomeImpl.
 * @author Jimmy Wang
 */
public class TickerV2ConsumerProxyHomeImpl extends BaseConsumerProxyHomeImpl implements TickerV2ConsumerProxyHome
{
    /** constructor. **/
    public TickerV2ConsumerProxyHomeImpl()
    {
        super();
    }

    /**
     * Follows the prescribed method for creating and generating a impl class.
     * Sets the Session Manager parent class and initializes the Order Query.
     * @param consumer Object to send events to client.
     * @param sessionManager Object that manages subscriptions for this proxy.
     * @param queuePolicy QueueActions value indicating how to handle high volume.
     * @return Object to send messages to client callback.
      */
    public ChannelListener create(CMITickerConsumer consumer, BaseSessionManager sessionManager, short queuePolicy)
        throws DataValidationException
    {
        TickerV2ConsumerProxy bo = null;

        switch(queuePolicy)
        {
            case QueueActions.OVERLAY_LAST:
                TickerV2ConsumerOverlayProxy overlaybo = new TickerV2ConsumerOverlayProxy(consumer, sessionManager);
                if (!overlaybo.getChannelAdapter().isListener(overlaybo))
                {
                    overlaybo.initialize();
                }
                bo = overlaybo;
                break;
            case QueueActions.FLUSH_QUEUE:
                bo = new TickerV2ConsumerFlushProxy(consumer, sessionManager);
                break;
            case QueueActions.NO_ACTION:
            case QueueActions.DISCONNECT_CONSUMER:
                bo = new TickerV2ConsumerProxy(consumer, sessionManager, queuePolicy);
                break;
            default:
                throw ExceptionBuilder.dataValidationException("Queue Action not supported by this consumer",
                        DataValidationCodes.MISSING_LISTENER );
        }

        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));
        if(getInstrumentationEnablementProperty())
        {
            bo.startMethodInstrumentation(getInstrumentationProperty());
        }
        bo.initConnectionProperty(getConnectionProperty(sessionManager));
        bo.initFlushProxyQueueDepthProperty(getFlushQueueDepth(sessionManager));
        bo.initNoActionProxyQueueDepthProperty(getNoActionQueueDepth(sessionManager));
        return bo;
    }
}
