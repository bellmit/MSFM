//
// -----------------------------------------------------------------------------------
// Source file: DefaultExceptionHandlerHome.java
//
// PACKAGE: com.cboe.presentation.common.exceptionHandling;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.exceptionHandling;

import java.lang.reflect.*;

import com.cboe.interfaces.presentation.common.exceptionHandling.DefaultExceptionHandler;

import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Used for obtaining references to an implementation of the DefaultExceptionHandler interface
 */
public class DefaultExceptionHandlerHome
{
    private static DefaultExceptionHandler exceptionHandler = null;
    private static final Class thisClass = DefaultExceptionHandlerHome.class;

    private DefaultExceptionHandlerHome()
    {}

    /**
     * Creates the appropriate DefaultExceptionHandler based on class name passed in
     * @param className fully qualified class name that implements
     * <code>com.cboe.interfaces.presentation.common.exceptionHandling.DefaultExceptionHandler</code>
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
     * Creates the appropriate DefaultExceptionHandler based on class passed in
     * @param theClass that implements
     * <code>com.cboe.interfaces.presentation.common.exceptionHandling.DefaultExceptionHandler</code>
     */
    public static void create(Class theClass)
    {
        try
        {
            Class interfaceClass = com.cboe.interfaces.presentation.common.exceptionHandling.DefaultExceptionHandler.class;

            Object newOBJ = theClass.newInstance();

            if(interfaceClass.isInstance(newOBJ))
            {
                exceptionHandler = (DefaultExceptionHandler)newOBJ;
            }
            else
            {
                throw new IllegalArgumentException(thisClass.getName() + ": Does not support interface com.cboe.interfaces.presentation.common.exceptionHandling.DefaultExceptionHandler. className=" + theClass.getName());
            }
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(thisClass.getName(), "Could not load class for: " + theClass.getName(), e);
        }
    }

    /**
     * Finds the implementation of DefaultExceptionHandler
     * @return DefaultExceptionHandler
     */
    public static DefaultExceptionHandler find()
    {
        if(exceptionHandler != null)
        {
            return exceptionHandler;
        }
        else
        {
            throw new IllegalStateException(thisClass.getName() + ": Create has not been called yet.");
        }
    }
}