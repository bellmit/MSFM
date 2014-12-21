//
// -----------------------------------------------------------------------------------
// Source file: UserSessionListener.java
//
// PACKAGE: com.cboe.presentation.userSession;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.userSession;

import java.util.*;

/**
 * Defines the contract that a class should implement to be notified
 * of user session events.
 */
public interface UserSessionListener extends EventListener
{
    /**
     * A UserSession has been changed.
     * @param event identifying change
     */
    public void userSessionChange(UserSessionEvent event);
}