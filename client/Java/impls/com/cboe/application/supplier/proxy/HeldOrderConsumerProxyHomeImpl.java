/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 4, 2002
 * Time: 4:19:28 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.application.supplier.proxy;

import com.cboe.domain.supplier.proxy.BaseConsumerProxyHomeImpl;
import com.cboe.util.channel.ChannelListener;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.application.HeldOrderConsumerProxyHome;
import com.cboe.idl.cmiIntermarketCallback.CMIIntermarketOrderStatusConsumer;

public class HeldOrderConsumerProxyHomeImpl extends BaseConsumerProxyHomeImpl implements HeldOrderConsumerProxyHome
{
    /** constructor. **/
    public HeldOrderConsumerProxyHomeImpl()
    {
        super();
    }

    /**
      * Follows the proscribed method for creating and generating a impl class.
      * Sets the Session Manager parent class and initializes the Order Query.
      * @param theSession com.cboe.application.session.SessionManager
      * @returns ChannelListener
      */
    public ChannelListener create(CMIIntermarketOrderStatusConsumer consumer, BaseSessionManager sessionManager)
    {
        HeldOrderConsumerProxy bo = new HeldOrderConsumerProxy(consumer, sessionManager);

        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));
        if(getInstrumentationEnablementProperty())
        {
            bo.startMethodInstrumentation(getInstrumentationProperty());
        }
        bo.initConnectionProperty(getConnectionProperty(sessionManager));
        return bo;
    }
}
