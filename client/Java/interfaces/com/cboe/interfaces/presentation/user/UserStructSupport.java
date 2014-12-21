package com.cboe.interfaces.presentation.user;

import com.cboe.idl.cmiUser.UserStruct;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;

/**
 * -----------------------------------------------------------------------------------
 * Source file: UserStructSupport.java
 *
 * PACKAGE: com.cboe.presentation.user;
 *
 * -----------------------------------------------------------------------------------
 * Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
 * -----------------------------------------------------------------------------------
 */
public interface UserStructSupport
{
    /** Returns the UserStruct that backs the implemented model class */
    public SessionProfileUserStruct getUserStruct();

    /** Returns the UserStruct that backs the implemented model class */
    public void setUserStruct(SessionProfileUserStruct userStruct);
}
