//
// -----------------------------------------------------------------------------------
// Source file: SubjectData.java
//
// PACKAGE: com.cboe.infra.presentation.network
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.infra.presentation.network;

import com.cboe.utils.monitoringService.TransportPSSubject;

/**
 * A static copy of an TransportPSSubject object.  These objects are initialized
 * with "live" TransportPSSubject objects, from which they copy their data, but after
 * initialization, no connection exists between the SubjectData object and its original
 * source, creating in effect, a "snap shot" of the TransportPSSubject object.
 */
public class SubjectData
{
    String name;
    long msgsSent;
    long bytesSent;
    long msgsReceived;
    long bytesReceived;

    /**
     * Create a SubjectData and initialize its data from the TransportPSSubject source.
     * After creation, the SubjectData object is independent of the source; that is
     * if the number of messages sent or received changes in the TransportPSSubject,
     * no change is reflected in the SubjectData object.
     * @param source
     */
    public SubjectData(TransportPSSubject source)
    {
        name = source.getSubject();
        msgsSent = source.getMsgsSent();
        bytesSent = source.getBytesSent();
        msgsReceived = source.getMsgsRecv();
        bytesReceived = source.getBytesRecv();
    }

    public long getBytesReceived() {
        return bytesReceived;
    }

    public long getBytesSent() {
        return bytesSent;
    }

    public long getMsgsReceived() {
        return msgsReceived;
    }

    public long getMsgsSent() {
        return msgsSent;
    }

    public String getName() {
        return name;
    }
}
