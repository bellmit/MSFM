package com.cboe.interfaces.domain;

import com.cboe.idl.cmiTraderActivity.*;

public interface ActivityHistory
{
    public long getEventTime();
    public short getEventType();
    public ActivityRecordStruct toActivityRecord();
}
