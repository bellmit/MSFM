//
// -----------------------------------------------------------------------------------
// Source file: PARBrokerProfileFactoryHome.java
//
// PACKAGE: com.cboe.internalPresentation.user
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2011 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.user;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

public class PARBrokerProfileFactoryHome
{
    private static PARBrokerProfileFactory factory;

    public static void create(Class theClass)
    {
        try
        {
            Class interfaceClass = PARBrokerProfileFactory.class;

            Object newOBJ = theClass.newInstance();

            if (interfaceClass.isInstance(newOBJ))
            {
                factory = (PARBrokerProfileFactory) newOBJ;
            }
            else
            {
                throw new IllegalArgumentException("PARBrokerProfileFactoryHome: class '" + theClass +
                        "' does not support interface '" + interfaceClass + "'");
            }
        }
        catch (Exception e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
    }

    public static PARBrokerProfileFactory find()
    {
        if (factory != null)
        {
            return factory;
        }
        else
        {
            throw new IllegalStateException("PARBrokerProfileFactoryHome: Create has not been called yet.");
        }
    }
}
