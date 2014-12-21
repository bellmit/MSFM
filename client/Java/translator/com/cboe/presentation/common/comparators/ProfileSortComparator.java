//
// -----------------------------------------------------------------------------------
// Source file: ProfileSortComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import com.cboe.interfaces.presentation.user.Profile;
import com.cboe.interfaces.presentation.product.SessionProductClass;

public class ProfileSortComparator extends SessionProductClassComparator
{
    public ProfileSortComparator()
    {
        super();
    }

    public int compare(Object o1, Object o2)
    {
        int result = 0;

        if(o1 != o2)
        {
            Profile o1Profile = (Profile) o1;
            Profile o2Profile = (Profile) o2;
            SessionProductClass pc1 = o1Profile.getProductClass();
            SessionProductClass pc2 = o2Profile.getProductClass();
        
            if (!pc1.isValid() || pc1.isDefaultSession())
            {
                result = -1;
            }
            else if (!pc2.isValid() || pc1.isDefaultSession())
            {
                result = 1;
            }
            else
            {
                result = super.compare(pc1, pc2);
            }
        }

        return result;
    }
}
