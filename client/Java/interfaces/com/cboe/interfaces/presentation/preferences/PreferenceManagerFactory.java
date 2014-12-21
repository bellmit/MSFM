//
// -----------------------------------------------------------------------------------
// Source file: com/cboe/presentation/common/cboePreferences/PreferenceManagerFactory.java
//
// PACKAGE: com.cboe.presentation.preferences;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.preferences;

/**
 * This factory provides access to all the preference managers
 */
public interface PreferenceManagerFactory
{
    /**
     * Returns the PreferenceManager responsible for business preferences
     * @return a PreferenceManager
     */
    BusinessPreferenceManager findBusinessPreferenceManager();

    /**
     * Returns the PreferenceManager responsible for GUI preferences
     * @return a PreferenceManager
     */
    PreferenceManager findGUIPreferenceManager();
}