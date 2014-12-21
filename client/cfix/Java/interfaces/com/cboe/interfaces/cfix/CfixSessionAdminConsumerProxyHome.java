/**
 * Created by IntelliJ IDEA.
 * User: chenj
 * Date: Feb 20, 2003
 * Time: 5:17:33 PM
 * To change this template use Options | File Templates.
 */
package com.cboe.interfaces.cfix;

import com.cboe.util.channel.ChannelListener;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.exceptions.DataValidationException;

public interface CfixSessionAdminConsumerProxyHome {

    public final static String HOME_NAME = "CfixSessionAdminConsumerProxyHome";

    public ChannelListener create(CfixUserSessionAdminConsumer consumer, BaseSessionManager sessionManager, boolean gmdTextMessaging)
            throws DataValidationException;
}
