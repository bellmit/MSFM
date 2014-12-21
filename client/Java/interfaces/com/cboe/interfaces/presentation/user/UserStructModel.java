//
// -----------------------------------------------------------------------------------
// Source file: UserStructModel.java
//
// PACKAGE: com.cboe.interfaces.presentation.user;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.user;

import com.cboe.interfaces.presentation.user.*;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.dpm.DPMModel;
import com.cboe.idl.cmiUser.UserStruct;

public interface UserStructModel extends UserModel, UserStructSupport
{
    String getFirm();
    int [] getSotedClassKeys();
    Account[] getAccounts();
}
