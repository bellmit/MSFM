package com.cboe.consumers.eventChannel;

/**
 * @author William Wei
 */

import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.domain.startup.ClientBOHome;

/**
 * Just an empty home with which to associate properties
 */
public class EventChannelHomeImpl extends ClientBOHome implements EventChannelHome
{
    public final static String SMA_TYPE_PATH = "GlobalEventChannelHome.EventChannelHomeImpl";
    public EventChannelHomeImpl() {
        super();

        setSmaType(SMA_TYPE_PATH);
    }

    public void clientStart()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
    }

}// EOF
