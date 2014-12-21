//
// -----------------------------------------------------------------------------------
// Source file: CBOEPreference.java
//
// PACKAGE: com.cboe.presentation.preferences;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.preferences;

import com.cboe.idl.cmiUser.PreferenceStruct;

/**
 * Defines the contract of a Preference
 */
public interface CBOEPreference
{
    /**
     * Gets the name of the preference
     * @return name
     */
    public String getName();

    /**
     * Gets the value of the preference
     * @return value
     */
    public String getValue();

    /**
     * Determines if this preference is valid
     * @return true if valid, false if invalid
     */
    public boolean isValid();

    /**
     * Sets the name of the preference
     * @param name to set
     */
    public void setName(String name);

    /**
     * Sets the value of the preference
     * @param value to set
     */
    public void setValue(String value);

    /**
     * Returns this interface as a representation of a CMI struct
     */
    public PreferenceStruct toStruct();

    /**
     * Gets the version of this preference
     * @return version
     */
    public String getVersion();
    /**
     * Hashcode to identify a unique key based on the key name.
     * @return the hashCode of the key name.
     */
    public int hashCode();
    /**
     * Check if 2 keys are equals based on their name.
     * @param obj
     * @return true if equals.
     */
    public boolean equals(Object obj);
    
}