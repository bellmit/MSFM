//
// -----------------------------------------------------------------------------------
// Source file: MessageDetail.java
//
// PACKAGE: com.cboe.presentation.alarms.events.consumers
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms.events.consumers;

import com.cboe.idl.infrastructureServices.loggingService.corba.Message;
/**
 * 
 */
public class MessageDetail
{
    private Message message;
    private boolean cleared;

    public MessageDetail(Message msg, boolean clrd)
    {
        message = msg;
        cleared = clrd;
    }

    public Message getMessage()
    {
        return message;
    }

    public boolean isCleared()
    {
        return cleared;
    }

    public void setMessage(Message msg)
    {
        message = msg;
    }

    public void setCleared(boolean clrd)
    {
        cleared = clrd;
    }
}
