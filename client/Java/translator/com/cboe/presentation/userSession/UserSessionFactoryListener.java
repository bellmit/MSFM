//
// -----------------------------------------------------------------------------------
// Source file: UserSessionFactoryListener.java
//
// PACKAGE: com.cboe.presentation.userSession;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.userSession;

/**
 * Defines the contract that a class should implement to be notified
 * of user session factory events.
 */
public interface UserSessionFactoryListener
{
    /**
     * The UserSessionFactory has been initialized
     */
    public void userSessionFactoryInit();
}