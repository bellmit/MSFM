//
// -----------------------------------------------------------------------------------
// Source file: FIXAPIHome.java
//
// PACKAGE: com.cboe.presentation.fix.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.fix.api;

import com.cboe.interfaces.presentation.api.*;

import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 *  Used for building FIX APIFactories to locate various Translator API services
 *  among client apps.
 */
public class FIXAPIHome
{
    private static FIXAPIFactory apiFactory = null;

    /**
     *  Creates the appropriate API Factory based on class name passed in and
     *  uses reflection to create it for later finds.
     *
     *@param  className  fully qualified class name to call find on to obtain an
     *      APIFactory.
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
            GUILoggerHome.find().exception("FIXAPIHome.create()","",e);
        }
    }

    /**
     *  Creates the appropriate API Factory based on class passed in and uses
     *  reflection to create it for later finds.
     *
     *@param  theClass  class to call find on to obtain an APIFactory.
     */
    public static void create(Class theClass)
    {
        try
        {

            Class interfaceClass = com.cboe.interfaces.presentation.api.FIXAPIFactory.class;

            Object newOBJ = theClass.newInstance();

            if (interfaceClass.isInstance(newOBJ))
            {
                apiFactory = (FIXAPIFactory) newOBJ;
            }
            else
            {
                throw new IllegalArgumentException("FIXAPIHome: Does not support interface com.cboe.interfaces.presentation.api.FIXAPIFactory.class Name = " + theClass);
            }
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception("FIXAPIHome.create()","",e);
        }
    }

    public static FIXMarketMakerAPI findFIXMarketMakerAPI()
    {
        if (apiFactory != null)
        {
            return apiFactory.findFIXMarketMakerAPI();
        }
        else
        {
            throw new IllegalStateException("FIXAPIHome: Create has not been called yet.");
        }
    }

    /**
     *  Initiates clean up on apiFactory.
     */
    public static void cleanUp()
    {
        if (apiFactory != null)
        {
            apiFactory.cleanUp();
        }
        else
        {
            throw new IllegalStateException("FIXAPIHome: Create has not been called yet.");
        }
    }
}
