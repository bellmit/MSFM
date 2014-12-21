package com.cboe.domain.routingProperty;
// -----------------------------------------------------------------------------------
// Source file: AffiliatedFirmPropertyFactoryHome
//
// PACKAGE: com.cboe.domain.routingProperty
// 
// Created: 
// -----------------------------------------------------------------------------------
// Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.lang.reflect.Constructor;

import com.cboe.interfaces.domain.routingProperty.BasePropertyFactory;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class AffiliatedFirmPropertyFactoryHome
{
    private static BasePropertyFactory factory;

    private AffiliatedFirmPropertyFactoryHome() {}

    /**
     * Creates the appropriate BasePropertyFactory based on class name passed in, using the constructor with and to
     * initialize, with the specified initialization parameters in constructorParms.
     * @param className fully qualified class name that implements BasePropertyFactory
     * @param constructorParms array of objects to be passed as arguments to the constructor call for the passed
     * className. If the number of formal parameters required by the underlying constructor is 0, the supplied
     * constructorParms array may be of length 0 or null. Both primitive and reference parameters are subject to method
     * invocation conversions as necessary. values of primitive types are wrapped in a wrapper object of the appropriate
     * type (e.g. a float  in a Float)
     */
    public static void create(String className, Object[] constructorParms)
    {
        try
        {
            Class theClass = Class.forName(className);
            create(theClass, constructorParms);
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
    }

    /**
     * Creates the appropriate BasePropertyFactory based on the class passed in, using the constructor with and to
     * initialize, with the specified initialization parameters in constructorParms.
     * @param theClass class that implements BasePropertyFactory
     * @param constructorParms array of objects to be passed as arguments to the constructor call for the passed
     * className. If the number of formal parameters required by the underlying constructor is 0, the supplied
     * constructorParms array may be of length 0 or null. Both primitive and reference parameters are subject to method
     * invocation conversions as necessary. values of primitive types are wrapped in a wrapper object of the appropriate
     * type (e.g. a float  in a Float)
     */
    public static void create(Class theClass, Object[] constructorParms)
    {
        try
        {
            Class interfaceClass = com.cboe.interfaces.domain.routingProperty.BasePropertyFactory.class;

            Object newOBJ;

            if (constructorParms == null || constructorParms.length == 0)
            {
                newOBJ = theClass.newInstance();
            }
            else
            {
                Class[] parameterTypes = new Class[constructorParms.length];
                for (int i = 0; i < constructorParms.length; i++)
                {
                    parameterTypes[i] = constructorParms[i].getClass();
                }
                Constructor constructor = theClass.getConstructor(parameterTypes);
                newOBJ = constructor.newInstance(constructorParms);
            }

            if (interfaceClass.isInstance(newOBJ))
            {
                factory = (BasePropertyFactory) newOBJ;
            }
            else
            {
                throw new IllegalArgumentException("AffiliatedFirmPropertyFactoryHome: Does not support interface " +
                                                   "com.cboe.interfaces.domain.routingProperty.BasePropertyFactory className = " +
                                                   theClass);
            }
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
    }

    /**
     * Gets the specific impl of BasePropertyFactory
     */
    public static com.cboe.interfaces.domain.routingProperty.BasePropertyFactory find()
    {
        if (factory != null)
        {
            return factory;
        }
        else
        {
            create("com.cboe.domain.routingProperty.AffiliatedFirmPropertyFactoryImpl",null);
            return factory;
            
            //throw new IllegalStateException("FirmPropertyFactoryHome: Create has not been called yet.");
        }
    }
}
