//
// -----------------------------------------------------------------------------------
// Source file: com/cboe/presentation/common/cboePreferences/PreferenceCollection.java
//
// PACKAGE: com.cboe.presentation.preferences;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.preferences;

import java.util.Properties;

import com.cboe.idl.cmiUser.PreferenceStruct;

/**
 * Defines the contract that a preference collection should adhere to.
 */
public interface PreferenceCollection
{
    /**
     * Adds another collection of preferences.
     * @param preferences to add
     */
    void addPreferences(PreferenceCollection preferences);

    /**
     * Adds a preference
     * @param preference to add
     */
    void addPreference(CBOEPreference preference);

    /**
     * Adds a preferences
     * @param preferences to add
     */
    void addPreferences(PreferenceStruct[] preferences);

    /**
     * Adds a set of preferences
     * @param preferences to add
     */
    void addPreferences(CBOEPreference[] preferences);

    /**
     * Adds a preference
     * @param name of preference
     * @param value of preference
     */
    void addPreference(String name, String value);

    /**
     * Gets a preference by name
     * @param name of preference
     * @return preference
     */
    CBOEPreference getPreference(String name);

    /**
     * Gets an array of all the preferences.
     */
    CBOEPreference[] getPreferences();

    /**
     * Gets a section of the collection and returns it as a collection
     * @param sectionName to locate
     * @return collection of preferences obtained with the name passed. Will return an empty collection
     * if <code>sectionName</code> not found.
     */
    PreferenceCollection getSection(String sectionName);

    /**
     * Gets a section of the collection and returns it as a collection
     * @param sectionName to locate
     * @param includeDefaults True if defaults should be included, false if not.
     * @return collection of preferences obtained with the name passed. Will return an empty collection
     * if <code>sectionName</code> not found.
     */
    PreferenceCollection getSection(String sectionName, boolean includeDefaults);

    /**
     * Gets the number of preferences in the collection.
     * @return size of collection
     */
    int getSize();

    /**
     * Clears this collection.
     */
    void clear();

    /**
     * Removes a collection preferences from the called collection.
     * @param preferences to remove each from called collection.
     */
    void removePreferences(PreferenceCollection preferences);

    /**
     * Removes the preferences from this collection if found by name.
     * @param preferences to remove
     */
    void removePreferences(CBOEPreference[] preferences);

    /**
     * Removes the preference from this collection if found by name.
     * @param preference to remove
     * @return value of preference removed
     */
    String removePreference(CBOEPreference preference);

    /**
     * Removes the preference from this collection if found by name.
     * @param preferenceName of preference to remove
     * @return value of preference removed
     */
    String removePreference(String preferenceName);

    /**
     * Gets all the preferences this collection contains as structs.
     * @return an array of PreferenceStruct's
     */
    PreferenceStruct[] toPreferenceStructArray();

    /**
     * Returns the preference collection as <tt>Properties</tt>.
     */
    Properties toProperties();

    /**
     * Gets the preference name that represents this collection of preferences
     * @return name used to represent these preferences. The name should be added to the context
     * of all preferences added to this collection.
     */
    String getPreferenceName();

    /**
     * Sets the preference name that represents this collection of preferences
     * @param preferenceName used to represent these preferences. The name should be added to the context
     * of all preferences added to this collection.
     */
    void setPreferenceName(String preferenceName);

    /**
     * Gets the version of the preferences in this collection
     * @return version
     */
    String getVersion();

    /**
     * Sets the version of the preferences in this collection
     * @param version to set
     */
    void setVersion(String version);
}
