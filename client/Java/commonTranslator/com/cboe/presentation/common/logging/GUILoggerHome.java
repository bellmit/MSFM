//
// -----------------------------------------------------------------------------------
// Source file: GUILoggerHome.java
//
// PACKAGE: com.cboe.presentation.common.logging;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.logging;

import com.cboe.interfaces.presentation.common.logging.IGUILogger;

/**
 *  Used for building IGUILogger's to provide logging among client apps.
 *
 *@author     Troy Wehrle
 *@created    November 30, 2000
 *@version    12/21/1999
 */
public class GUILoggerHome
{
    private static IGUILogger logger = null;

    /**
     *  Creates the appropriate GUILogger based on class name passed in and uses
     *  reflection to create it for later finds.
     *
     *@param  className  fully qualified class name to call find on to obtain a
     *      IGUILogger.
     */
    public static void create(String className)
    {
        try
        {
            Class theClass = Class.forName(className);
            create(theClass);
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
    }

    /**
     *  Creates the appropriate GUILogger based on class passed in and uses
     *  reflection to create it for later finds.
     *
     *@param  theClass  class to call find on to obtain a IGUILogger.
     */
    public static void create(Class theClass)
    {
        try
        {
            Class interfaceClass = com.cboe.interfaces.presentation.common.logging.IGUILogger.class;

            Object newOBJ = theClass.newInstance();

            if (interfaceClass.isInstance(newOBJ))
            {
                logger = (IGUILogger) newOBJ;
            }
            else
            {
                throw new IllegalArgumentException("GUILoggerHome: Does not support interface com.cboe.presentation.common.logging.IGUILogger. className = " + theClass);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
    }

    /**
     *  Returns the instantiated GUILogger
     *
     *@return    Description of the Returned Value
     */
    public static IGUILogger find()
    {
        if (logger != null)
        {
            return logger;
        }
        else
        {
            throw new IllegalStateException("GUILoggerHome: Create has not been called yet.");
        }
    }
}
