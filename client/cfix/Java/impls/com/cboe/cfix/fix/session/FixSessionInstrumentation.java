package com.cboe.cfix.fix.session;

/**
 * FixSessionInstrumentation.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.util.*;

import com.cboe.cfix.interfaces.*;
import com.cboe.client.util.*;
import com.cboe.client.util.collections.*;
import com.cboe.instrumentationService.instrumentors.*;

public class FixSessionInstrumentation implements FixSessionInstrumentationIF
{
    public long                          startTime;
    public long                          endTime;
    public Map                           recvMsgTypesMap    = new HashMap(64);
    public Map                           sentMsgTypesMap    = new HashMap(64);
    public ObjectReferenceCountMap       sentMDReqIDMap     = new ObjectReferenceCountMap();
    public ObjectReferenceCountMap       rejectedMDReqIDMap = new ObjectReferenceCountMap();
    public NetworkConnectionInstrumentor networkConnectionInstrumentor;

    public FixSessionInstrumentation(NetworkConnectionInstrumentor networkConnectionInstrumentor)
    {
        this.networkConnectionInstrumentor = networkConnectionInstrumentor;
    }

    public NetworkConnectionInstrumentor getNetworkConnectionInstrumentor()
    {
        return networkConnectionInstrumentor;
    }

    public void addBytesSent(int bytesSent)
    {
        networkConnectionInstrumentor.incBytesSent(bytesSent);
    }

    public void addBytesReceived(int bytesReceived)
    {
        networkConnectionInstrumentor.incBytesReceived(bytesReceived);
    }

    public long getTotalBytesSent()
    {
        return networkConnectionInstrumentor.getBytesSent();
    }

    public long getTotalBytesReceived()
    {
        return networkConnectionInstrumentor.getBytesReceived();
    }

    public boolean isStarted()
    {
        return startTime != 0;
    }

    public Map getRecvMsgTypesMap()
    {
        return recvMsgTypesMap;
    }

    public void addSentMDReqID(String mdReqID, int add)
    {
        sentMDReqIDMap.incKeyValue(mdReqID, add);
    }

    public void incSentMDReqID(String mdReqID)
    {
        sentMDReqIDMap.incKeyValue(mdReqID);
    }

    public ObjectReferenceCountMap getSentMDReqIDMap()
    {
        return sentMDReqIDMap;
    }

    public void incRejectedMDReqID(String mdReqID)
    {
        rejectedMDReqIDMap.incKeyValue(mdReqID);
    }

    public ObjectReferenceCountMap getRejectedMDReqIDMap()
    {
        return rejectedMDReqIDMap;
    }

    public void incRecvMsgType(String msgType)
    {
        MutableInteger mutableInteger = (MutableInteger) recvMsgTypesMap.get(msgType);
        if (mutableInteger == null)
        {
            recvMsgTypesMap.put(msgType, new MutableInteger(1));
        }
        else
        {
            mutableInteger.inc();
        }
    }

    public Map getSentMsgTypesMap()
    {
        return sentMsgTypesMap;
    }

    public void incSentMsgType(String msgType)
    {
        networkConnectionInstrumentor.incMsgsSent(1);

        MutableInteger mutableInteger = (MutableInteger) sentMsgTypesMap.get(msgType);
        if (mutableInteger == null)
        {
            sentMsgTypesMap.put(msgType, new MutableInteger(1));
        }
        else
        {
            mutableInteger.inc();
        }
    }

    public long getNetworkPacketsSent()
    {
        return networkConnectionInstrumentor.getPacketsSent();
    }

    public void incNetworkPacketsSent()
    {
        networkConnectionInstrumentor.setLastTimeSent(System.currentTimeMillis());
        networkConnectionInstrumentor.incPacketsSent(1);
    }

    public void incValidNetworkPacketsReceived()
    {
        networkConnectionInstrumentor.incPacketsReceived(1);
    }

    public void incInvalidNetworkPacketsReceived()
    {
        networkConnectionInstrumentor.incInvalidPacketsReceived(1);
    }

    public void incGarbageNetworkPacketsReceived()
    {
        // nop
    }

    public void incTotalNetworkPacketsReceived()
    {
        networkConnectionInstrumentor.setLastTimeReceived(System.currentTimeMillis());
        networkConnectionInstrumentor.incPacketsReceived(1);
    }

    public void start()
    {
        startTime                     = System.currentTimeMillis();
        endTime                       = 0;
    }

    public void stop()
    {
        endTime = System.currentTimeMillis();
    }

    public Object clone() throws CloneNotSupportedException
    {
        FixSessionInstrumentation clone = (FixSessionInstrumentation) super.clone();

        //TODO: See if we should make this better for MT purposes

        clone.recvMsgTypesMap     = new HashMap(recvMsgTypesMap);
        clone.sentMsgTypesMap     = new HashMap(sentMsgTypesMap);
        clone.sentMDReqIDMap      = new ObjectReferenceCountMap(sentMDReqIDMap);
        clone.rejectedMDReqIDMap  = new ObjectReferenceCountMap(rejectedMDReqIDMap);

        return clone;
    }
}
