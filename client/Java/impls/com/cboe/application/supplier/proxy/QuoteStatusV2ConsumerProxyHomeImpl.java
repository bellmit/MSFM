package com.cboe.application.supplier.proxy;

import com.cboe.interfaces.application.*;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;
import com.cboe.application.supplier.proxy.GMDConsumerProxyHomeImpl;
import com.cboe.exceptions.DataValidationException;

/**
 * QuoteStatusConsumerProxyHomeImpl.
 * @author Jimmy Wang
 */
public class QuoteStatusV2ConsumerProxyHomeImpl
    extends GMDConsumerProxyHomeImpl
    implements QuoteStatusV2ConsumerProxyHome
{
    /**
      * Follows the proscribed method for creating and generating a impl class.
      * Sets the Session Manager parent class and initializes the Order Query.
      */
    public ChannelListener create(
            com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer consumer,
            BaseSessionManager sessionManager,
            boolean gmdProxy)
        throws DataValidationException
    {

        return null;
    }
}
