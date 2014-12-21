/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 11, 2002
 * Time: 2:20:13 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.application.supplier.proxy;

import com.cboe.interfaces.application.NBBOAgentSessionAdminProxyHome;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.domain.supplier.proxy.BaseConsumerProxyHomeImpl;
import com.cboe.util.channel.ChannelListener;
import com.cboe.idl.cmiIntermarketCallback.CMINBBOAgentSessionAdmin;

public class NBBOAgentSessionAdminProxyHomeImpl extends BaseConsumerProxyHomeImpl
        implements NBBOAgentSessionAdminProxyHome
{
    /** constructor. **/
    public NBBOAgentSessionAdminProxyHomeImpl()
    {
        super();
    }

    /**
      * Follows the proscribed method for creating and generating a impl class.
      * Sets the Session Manager parent class and initializes the Order Query.
      * @param theSession com.cboe.application.session.SessionManager
      * @returns ChannelListener
      */
    public ChannelListener create(CMINBBOAgentSessionAdmin consumer, BaseSessionManager sessionManager)
    {
        NBBOAgentSessionAdminProxy bo = new NBBOAgentSessionAdminProxy(consumer, sessionManager);

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
