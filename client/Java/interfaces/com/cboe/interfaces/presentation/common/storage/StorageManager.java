//
// ------------------------------------------------------------------------
// FILE: StorageManager.java
// 
// PACKAGE: com.cboe.interfaces.presentation.common.storage
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.presentation.common.storage;

public interface StorageManager
{
    Storage getLocalStorage();

    Storage getRemoteStorage();

    Storage getCASInfoStorage();

    Storage getOrbNameAliasStorage();

    Storage getLogicalNameStorage();

    Storage getSQLQueryStorage();

    Storage getARCommandFavoriteStorage();

    Storage getXtpGuiUserPrefStorage();
}
