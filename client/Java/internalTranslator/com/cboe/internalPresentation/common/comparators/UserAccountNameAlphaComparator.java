//
// -----------------------------------------------------------------------------------
// Source file: UserAccountNameAlphaComparator.java
//
// PACKAGE: com.cboe.internalPresentation.common.comparators;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.comparators;

import com.cboe.interfaces.internalPresentation.user.UserAccountModel;

/**
 * Compares UserAccountModel's and return a -1, 0, or 1 to allow ordering.
 * The compare is done on fullName and userId, case sensitive.
 */
public class UserAccountNameAlphaComparator extends UserAccountIDAlphaComparator
{
    public int compare(UserAccountModel user1, UserAccountModel user2)
    {
        int result;

        if(user1 == user2)
        {
            result = 0;
        }
        else
        {
            result = user1.getFullName().compareTo(user2.getFullName());

            if(result == 0)
            {
                result = super.compare(user1, user2);
            }
        }
        return result;
    }
}