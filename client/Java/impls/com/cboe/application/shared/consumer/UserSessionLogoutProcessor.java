package com.cboe.application.shared.consumer;

/**
 * @author Jeff Illian
 */
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.util.channel.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.*;

public class UserSessionLogoutProcessor implements EventChannelListener {
    private UserSessionLogoutCollector parent = null;
    private EventChannelAdapter internalEventChannel = null;

    /**
    * @author Jeff Illian
    */
    public UserSessionLogoutProcessor() {
        super();
        internalEventChannel = EventChannelAdapterFactory.find();
    }

    /**
    * @author Jeff Illian
    */
    public void setParent(UserSessionLogoutCollector parent) {
        this.parent = parent;
    }

    /**
    * @author Jeff Illian
    */
    public UserSessionLogoutCollector getParent() {
        return parent;
    }

    /**
    * @author Jeff Illian
    */
    public void channelUpdate(ChannelEvent event) {
        parent.acceptUserSessionLogout();
    }
}
