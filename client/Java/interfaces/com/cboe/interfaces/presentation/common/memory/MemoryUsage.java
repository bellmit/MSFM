//
// ------------------------------------------------------------------------
// FILE: MemoryUsage.java
// 
// PACKAGE: com.cboe.interfaces.presentation.common.memory
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.interfaces.presentation.common.memory;

public interface MemoryUsage
{
    public static final long MB = 1024 * 1024;
    int getFreeMemoryMb();
    int getUsedMemoryMb();
    long getFreeMemory();
    long getUsedMemory();
    double getPercentUsage();
    double getUsageThreshold();
    Object clone() throws CloneNotSupportedException;
}
