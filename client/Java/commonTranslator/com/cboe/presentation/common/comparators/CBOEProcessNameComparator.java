//
// -----------------------------------------------------------------------------------
// Source file: CBOEProcessNameComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.interfaces.presentation.processes.CBOEProcess;

public class CBOEProcessNameComparator implements Comparator
{
    public int compare(Object o1, Object o2)
    {
        String name1 = ((CBOEProcess) o1).getDisplayName();
        String name2 = ((CBOEProcess) o2).getDisplayName();

        return name1.compareTo(name2);
    }
}
