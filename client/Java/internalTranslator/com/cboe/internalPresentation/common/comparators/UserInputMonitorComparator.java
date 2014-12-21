//
// -----------------------------------------------------------------------------------
// Source file: UserInputMonitorComparator.java
//
// PACKAGE: com.cboe.internalPresentation.common.comparators;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.comparators;

import java.util.Comparator;
import com.cboe.interfaces.domain.uim.UserInputMonitorEntry;


/**
 *  This comparator compares two UserInputMonitorEntry impls to be able to sort them by session name 
 */
public class UserInputMonitorComparator implements Comparator<UserInputMonitorEntry>
{
    public UserInputMonitorComparator()
    {
    }

 
    public int compare(UserInputMonitorEntry uim1, UserInputMonitorEntry uim2)
    {
        return uim1 == uim2 ? 0 : uim1.getSessionName().compareToIgnoreCase(uim2.getSessionName());
    }
}
