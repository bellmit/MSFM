// -----------------------------------------------------------------------------------
// Source file: UserFirmAffiliationFactory
//
// PACKAGE: com.cboe.internalPresentation.userFirmAffiliation
// 
// Created: Oct 19, 2004 9:36:51 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.user;

import com.cboe.interfaces.internalPresentation.user.UserFirmAffiliation;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.idl.user.UserFirmAffiliationStruct;

public class UserFirmAffiliationFactory
{
    private UserFirmAffiliationFactory() {}

    public static UserFirmAffiliation create(UserFirmAffiliationStruct struct)
    {
        return new UserFirmAffiliationImpl(struct);
    }

    public static UserFirmAffiliation[] create(UserFirmAffiliationStruct[] structs)
    {
        UserFirmAffiliation[] resultArray = new UserFirmAffiliation[0];

        if (structs != null && structs.length > 0)
        {
            resultArray = new UserFirmAffiliation[structs.length];
            for (int i=0; i<resultArray.length; i++)
            {
                resultArray[i] = new UserFirmAffiliationImpl(structs[i]);
            }
        }

        return resultArray;
    }

    public static UserFirmAffiliation create(ExchangeAcronym exchangeAcronym, String firm)
    {
        return new UserFirmAffiliationImpl(new UserFirmAffiliationStruct(exchangeAcronym.getExchangeAcronymStruct(), firm));
    }

}
