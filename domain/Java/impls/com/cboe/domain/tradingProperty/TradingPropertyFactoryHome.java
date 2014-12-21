//
// -----------------------------------------------------------------------------------
// Source file: TradingPropertyFactoryHome.java
//
// PACKAGE: com.cboe.domain.tradingProperty;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import java.lang.reflect.Constructor;

import com.cboe.interfaces.domain.tradingProperty.TradingPropertyFactory;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Provides a home for obtaining implementations of TradingPropertyFactory. Provides abstraction to implementations
 * for different platform layers.
 */
public class TradingPropertyFactoryHome
{
    private static TradingPropertyFactory factory;

    private TradingPropertyFactoryHome(){}

    /**
     * Creates the appropriate TradingPropertyFactory based on class name passed in, using the constructor with
     * and to initialize, with the specified initialization parameters in constructorParms.
     * @param className fully qualified class name that implements TradingPropertyFactory
     * @param constructorParms array of objects to be passed as arguments to the constructor call for the
     * passed className. If the number of formal parameters required by the underlying constructor is 0,
     * the supplied constructorParms array may be of length 0 or null. Both primitive and reference parameters
     * are subject to method invocation conversions as necessary.
     * values of primitive types are wrapped in a wrapper object of the appropriate type (e.g. a float  in a Float)
     */
    public static void create(String className, Object[] constructorParms)
    {
        try
        {
            Class theClass = Class.forName(className);
            create(theClass, constructorParms);
        }
        catch(Exception e)
        {
            Log.exception(e);
        }
    }

    /**
     * Creates the appropriate TradingPropertyFactory based on the class passed in, using the constructor with
     * and to initialize, with the specified initialization parameters in constructorParms.
     * @param theClass class that implements TradingPropertyFactory
     * @param constructorParms array of objects to be passed as arguments to the constructor call for the
     * passed className. If the number of formal parameters required by the underlying constructor is 0,
     * the supplied constructorParms array may be of length 0 or null. Both primitive and reference parameters
     * are subject to method invocation conversions as necessary.
     * values of primitive types are wrapped in a wrapper object of the appropriate type (e.g. a float  in a Float)
     */
    public static void create(Class theClass, Object[] constructorParms)
    {
        try
        {
            Class interfaceClass = com.cboe.interfaces.domain.tradingProperty.TradingPropertyFactory.class;

            Object newOBJ;

            if(constructorParms == null || constructorParms.length == 0)
            {
                newOBJ = theClass.newInstance();
            }
            else
            {
                Class[] parameterTypes = new Class[constructorParms.length];
                for(int i = 0; i < constructorParms.length; i++)
                {
                    parameterTypes[i] = constructorParms[i].getClass();
                }
                Constructor constructor = theClass.getConstructor(parameterTypes);
                newOBJ = constructor.newInstance(constructorParms);
            }

            if(interfaceClass.isInstance(newOBJ))
            {
                factory = (TradingPropertyFactory) newOBJ;
            }
            else
            {
                throw new IllegalArgumentException("TradingPropertyFactoryHome: Does not support interface " +
                                                   "com.cboe.interfaces.domain.tradingProperty.TradingPropertyFactory className = " +
                                                   theClass);
            }
        }
        catch(Exception e)
        {
            System.err.println(e);
        }
    }

    /**
     * Gets the specific impl of TradingPropertyFactory
     */
    public static TradingPropertyFactory find()
    {
        if(factory != null)
        {
            return factory;
        }
        else
        {
            throw new IllegalStateException("TradingPropertyFactoryHome: Create has not been called yet.");
        }
    }
}


