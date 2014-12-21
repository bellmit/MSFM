//
// -----------------------------------------------------------------------------------
// Source file: SessionInfoManagerHome.java
//
// PACKAGE: com.cboe.internalPresentation.sessionManagement;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.sessionManagement;

import java.lang.reflect.*;

import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 *  Used for building SessionInfoManager impls
 */
public class SessionInfoManagerHome
{
    private static SessionInfoManager smsInfoMgr = null;
    private static final Class thisClass = SessionInfoManagerHome.class;

    private SessionInfoManagerHome()
    {}

    /**
     * Creates the appropriate SessionInfoManager based on class name passed in
     * @param className fully qualified class name that implements
     * <code>com.cboe.internalPresentation.sessionManagement.SessionInfoManager</code>
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
     * <code>com.cboe.internalPresentation.sessionManagement.SessionInfoManager</code>
     */
    public static void create(Class theClass)
    {
        try
        {
            Class interfaceClass = com.cboe.internalPresentation.sessionManagement.SessionInfoManager.class;

            Object newOBJ = theClass.newInstance();

            if(interfaceClass.isInstance(newOBJ))
            {
                smsInfoMgr = (SessionInfoManager)newOBJ;
            }
            else
            {
                throw new IllegalArgumentException(thisClass.getName() + ": Does not support interface com.cboe.internalPresentation.sessionManagement.SessionInfoManager. className=" + theClass.getName());
            }
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(thisClass.getName(), "Could not load class for: " + theClass.getName(), e);
        }
    }

    /**
     * Finds the implementation of SessionInfoManager
     * @return SessionInfoManager
     */
    public static SessionInfoManager find()
    {
        if(smsInfoMgr != null)
        {
            return smsInfoMgr;
        }
        else
        {
            throw new IllegalStateException(thisClass.getName() + ": Create has not been called yet.");
        }
    }
}