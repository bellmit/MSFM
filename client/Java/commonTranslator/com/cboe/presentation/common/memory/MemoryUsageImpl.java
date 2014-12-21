//
// ------------------------------------------------------------------------
// FILE: MemoryUsageImpl.java
// 
// PACKAGE: com.cboe.presentation.common.memory
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.common.memory;

import com.cboe.interfaces.presentation.common.memory.MemoryUsage;

/**
 * @author torresl@cboe.com
 */
public class MemoryUsageImpl
        implements MemoryUsage
{
    protected long freeMemory;
    protected long usedMemory;
    protected int freeMemoryMb;
    protected int usedMemoryMb;
    protected double percentUsage;
    protected double usageThreshold;
    private String displayString;
    public MemoryUsageImpl(long freeMemory, long usedMemory, double percentUsage, double usageThreshold)
    {
        resetValues(freeMemory, usedMemory, percentUsage, usageThreshold);
    }

    protected void resetValues(long freeMemory, long usedMemory, double percentUsage, double usageThreshold)
    {
        this.freeMemory = freeMemory;
        this.usedMemory = usedMemory;
        this.percentUsage = percentUsage;
        this.usageThreshold = usageThreshold;
        this.displayString = null;
        usedMemoryMb = (int) (usedMemory / MB);
        freeMemoryMb = (int) (freeMemory / MB);
    }

    public long getFreeMemory()
    {
        return freeMemory;
    }

    public long getUsedMemory()
    {
        return usedMemory;
    }

    public int getFreeMemoryMb()
    {
        return freeMemoryMb;
    }

    public int getUsedMemoryMb()
    {
        return usedMemoryMb;
    }

    public double getPercentUsage()
    {
        return percentUsage;
    }

    public double getUsageThreshold()
    {
        return usageThreshold;
    }

    public String toString()
    {
        if(displayString == null)
        {
            StringBuffer buffer = new StringBuffer(20);
            buffer.append(" ").append(usedMemoryMb).append("M of ").append(MemoryWatcher.getInstance().getMaxMemMb()).append("M ");
            displayString = buffer.toString();
        }
        return displayString;
    }

    public Object clone() throws CloneNotSupportedException
    {
        MemoryUsageImpl impl = new MemoryUsageImpl(freeMemory, usedMemory, percentUsage, usageThreshold);
        return impl;
    }
}
