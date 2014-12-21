//
// -----------------------------------------------------------------------------------
// Source file: BusinessPreferenceManager.java
//
// PACKAGE: com.cboe.interfaces.presentation.preferences
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.preferences;

import java.beans.PropertyChangeListener;

public interface BusinessPreferenceManager extends PreferenceManager
{
    double getDoublePreference(String name) throws PreferenceNotFoundException;
    boolean getBooleanPreference(String name) throws PreferenceNotFoundException;
    int getIntPreference(String name) throws PreferenceNotFoundException;

    void setPreference(String name, double value);
    void setPreference(String name, int value);
    void setPreference(String name, boolean value);

    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);
    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
    void firePropertyChange(String propertyName, Object oldValue, Object newValue);
}