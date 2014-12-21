//
// -----------------------------------------------------------------------------------
// Source file: PropertyServiceFacadeHome.java
//
// PACKAGE: com.cboe.domain.property;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.property;

import com.cboe.interfaces.domain.property.PropertyServiceFacade;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class PropertyServiceFacadeHome
{
    private static boolean isCachingPerformed = false;
    private static PropertyServiceFacade facade;

    /**
     * Creates the appropriate PropertyServiceFacade based on class name passed in. Calls create(String, boolean),
     * passing false as the boolean.
     * @param className fully qualified class name that implements PropertyServiceFacade
     */
    public static void create(String className)
    {
        create(className, false);
    }

    /**
     * Creates the appropriate PropertyServiceFacade based on class passed in. Calls create(Class, boolean, int, int),
     * passing false as the boolean, and zeros for the two ints.
     * @param theClass class that implements PropertyServiceFacade
     */
    public static void create(Class theClass)
    {
        create(theClass, false, 0, 0);
    }

    /**
     * Creates the appropriate PropertyServiceFacade based on class name passed in. Calls create(Class, boolean, int, int),
     * passing PropertyServiceFacadeCacheProxy.DEFAULT_CACHE_SIZE for the two ints.
     * @param className fully qualified class name that implements PropertyServiceFacade
     * @param performCaching True to provide an automatic caching proxy on your passed className, false for direct
     * calls only to your className.
     */
    public static void create(String className, boolean performCaching)
    {
        try
        {
            Class theClass = Class.forName(className);
            create(theClass, performCaching, PropertyServiceFacadeCacheProxy.DEFAULT_CACHE_SIZE,
                   PropertyServiceFacadeCacheProxy.DEFAULT_CACHE_SIZE);
        }
        catch(Exception e)
        {
            Log.exception(e);
        }
    }

    /**
     * Creates the appropriate PropertyServiceFacade based on class passed in.
     * @param theClass class that implements PropertyServiceFacade
     * @param performCaching True to provide an automatic caching proxy on your passed className, false for direct calls
     * only to your className.
     */
    public static void create(Class theClass, boolean performCaching)
    {
        create(theClass, performCaching, PropertyServiceFacadeCacheProxy.DEFAULT_CACHE_SIZE,
               PropertyServiceFacadeCacheProxy.DEFAULT_CACHE_SIZE);
    }

    /**
     * Creates the appropriate PropertyServiceFacade based on class name passed in.
     * @param className fully qualified class name that implements PropertyServiceFacade
     * @param performCaching True to provide an automatic caching proxy on your passed className, false for direct calls
     * only to your className.
     * @param initialCacheSize initial size of the cache that holds the objects.
     * @param initialKeyCacheSize initial size of the cache that holds the keys of the to the cached objects.
     */
    public static void create(String className, boolean performCaching, int initialCacheSize, int initialKeyCacheSize)
    {
        try
        {
            Class theClass = Class.forName(className);
            create(theClass, performCaching, initialCacheSize, initialKeyCacheSize);
        }
        catch(Exception e)
        {
            Log.exception(e);
        }
    }

    /**
     * Creates the appropriate PropertyServiceFacade based on class passed in.
     * @param theClass class that implements PropertyServiceFacade
     * @param performCaching True to provide an automatic caching proxy on your passed theClass, false for direct
     * calls only to your theClass.
     * @param initialCacheSize initial size of the cache that holds the objects. If performCaching is true, these values
     * will be passed to the constructor of the automatic caching proxy.
     * @param initialKeyCacheSize initial size of the cache that holds the keys of the to the cached objects.
     * If performCaching is true, these values will be passed to the constructor of the automatic caching proxy.
     */
    public static void create(Class theClass, boolean performCaching, int initialCacheSize, int initialKeyCacheSize)
    {
        try
        {
            Class interfaceClass = com.cboe.interfaces.domain.property.PropertyServiceFacade.class;

            Object newOBJ = theClass.newInstance();

            if(interfaceClass.isInstance(newOBJ))
            {
                isCachingPerformed = performCaching;
                if(performCaching)
                {
                    facade = new PropertyServiceFacadeCacheProxy((PropertyServiceFacade) newOBJ,
                                                                 initialCacheSize, initialKeyCacheSize);
                }
                else
                {
                    facade = (PropertyServiceFacade) newOBJ;
                }
            }
            else
            {
                throw new IllegalArgumentException("PropertyServiceFacadeHome: Does not support interface " +
                                                   "com.cboe.interfaces.domain.property.PropertyServiceFacade className = " +
                                                   theClass);
            }
        }
        catch(Exception e)
        {
            System.err.println(e);
        }
    }

    /**
     * Determines whether automatic caching through a proxy is performed.
     * @return True is caching is performed, false otherwise.
     */
    public boolean isCachingPerformed()
    {
        return isCachingPerformed;
    }

    /**
     * Gets the specific impl of PropertyServiceFacade
     */
    public static PropertyServiceFacade find()
    {
        if(facade != null)
        {
            return facade;
        }
        else
        {
            throw new IllegalStateException("PropertyServiceFacadeHome: Create has not been called yet.");
        }
    }
}


