//
// -----------------------------------------------------------------------------------
// Source file: InstrumentorCollectorHome.java
//
// PACKAGE: com.cboe.presentation.collector;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.collector;

import com.cboe.interfaces.instrumentation.collector.InstrumentorCollector;

import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 *  Used for building the instrumentor collector that will subscribe to the
 *  infra event channel for collecting instrumentors.
 */
public abstract class InstrumentorCollectorHome
{
    private static InstrumentorCollector collector = null;

    /**
     *  Creates the appropriate InstrumentorCollector based on class name passed in and uses
     *  reflection to create it for later finds.
     *
     *@param  className  fully qualified class name to call
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
            GUILoggerHome.find().exception(InstrumentorCollectorHome.class.getName(), "Could not load class for: " +
                                                                                className, e);
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
            Class interfaceClass = InstrumentorCollector.class;

            Object newOBJ = theClass.newInstance();

            if (interfaceClass.isInstance(newOBJ))
            {
                collector = (InstrumentorCollector) newOBJ;
            }
            else
            {
                throw new IllegalArgumentException(InstrumentorCollectorHome.class.getName() +
                                                   ": Does not support interface " +
                                                   "com.cboe.interfaces.instrumentation.collector.InstrumentorCollector. " +
                                                   "className=" + theClass.getName());
            }
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(InstrumentorCollectorHome.class.getName(), "Could not load class for: " +
                                                                                theClass.getName(), e);
        }
    }

    /**
     *  Returns the instantiated GUILogger
     *
     *@return    Description of the Returned Value
     */
    public static InstrumentorCollector find()
    {
        if (collector != null)
        {
            return collector;
        }
        else
        {
            throw new IllegalStateException(InstrumentorCollectorHome.class.getName() + ": Create has not been called yet.");
        }
    }
}
