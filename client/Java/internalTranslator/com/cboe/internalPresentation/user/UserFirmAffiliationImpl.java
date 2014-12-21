// -----------------------------------------------------------------------------------
// Source file: UserFirmAffiliationImpl
//
// PACKAGE: com.cboe.internalPresentation.userFirmAffiliation
// 
// Created: Oct 18, 2004 4:05:19 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.user;

//import java.beans.PropertyChangeListener;
//import java.beans.PropertyChangeSupport;

import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
import com.cboe.presentation.user.ExchangeAcronymFactory;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.internalPresentation.user.UserFirmAffiliation;
import com.cboe.idl.user.UserFirmAffiliationStruct;

public class UserFirmAffiliationImpl extends AbstractBusinessModel implements UserFirmAffiliation
{
    private UserFirmAffiliationStruct struct;
    private ExchangeAcronym userAcronym;

    public UserFirmAffiliationImpl(UserFirmAffiliationStruct struct)
    {
        if (struct == null)
        {
            throw new IllegalStateException("UserFirmAffiliationStruct struct cannot be NULL");
        }

        setUserFirmAffiliationStruct(struct);
    }

    public String getAcronym()
    {
        return userAcronym.getAcronym();
    }

    public String getExchange()
    {
        return userAcronym.getExchange();
    }

    public ExchangeAcronym getExchangeAcronym()
    {
        return userAcronym;
    }

    public String getAffiliatedFirm()
    {
        return struct.affiliatedFirm;
    }

    public UserFirmAffiliationStruct getUserFirmAffiliationStruct()
    {
        return struct;
    }

    public String toString()
    {
        return getAcronym() + ":" + getAffiliatedFirm();
    }

    public int hashCode()
    {
        return getAffiliatedFirm().hashCode() + getAcronym().hashCode();
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        else if (obj == null)
        {
            return false;
        }
        else if (getClass() == obj.getClass())
        {
            UserFirmAffiliation castedObj = (UserFirmAffiliation)obj;

            if (getAffiliatedFirm().equals(castedObj.getAffiliatedFirm()) &&
                getAcronym().equals(castedObj.getAcronym()))
            {
                return true;
            }
        }

        return false;
    }

    public void setAffiliatedFirm(String newFirm)
    {
        struct.affiliatedFirm = newFirm;
    }

    public void setUserFirmAffiliationStruct(UserFirmAffiliationStruct newStruct)
    {
        UserFirmAffiliationStruct oldStruct = getUserFirmAffiliationStruct();

        if (oldStruct != newStruct)
        {
            struct = newStruct;
            userAcronym = ExchangeAcronymFactory.createExchangeAcronym(struct.userAcronym);
        }
    }
}
