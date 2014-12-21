//
// -----------------------------------------------------------------------------------
// Source file: ARCommandFullNameComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.interfaces.instrumentation.adminRequest.ARCommand;

public class ARCommandFullNameComparator implements Comparator<ARCommand>
{
    public int compare(ARCommand command1, ARCommand command2)
    {
        return command1.getFullName().compareTo(command2.getFullName());
    }
}
