/*
 * Created on Jun 8, 2005
 * -----------------------------------------------------------------------------------
 * Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
 *-----------------------------------------------------------------------------------
 */
package com.cboe.presentation.api;

import com.cboe.interfaces.presentation.processes.OrbProcessCacheFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * @author I Nyoman Mahartayasa
 */
public class OrbProcessCacheFactoryHome
{
    private static OrbProcessCacheFactory instance;
    public static void create(Class theClass)
    {

        try
        {
            Class interfaceClass = OrbProcessCacheFactory.class;

            Object newOBJ = theClass.newInstance();

            if( interfaceClass.isInstance(newOBJ) )
            {
                instance= ( OrbProcessCacheFactory ) newOBJ;
            }
            else
            {
                throw new IllegalArgumentException(OrbProcessCacheFactoryHome.class.getName() +
                                                   ": Does not support interface " +
                                                   "com.cboe.interfaces.presentation.processes.OrbProcessCacheFactory " +
                                                   "className=" + theClass.getName());
            }
        }
        catch( Exception e )
        {
            GUILoggerHome.find().exception(OrbProcessCacheFactoryHome.class.getName(), "Could not load class for: " +
                                                                                theClass.getName(), e);
        }
    }

    public static OrbProcessCacheFactory find()
    {
        if( instance != null )
        {
            return instance;
        }
        else
        {
            throw new IllegalStateException(OrbProcessCacheFactoryHome.class.getName() + ": Create has not been called yet.");
        }        
    }
}
