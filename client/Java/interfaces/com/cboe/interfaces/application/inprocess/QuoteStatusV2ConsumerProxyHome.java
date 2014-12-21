package com.cboe.interfaces.application.inprocess;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.application.BaseProxyHome;
import com.cboe.interfaces.application.inprocess.QuoteStatusV2Consumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * Created by IntelliJ IDEA.
 * User: mageem
 * Date: Jun 19, 2009
 * Time: 9:36:54 AM
 * To change this template use File | Settings | File Templates.
 */
public interface QuoteStatusV2ConsumerProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "QuoteStatusV2ConsumerProxyHome";

    public ChannelListener create(QuoteStatusV2Consumer consumer, BaseSessionManager sessionManager)
        throws  DataValidationException, SystemException, CommunicationException, AuthorizationException;
}
