package com.cboe.consumers.eventChannel;

/**
 * @author Mike Pyatetsky
 */
import com.cboe.interfaces.events.*;
import com.cboe.util.event.*;
import com.cboe.idl.infrastructureServices.sessionManagementService.UserLoginStruct;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.domain.util.LogoutMessage;

public class ForcedLogoutConsumerIECImpl extends BObject implements UserSessionConsumer
{
    private EventChannelAdapter internalEventChannel = null;
    /**
     * constructor comment.
     */
    public ForcedLogoutConsumerIECImpl() {
        super();
        internalEventChannel = EventChannelAdapterFactory.find();
    }

    public void acceptLogout(String userId, String sourceComponent, boolean forced, int sessionKey, String message )
    {}

    public void acceptLogin(String p0, String p1, String p2, int p3)
    {}

    public void acceptSessionClosed(int p0, String userId, boolean p2, String message)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> ForcedLogout for " + userId + " :: " + message+" flag:"+p2);
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        LogoutMessage logoutMessage = new LogoutMessage(p0, message);

        channelKey = new ChannelKey(ChannelKey.CB_LOGOUT, userId);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, logoutMessage);
        internalEventChannel.dispatch(event);
    }

    public void acceptSessionOpened(int p0, String userId)
    {}

    public void acceptOpenSessions(UserLoginStruct[] userSession)
    {}
}
