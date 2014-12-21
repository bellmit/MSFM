//
// -----------------------------------------------------------------------------------
// Source file: UserAccountIDAlphaComparator.java
//
// PACKAGE: com.cboe.internalPresentation.common.comparators;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.comparators;

import java.util.Comparator;

import com.cboe.interfaces.internalPresentation.user.UserAccountModel;

/**
 * Compares UserAccountModel's and return a -1, 0, or 1 to allow ordering.
 * The compare is done on userId, case sensitive.
 */
public class UserAccountIDAlphaComparator implements Comparator<UserAccountModel>
{
    public int compare(UserAccountModel user1, UserAccountModel user2)
    {
        if(user1 == user2)
        {
            return 0;
        }
        else
        {
            return user1.getUserId().compareTo(user2.getUserId());
        }
    }
}