//
// -----------------------------------------------------------------------------------
// Source file: JointAccountComparator.java
//
// PACKAGE: com.cboe.internalPresentation.common.comparators;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.comparators;

import java.util.*;

import com.cboe.interfaces.internalPresentation.user.JointAccount;
import com.cboe.presentation.common.comparators.ExchangeFirmComparator;

/**
 * Compares JointAccount's and return a -1, 0, or 1 to allow ordering.
 * The compare is done on all fields, exact contents.
 * Note: this comparator imposes orderings that are inconsistent with equals
 */
public class JointAccountComparator implements Comparator
{
    ExchangeFirmComparator exchangeFirmComparator = new ExchangeFirmComparator();

    /**
     * JointAccountComparator constructor comment.
     */
    public JointAccountComparator()
    {
        super();
    }

    /**
     * compare method comment.
     */
    public int compare(Object arg1, Object arg2)
    {
        if(arg1 == arg2)
        {
            return 0;
        }
        else if(arg1 instanceof JointAccount && arg2 instanceof JointAccount)
        {
            JointAccount account1 = (JointAccount)arg1;
            JointAccount account2 = (JointAccount)arg2;

            if(account1.getAccountStruct() != account2.getAccountStruct())
            {
                int compareAccount = account1.getAccountName().compareTo(account2.getAccountName());

                if(compareAccount == 0)
                {
                    if(!account1.isActive() && account2.isActive())
                    {
                        return -1;
                    }
                    else if(account1.isActive() && !account2.isActive())
                    {
                        return 1;
                    }
                    else
                    {
                        if(!account1.isPrimaryDPMParticipant() && account2.isPrimaryDPMParticipant())
                        {
                            return -1;
                        }
                        else if(account1.isPrimaryDPMParticipant() && !account2.isPrimaryDPMParticipant())
                        {
                            return 1;
                        }
                        else
                        {
                            int compareFirm = exchangeFirmComparator.compare(account1.getExecutingGiveupFirm(), account2.getExecutingGiveupFirm());

                            if(compareFirm == 0)
                            {
                                Calendar calendar1 = account1.getLastModifiedTime();
                                Calendar calendar2 = account2.getLastModifiedTime();

                                if(calendar1.before(calendar2))
                                {
                                    return -1;
                                }
                                else if(calendar1.after(calendar2))
                                {
                                    return 1;
                                }
                                else
                                {
                                    return 0;
                                }
                            }
                            else
                            {
                                return compareFirm;
                            }
                        }
                    }
                }
                else
                {
                    return compareAccount;
                }
            }
            else
            {
                return 0;
            }
        }
        else
        {
            return -1;
        }
    }
}
