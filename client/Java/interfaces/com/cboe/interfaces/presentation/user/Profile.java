//
// -----------------------------------------------------------------------------------
// Source file: Profile.java
//
// PACKAGE: com.cboe.interfaces.presentation.user;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.user;

import com.cboe.idl.cmiUser.SessionProfileStruct;

import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

public interface Profile extends BusinessModel
{
    public SessionProductClass getProductClass();

    public int getClassKey();

    public String getSessionName();

    public String getAccount();

    public String getSubAccount();

    public ExchangeFirm getExecutingGiveupFirm();

    public char getOriginCode();

    public SessionProfileStruct getProfileStruct();

    public boolean isDefaultProfile();

    public boolean isAccountIgnored();
}