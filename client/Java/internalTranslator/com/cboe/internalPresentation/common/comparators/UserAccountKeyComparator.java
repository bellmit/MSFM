//
// -----------------------------------------------------------------------------------
// Source file: UserAccountKeyComparator.java
//
// PACKAGE: com.cboe.internalPresentation.common.comparators;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.comparators;

import java.util.Comparator;

import com.cboe.interfaces.internalPresentation.user.UserAccountModel;

/**
 * Used to compare UserAccountModel's by user key.
 */
public class UserAccountKeyComparator implements Comparator
{
    /**
    * UserAccountKeyComparator constructor comment.
    */
    public UserAccountKeyComparator()
    {
        super();
    }

    /**
    * Compares two UserAccountModel's based on user key.
    */
    public int compare(Object arg1, Object arg2)
    {
        if(arg1 == arg2)
        {
            return 0;
        }
        else
        {
            UserAccountModel user1 = (UserAccountModel)arg1;
            UserAccountModel user2 = (UserAccountModel)arg2;

            if(user1.getUserKey() < user2.getUserKey())
            {
                return -1;
            }
            else if(user1.getUserKey() > user2.getUserKey())
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
    }
}
