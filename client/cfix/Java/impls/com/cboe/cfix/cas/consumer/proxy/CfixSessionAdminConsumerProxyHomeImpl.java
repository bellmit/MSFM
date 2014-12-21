/**
 * Created by IntelliJ IDEA.
 * User: chenj
 * Date: Feb 20, 2003
 * Time: 5:22:47 PM
 * To change this template use Options | File Templates.
 */
package com.cboe.cfix.cas.consumer.proxy;

import com.cboe.application.supplier.proxy.*;
import com.cboe.exceptions.*;
import com.cboe.interfaces.cfix.*;
import com.cboe.interfaces.domain.session.*;
import com.cboe.util.channel.*;


public class CfixSessionAdminConsumerProxyHomeImpl extends GMDConsumerProxyHomeImpl implements CfixSessionAdminConsumerProxyHome
{
    /** constructor. **/
    public CfixSessionAdminConsumerProxyHomeImpl()
    {
        super();
    }

    /**
      * Follows the proscribed method for creating and generating a impl class.
      * Sets the Session Manager parent class and initializes the Order Query.
      * @param theSession com.cboe.application.session.SessionManager
      * @returns ChannelListener
      */
    public synchronized ChannelListener create(CfixUserSessionAdminConsumer consumer, BaseSessionManager sessionManager, boolean gmdTextMessaging)
        throws DataValidationException
    {
        CfixSessionAdminConsumerProxy bo = new CfixSessionAdminConsumerProxy(consumer, sessionManager, gmdTextMessaging, this);

        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));
        bo.initConnectionProperty(getConnectionProperty(sessionManager));

        if (gmdTextMessaging)
        {
            addGMDProxy(true, bo);
        }

        return bo;
    }
}
