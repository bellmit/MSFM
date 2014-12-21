//
// -----------------------------------------------------------------------------------
// Source file: EnablementComparator.java
//
// PACKAGE: com.cboe.internalPresentation.common.comparators;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.comparators;

import java.util.*;

import com.cboe.interfaces.internalPresentation.user.Enablement;

import com.cboe.presentation.common.comparators.SessionProductClassComparator;

/**
 *  This comparator compares two enablements to be able to sort
 *  them by session name, class name, operation type
 */
public class EnablementComparator implements Comparator<Enablement>
{
    SessionProductClassComparator spcComparator = new SessionProductClassComparator();

    public EnablementComparator()
    {
        super();
    }

    /**
     * compare method comment.
     */
    public int compare(Enablement enablement1, Enablement enablement2)
    {
        if(enablement1 == enablement2)
        {
            return 0;
        }
        else
        {
            int spcResult = spcComparator.compare(enablement1.getSessionProductClass(),enablement2.getSessionProductClass());
            if (spcResult == 0)
            {
                if (enablement1.getOperationType() == enablement2.getOperationType())    
                {
                    return 0;
                }
                else if (enablement1.getOperationType() < enablement2.getOperationType())
                {
                    return -1;
                }
                else
                {
                    return 1;
                }
            }
            else
            {
                return spcResult;
            }
        }
    }
}
