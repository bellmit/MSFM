//
// -----------------------------------------------------------------------------------
// Source file: ProfileModel.java
//
// PACKAGE: com.cboe.interfaces.presentation.user;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.user;

import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.presentation.product.SessionProductClass;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;

public interface ProfileModel extends MutableBusinessModel, Profile
{
    public void setProductClass ( SessionProductClass productClass );

    public void setProductClass(String sessionName, int classKey )
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            NotFoundException;

    public void setAccount ( String account );

    public void setSubAccount ( String subAccount );

    public void setExecutingGiveupFirm ( ExchangeFirm executingGiveupFirm );

    public void setExecutingGiveupFirm ( ExchangeFirmStruct executingGiveupFirm );

    public void setAccountIgnored(boolean ignored);

    public void setOriginCode(char originCode);
}