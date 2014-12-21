//
// -----------------------------------------------------------------------------------
// Source file: ProcessWatcherManagerHome
//
// PACKAGE: com.cboe.presentation.processWatcher
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.processWatcher;

import com.cboe.presentation.common.logging.GUILoggerHome;

public class ProcessWatcherManagerHome
{
    private static ProcessWatcherManager pwManager = null;
    private static final Class thisClass = ProcessWatcherManagerHome.class;

    /**
     * Creates the appropriate ProcessWatcherManager based on class name passed in
     * @param className fully qualified class name that implements
     * <code>com.cboe.presentation.processWatcher.ProcessWatcherManager</code>
     */
    public static void create(String className)
    {
        try
        {
            Class theClass = Class.forName(className);
            create(theClass);
        }
        catch(Exception e)
        {
           GUILoggerHome.find().exception(thisClass.getName(), "Could not load class for: " + className, e);
        }
    }

    /**
     * Creates the appropriate SessionInfoManager based on class passed in
     * @param theClass that implements
     * <code>com.cboe.presentation.processWatcher.ProcessWatcherManager</code>
     */
    public static void create(Class theClass)
    {
        try
        {
            Class interfaceClass = com.cboe.presentation.processWatcher.ProcessWatcherManager.class;

            Object newOBJ = theClass.newInstance();

            if(interfaceClass.isInstance(newOBJ))
            {
                pwManager = (ProcessWatcherManager)newOBJ;
            }
            else
            {
                throw new IllegalArgumentException(thisClass.getName() + ": Does not support interface com.cboe.presentation.processWatcher.ProcessWatcherManager. className=" + theClass.getName());
            }
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(thisClass.getName(), "Could not load class for: " + theClass.getName(), e);
        }
    }

    /**
     * Finds the implementation of ProcessWatcherManager
     * @return ProcessWatcherManager
     */
    public static ProcessWatcherManager find()
    {
        if(pwManager != null)
        {
            return pwManager;
        }
        else
        {
            throw new IllegalStateException(thisClass.getName() + ": Create has not been called yet.");
        }
    }

} // -- end of class ProcessWatcherManagerHome
