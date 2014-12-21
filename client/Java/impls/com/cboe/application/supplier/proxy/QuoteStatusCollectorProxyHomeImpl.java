package com.cboe.application.supplier.proxy;

import com.cboe.idl.cmiCallback.*;
import com.cboe.interfaces.application.*;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.util.ThreadPool;
import com.cboe.util.channel.ChannelListenerProxy;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.channel.ChannelAdapter;
import com.cboe.domain.supplier.proxy.BaseConsumerProxyHomeImpl;
/**
 * QuoteStatusCollectorProxyHomeImpl.
 * @author Keith A. Korecky
 */
public class QuoteStatusCollectorProxyHomeImpl extends BaseConsumerProxyHomeImpl implements QuoteStatusCollectorProxyHome
{
    /** constructor. **/
    public QuoteStatusCollectorProxyHomeImpl()
    {
        super();
    }

    /**
      * Follows the proscribed method for creating and generating a impl class.
      * Sets the Session Manager parent class and initializes the Order Query.
      * @param theSession com.cboe.application.session.SessionManager
      * @returns ChannelListener
      */
    public ChannelListener create(QuoteStatusCollector consumer, BaseSessionManager sessionManager)
    {
        QuoteStatusCollectorProxy bo = new QuoteStatusCollectorProxy(consumer, sessionManager, consumer);

        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));
        bo.initConnectionProperty(getConnectionProperty(sessionManager));
        return bo;
    }
}
