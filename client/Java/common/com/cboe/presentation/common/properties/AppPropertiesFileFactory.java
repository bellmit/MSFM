//
// -----------------------------------------------------------------------------------
// Source file: AppPropertiesFileFactory.java
//
// PACKAGE: com.cboe.presentation.common.properties;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.properties;

/**
 * Used for creating and storing an AppPropertiesFile impl.
 */
public class AppPropertiesFileFactory
{
    public static final transient String DEFAULT_FILE_NAME = "SBT_GUI.properties";

    public static PropertiesFile file = null;

    public static boolean loadFailed = false;

    /**
     * Creates the appropriate AppPropertiesFile class impl based on the class
     * String passed.
     * @param className fully qualified class name that implements
     * com.cboe.presentation.properties.PropertiesFile.
     */
    public static synchronized void create(String className)
    {
        try
        {
            Class theClass = Class.forName(className);
            create(theClass);
        }
        catch(Exception e)
        {
            loadFailed = true;

            //GUILogger does not exist yet.
            System.err.println("Exception occurred initializing AppPropertiesFile");
            System.err.println(e);
            System.err.println();
        }
    }

    /**
     * Creates the appropriate AppPropertiesFile class impl based on the class
     * passed.
     * @param theClass that implements com.cboe.presentation.properties.PropertiesFile.
     */
    public static synchronized void create(Class theClass)
    {
        try
        {
            Class interfaceClass = com.cboe.presentation.common.properties.PropertiesFile.class;

            Object newOBJ = theClass.newInstance();

            if (interfaceClass.isInstance(newOBJ))
            {
                file = (PropertiesFile) newOBJ;
                loadFailed = false;
            }
            else
            {
                throw new IllegalArgumentException("AppPropertiesFileFactory: Does not support interface com.cboe.presentation.properties.PropertiesFile. className = " + theClass);
            }
        }
        catch(Exception e)
        {
            loadFailed = true;

            //GUILogger does not exist yet.
            System.err.println("Exception occurred initializing AppPropertiesFile");
            e.printStackTrace(System.err);
            System.err.println();
        }
    }

    /**
     * Determines if app properties are available to retrieve.
     * @return True if they are available, false otherwise.
     */
    public static synchronized boolean isAppPropertiesAvailable()
    {
        if(file != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Gets the implementation of a PropertiesFile for App Properties
     */
    public static synchronized PropertiesFile find()
    {
        if(file != null)
        {
            return file;
        }
        else
        {
            throw new IllegalStateException("AppPropertiesFileFactory: Create has not been called yet.");
        }
    }
}
