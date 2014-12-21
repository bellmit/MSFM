// -----------------------------------------------------------------------------------
// Source file: com/cboe/presentation/common/cboePreferences/PreferenceManagerHome.java
//
// PACKAGE: com.cboe.presentation.preferences;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.preferences;

import com.cboe.interfaces.presentation.preferences.PreferenceManager;
import com.cboe.interfaces.presentation.preferences.PreferenceManagerFactory;
import com.cboe.interfaces.presentation.preferences.BusinessPreferenceManager;

import com.cboe.presentation.common.logging.GUILoggerHome;

public class PreferenceManagerHome
{
    private static PreferenceManagerFactory preferenceManagerFactory = null;

    private PreferenceManagerHome() {}

    /**
     *  Creates the appropriate PreferenceManagerFactory based on class name passed in and
     *  uses reflection to create it for later finds.
     *
     *@param  className  fully qualified class name to call find on to obtain a PreferenceManagerFactory
     */
    @SuppressWarnings({"RawUseOfParameterizedType", "CatchGenericClass"})
    public static void create(String className)
    {
        try
        {
            Class theClass = Class.forName(className);
            create(theClass);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception("PreferenceManagerHome.create()", "", e);
        }
    }

    /**
     *  Creates the appropriate PreferenceManagerFactory based on class passed in and uses
     *  reflection to create it for later finds.
     *
     *@param  theClass  class to call find on to obtain a PreferenceManagerFactory.
     */
    @SuppressWarnings({"RawUseOfParameterizedType", "CatchGenericClass"})
    public static void create(Class theClass)
    {
        try
        {
            Class interfaceClass = PreferenceManagerFactory.class;

            Object newOBJ = theClass.newInstance();

            if (interfaceClass.isInstance(newOBJ))
            {
                preferenceManagerFactory = (PreferenceManagerFactory) newOBJ;
            }
            else
            {
                throw new IllegalArgumentException("PreferenceManagerHome: Does not support interface PreferenceManagerFactory.class Name = " + theClass);
            }
        }
        catch(InstantiationException e)
        {
            GUILoggerHome.find().exception("PreferenceManagerHome.create()", e);
        }
        catch(IllegalAccessException e)
        {
            GUILoggerHome.find().exception("PreferenceManagerHome.create()", e);
        }
    }

    public static BusinessPreferenceManager findBusinessPreferenceManager()
    {
        if (preferenceManagerFactory != null)
        {
            return preferenceManagerFactory.findBusinessPreferenceManager();
        }
        else
        {
            throw new IllegalStateException("PreferenceManagerHome: Create has not been called yet.");
        }
    }

    public static PreferenceManager findGUIPreferenceManager()
    {
        if (preferenceManagerFactory != null)
        {
            return preferenceManagerFactory.findGUIPreferenceManager();
        }
        else
        {
            throw new IllegalStateException("PreferenceManagerHome: Create has not been called yet.");
        }
    }

}

