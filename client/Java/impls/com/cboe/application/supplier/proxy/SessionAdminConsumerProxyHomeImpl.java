package com.cboe.application.supplier.proxy;

import com.cboe.idl.cmiCallback.*;
import com.cboe.interfaces.application.*;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;
import com.cboe.application.supplier.proxy.GMDConsumerProxyHomeImpl;
import com.cboe.exceptions.*;
import com.cboe.application.shared.ServicesHelper;

/**
 * SessionAdminConsumerProxyHomeImpl.
 * @author Jimmy Wang
 */
public class SessionAdminConsumerProxyHomeImpl
    extends GMDConsumerProxyHomeImpl
    implements SessionAdminConsumerProxyHome
{
    /**
      * Follows the proscribed method for creating and generating a impl class.
      * Sets the Session Manager parent class and initializes the Order Query.
      */
    public synchronized ChannelListener create(CMIUserSessionAdmin consumer,
                                               BaseSessionManager sessionManager,
                                               boolean gmdTextMessaging)
        throws  DataValidationException,
                SystemException,
                CommunicationException,
                AuthorizationException
    {
        SessionAdminConsumerProxy proxy =
            new SessionAdminConsumerProxy(consumer,
                                          sessionManager,
                                          gmdTextMessaging,
                                          this);
        if(getInstrumentationEnablementProperty())
        {
            proxy.startMethodInstrumentation(getInstrumentationProperty());
        }
        // Every proxy object must be added to the container BEFORE anything else.
        addToContainer(proxy);

        // Every BObject create MUST have a name...if the object is to be a
        // managed object.
        proxy.create(String.valueOf(proxy.hashCode()));
        proxy.initConnectionProperty(getConnectionProperty(sessionManager));


        if (gmdTextMessaging)
        {
            addGMDProxy(true,       // true == user-level (not firm-level)
                        proxy);     // proxy == the proxy (duh!)
        }

        return proxy;
    }
}
