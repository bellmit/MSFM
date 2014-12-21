package com.cboe.domain.util;

import com.cboe.infraUtil.DateTypeStruct;

public final class CalendarUpdateEventHolder
{
    private final DateTypeStruct[] dateTypeStructs;
    private final short updateType;

    public CalendarUpdateEventHolder(DateTypeStruct[] dateTypeStructs, short updateType)
    {
        this.dateTypeStructs = dateTypeStructs;
        this.updateType = updateType;
    }
    
    public DateTypeStruct[] getDateTypeStructs()
    {
        return dateTypeStructs;    
    }
    
    public short getUpdateType()
    {
        return updateType;
    }
}
