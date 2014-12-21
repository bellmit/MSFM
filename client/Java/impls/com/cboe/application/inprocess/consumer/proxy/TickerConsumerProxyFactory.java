package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.interfaces.callback.TickerV2Consumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jing Chen.
 */
public class TickerConsumerProxyFactory
{
    protected static HashMap consumers;
    public TickerConsumerProxyFactory()
    {
    }

    private static Map getConsumers()
    {
        if(consumers==null)
        {
            consumers = new HashMap(11);
        }
        return consumers;
    }

    private static Map getMap(Map keyTable, Object key)
    {
        Map lookupHash = (Map) keyTable.get(key);
        if (lookupHash == null)
        {
            lookupHash = new HashMap();
            keyTable.put(key, lookupHash);
        }
        return lookupHash;
    }

    protected static TickerConsumerProxy create(BaseSessionManager sessionManager,
                                                SessionKeyContainer sessionClass,
                                                TickerV2Consumer consumer)
    {
        TickerConsumerProxy proxy = new TickerConsumerProxy(consumer, sessionManager);
        Map sessionClasses = getMap(getConsumers(), sessionManager);
        sessionClasses.put(sessionClass, proxy);
        return proxy;
    }

    public static TickerConsumerProxy find(BaseSessionManager sessionManager,
                                           SessionKeyContainer sessionClass,
                                           TickerV2Consumer consumer)
    {
        return create(sessionManager, sessionClass, consumer);
    }

    public static void remove(BaseSessionManager sessionManager)
    {
        getConsumers().remove(sessionManager);
    }
}
