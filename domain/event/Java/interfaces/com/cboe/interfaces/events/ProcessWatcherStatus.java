//
// ------------------------------------------------------------------------
// FILE: ProcessWatcherStatus.java
// 
// PACKAGE: com.cboe.interfaces.events
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.interfaces.events;

public interface ProcessWatcherStatus
{
    public static final int PROCESS_DOWN = 1;
    public static final int PROCESS_UP = 2;
    /**
     * These two events can be used to determine master/slave state
     * POA_UP - is used to indicate the associated process is in a master state
     * POA_DOWN - is used to indicate the associated process is in a slave state
     */
    public static final int POA_DOWN = 3;
    public static final int POA_UP = 4;

}
