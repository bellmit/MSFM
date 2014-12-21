//
// -----------------------------------------------------------------------------------
// Source file: UserNameAcronymIdNonCaseComparator.java
//
// PACKAGE: com.cboe.internalPresentation.common.comparators;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.comparators;

import com.cboe.interfaces.internalPresentation.user.UserAccountModel;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;

/**
 * Compares UserAccountModel's and return a -1, 0, or 1 to allow ordering.
 * The compare is done on full name, acronym, then exchange, then by user id, all case insensitive.
 */
public class UserNameAcronymIdNonCaseComparator extends UserAccountIDAlphaNonCaseComparator
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
            result = user1.getFullName().compareToIgnoreCase(user2.getFullName());

            if(result == 0)
            {
                ExchangeAcronym user1ExchAcro = user1.getExchangeAcronym();
                ExchangeAcronym user2ExchAcro = user2.getExchangeAcronym();

                result = user1ExchAcro.getAcronym().compareToIgnoreCase(user2ExchAcro.getAcronym());

                if(result == 0)
                {
                    result = user1ExchAcro.getExchange().compareToIgnoreCase(user2ExchAcro.getExchange());

                    if(result == 0)
                    {
                        result = super.compare(user1, user2);
                    }
                }
            }
        }

        return result;
    }
}