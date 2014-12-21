package com.cboe.domain.routingProperty;

// -----------------------------------------------------------------------------------
// Source file: BasePropertyValidationFactoryHome
//
// PACKAGE: com.cboe.domain.routingProperty
// 
// Created: Mar 20, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.lang.reflect.Constructor;

import com.cboe.interfaces.domain.routingProperty.BasePropertyValidationFactory;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class BasePropertyValidationFactoryHome
{
    private static BasePropertyValidationFactory factory;

    private BasePropertyValidationFactoryHome() {}

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

    public static void create(Class theClass, Object[] constructorParms)
    {
        try
        {
            Class interfaceClass = com.cboe.interfaces.domain.routingProperty.BasePropertyValidationFactory.class;

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
                factory = (BasePropertyValidationFactory) newOBJ;
            }
            else
            {
                throw new IllegalArgumentException("BasePropertyValidationFactoryHome: Does not support interface " +
                                                   "com.cboe.interfaces.domain.routingProperty.BasePropertyValidationFactory className = " +
                                                   theClass);
            }
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
    }

    public static com.cboe.interfaces.domain.routingProperty.BasePropertyValidationFactory find()
    {
        if(factory == null)
        {
            create(NullBasePropertyValidationFactoryImpl.class, null);
        }

        if (factory != null)
        {
            return factory;
        }
        else
        {
            throw new IllegalStateException("BasePropertyValidationFactoryHome: Create has not been called yet.");
        }
    }
}
