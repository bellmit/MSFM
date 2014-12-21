package com.cboe.presentation.api;

import com.cboe.interfaces.presentation.rfq.RFQCache;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.ChannelType;
import com.cboe.util.ChannelKey;
import java.util.Map;
import java.util.HashMap;

public class RFQCacheFactory {
    public static Map sessions;

    public RFQCacheFactory()
    {
    }

    private static void registerRFQCache(String sessionName, RFQCache rfqCache)
    {
        ChannelKey key = new ChannelKey(ChannelType.RFQ, sessionName);
        EventChannelAdapterFactory.find().setDynamicChannels(true);
        EventChannelAdapterFactory.find().addChannelListener(rfqCache, rfqCache, key);

        key = new ChannelKey(ChannelType.CB_ALL_QUOTES, new Integer(0));
        EventChannelAdapterFactory.find().addChannelListener(rfqCache, rfqCache, key);
    }

    private synchronized static Map getSessions()
    {
        if (sessions == null)
        {
            sessions = new HashMap();
        }
        return sessions;
    }

    private synchronized static RFQCache getRFQCache(String sessionName)
    {

        Map sessions = getSessions();
        RFQCache theRFQCache = (RFQCache)sessions.get(sessionName);
        if (theRFQCache == null)
        {
            theRFQCache = new RFQCacheImpl();
            sessions.put(sessionName, theRFQCache);
        }

        registerRFQCache(sessionName, theRFQCache);

        return theRFQCache;
    }

    public static RFQCache create(String sessionName)
    {
        return getRFQCache(sessionName);
    }

    public static RFQCache find(String sessionName)
    {
        return create(sessionName);
    }
}
