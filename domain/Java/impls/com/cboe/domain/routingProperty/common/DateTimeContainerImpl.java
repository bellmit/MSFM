//
//-----------------------------------------------------------------------------------
//Source file: DateTimeContainerImpl.java
//
//PACKAGE: com.cboe.domain.routingProperty.common
//
//-----------------------------------------------------------------------------------
//Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------

package com.cboe.domain.routingProperty.common;

import com.cboe.interfaces.domain.routingProperty.common.DateTimeContainer;


/**
* Wrapper for long value
*/
public class DateTimeContainerImpl implements DateTimeContainer
{
 private long time;

 
 
 public DateTimeContainerImpl(long value)
 {
     time = value;
 }


    public void setTime(long value)
    {
        time = value;
    }

    public long getTime()
    {
        return time;
    }
}
