/**
 * Created by IntelliJ IDEA.
 * User: chenj
 * Date: Feb 24, 2003
 * Time: 8:53:04 AM
 * To change this template use Options | File Templates.
 */
package com.cboe.cfix.cas.supplier;

import com.cboe.application.supplier.*;
import com.cboe.cfix.cas.consumer.proxy.*;
import com.cboe.interfaces.domain.session.*;

public class CfixUserSessionAdminSupplier extends UserSessionBaseSupplier
{
    public CfixUserSessionAdminSupplier(BaseSessionManager session)
    {
        super(session);
    }

    public String getListenerClassName()
    {
        return CfixSessionAdminConsumerProxy.class.getName();
    }
}
