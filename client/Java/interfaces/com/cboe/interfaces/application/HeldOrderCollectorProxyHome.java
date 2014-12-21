/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 4, 2002
 * Time: 5:06:12 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.interfaces.application;

import com.cboe.util.channel.ChannelListener;
import com.cboe.interfaces.domain.session.BaseSessionManager;

public interface HeldOrderCollectorProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "HeldOrderCollectorProxyHome";

    public ChannelListener create(HeldOrderCollector consumer, BaseSessionManager sessionManager);
}
