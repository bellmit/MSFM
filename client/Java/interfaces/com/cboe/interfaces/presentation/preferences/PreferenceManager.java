//
// -----------------------------------------------------------------------------------
// Source file: com/cboe/presentation/common/cboePreferences/PreferenceManager.java
//
// PACKAGE: com.cboe.presentation.preferences;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.preferences;

/**
 * Defines the contract that a PreferenceManager should provide.
 */
public interface PreferenceManager
{
    /**
     * Loads the preferences from the source.
     */
    void loadPreferences();

    /**
     * Saves the preferences to the source.
     */
    void savePreferences();

    /**
     * Gets the collection of preferences from the Manager
     * @return collection of preferences
     */
    PreferenceCollection getPreferences();

    /**
     * Gets the master collection (contains preferences for ALL versions) from the Manager
     * @return collection of preferences
     */
    PreferenceCollection getMasterPreferenceCollection();

    /**
     * Sets the master collection (contains preferences for ALL versions) on the Manager
     * @return collection of preferences
     */
    void setMasterPreferenceCollection(PreferenceCollection preferences);
    
    /**
     * Gets an array of PreferenceCollection's where each element is a different version of preferences
     * that the user has collected.
     */
    PreferenceCollection[] getAllPreferenceVersions();

}