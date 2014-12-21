// -----------------------------------------------------------------------------------
// Source file: VolumeType.java
//
// PACKAGE: com.cboe.interfaces.presentation.marketData;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

package com.cboe.interfaces.presentation.marketData;

public interface VolumeType
{
    public String getName();

    public int getKey();

    boolean equals(Object obj);

    String toString();

}
