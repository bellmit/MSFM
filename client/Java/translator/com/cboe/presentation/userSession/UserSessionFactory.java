//
// -----------------------------------------------------------------------------------
// Source file: UserSessionFactory.java
//
// PACKAGE: com.cboe.presentation.userSession;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.userSession;

import java.util.*;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

/**
 *  Used for creating and storing a UserSession
 */
public class UserSessionFactory
{
    private static UserSession session = null;
    private static ArrayList listeners = new ArrayList();

    /**
     * Adds a listeners for UserSessionFactory events
     * @param listener to add
     */
    public static synchronized void addListener(UserSessionFactoryListener listener)
    {
        if(isInitialized())
        {
            listener.userSessionFactoryInit();
        }
        else
        {
            if(!listeners.contains(listener))
            {
                listeners.add(listener);
            }
        }
    }

    /**
     * Adds a listeners for UserSessionFactory events
     * @param listener to add
     */
    public static synchronized void removeListener(UserSessionFactoryListener listener)
    {
        listeners.remove(listener);
    }

    /**
     *  Creates the appropriate UserSession class impl based on the class String
     *  passed.
     *
     *@param  className  fully qualified class name that implements
     *      com.cboe.presentation.userSession.UserSession.
     */
    public static synchronized void create(String className)
    {
        try
        {
            Class theClass = Class.forName(className);
            create(theClass);
        }
        catch (Exception e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
    }
    /**
     * Provides a way to force this to use a specific instance of UserSession.
     * @param theSession
     */ 
    public static synchronized void create(UserSession theSession)
    {
        session = theSession;
    }
    /**
     *  Creates the appropriate UserSession class impl based on the class
     *  passed.
     *
     *@param  theClass  class that implements
     *      com.cboe.presentation.userSession.UserSession.
     */
    public static synchronized void create(Class theClass)
    {
        try
        {
            Class interfaceClass = com.cboe.presentation.userSession.UserSession.class;

            Object newOBJ = theClass.newInstance();

            if (interfaceClass.isInstance(newOBJ))
            {
                session = (UserSession) newOBJ;

                fireInitializedEvent();
            }
            else
            {
                throw new IllegalArgumentException("UserSessionFactory: Does not support interface com.cboe.presentation.userSession.UserSession. className = " + theClass);
            }
        }
        catch (Exception e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
    }

    /**
     *  Gets the implementation of a UserSession
     *
     *@return    Description of the Returned Value
     */
    public static synchronized UserSession findUserSession()
    {
        if (session != null)
        {
            return session;
        }
        else
        {
            throw new IllegalStateException("UserSessionFactory: Create has not been called yet.");
        }
    }

    /**
     * Determines if it has been initialized yet.
     */
    public static synchronized boolean isInitialized()
    {
        return session != null;
    }

    /**
     * Fires events when initialized
     */
    protected static synchronized void fireInitializedEvent()
    {
        for(Iterator i = listeners.iterator(); i.hasNext();)
        {
            ((UserSessionFactoryListener)i.next()).userSessionFactoryInit();
        }

        listeners.clear();
    }
}