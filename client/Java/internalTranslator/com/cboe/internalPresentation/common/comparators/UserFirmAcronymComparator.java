//
// -----------------------------------------------------------------------------------
// Source file: UserFirmAcronymComparator.java
//
// PACKAGE: com.cboe.internalPresentation.common.comparators
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.comparators;

import com.cboe.interfaces.internalPresentation.user.UserAccountModel;
import com.cboe.interfaces.internalPresentation.firm.FirmModel;

public class UserFirmAcronymComparator extends FirmAcronymNumberNameComparator
{
    private UserAcronymAlphaNonCaseComparator userAcronymComparator;

    public UserFirmAcronymComparator()
    {
        userAcronymComparator = new UserAcronymAlphaNonCaseComparator();
    }

    public int compare(Object arg1, Object arg2)
    {
        int result;
        if(arg1 == arg2)
        {
            result = 0;
        }
        else
        {
            if(arg1 instanceof FirmModel && arg2 instanceof FirmModel)
            {
                result = super.compare(arg1, arg2);
            }
            else if(arg1 instanceof FirmModel && arg2 instanceof UserAccountModel)
            {
                result = -1;
            }
            else if(arg1 instanceof UserAccountModel && arg2 instanceof FirmModel)
            {
                result = 1;
            }
            else
            {
                UserAccountModel user1 = (UserAccountModel) arg1;
                UserAccountModel user2 = (UserAccountModel) arg2;

                FirmModel user1Firm = user1.getFirmModel();
                FirmModel user2Firm = user2.getFirmModel();

                if(user1Firm != null && user2Firm != null)
                {
                    result = super.compare(user1Firm, user2Firm);

                    if(result == 0)
                    {
                        result = userAcronymComparator.compare(user1, user2);
                    }
                }
                else if(user1Firm == null && user2Firm != null)
                {
                    result = 1;
                }
                else
                {
                    result = -1;
                }
            }
        }

        return result;
    }
}
