package com.cboe.application.supplier.proxy;

import com.cboe.domain.supplier.proxy.BaseConsumerProxyHomeImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.consumers.TickerConsumer;
import com.cboe.interfaces.application.LargeTradeLastSaleConsumerProxyHome;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelListener;

public class LargeTradeLastSaleConsumerProxyHomeImpl 
			extends BaseConsumerProxyHomeImpl
			implements LargeTradeLastSaleConsumerProxyHome {

	public ChannelListener create(TickerConsumer consumer,
			BaseSessionManager sessionManager, short queuePolicy)
			throws DataValidationException {
		LargeTradeLastSaleConsumerProxy bo = null;

        switch(queuePolicy)
        {
        	case QueueActions.NO_ACTION:
        	case QueueActions.DISCONNECT_CONSUMER:
        		bo = new LargeTradeLastSaleConsumerProxy(consumer, sessionManager);
                break;
        	case QueueActions.OVERLAY_LAST:
            case QueueActions.FLUSH_QUEUE:
            
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
