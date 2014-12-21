//
// ------------------------------------------------------------------------
// Source file: IntermarketAPIHome.java
//
// PACKAGE: com.cboe.presentation.api
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------

package com.cboe.presentation.api;

import com.cboe.interfaces.intermarketPresentation.api.*;
import com.cboe.idl.cmi.UserSessionManager;
import com.cboe.presentation.common.logging.GUILoggerHome;

public class IntermarketAPIHome
{
    private static IntermarketAPIHomeFactory intermarketAPIHomeFactory;

    /**
     *  Creates the appropriate API Factory based on class name passed in and
     *  uses reflection to create it for later finds.
     *
     *@param  className  fully qualified class name to call find on to obtain an IntermarketAPIFactory.
     *
     */
    public static void create( String className )
    {
        try
        {
            Class theClass = Class.forName( className );
            create( theClass );
        }
        catch ( Exception e )
        {
            GUILoggerHome.find().exception( "com.cboe.intermarketPresentation.api.IntermarketAPIHome.create()", "", e );
        }
    }

    /**
     *  Creates the appropriate Intermarket API Factory based on class passed in and uses
     *  reflection to create it for later finds.
     *
     *@param  theClass  class to call find on to obtain an IntermarketAPIFactory.
     */

    public static void create( Class theClass )
    {
        try
        {
            Class interfaceClass = com.cboe.interfaces.intermarketPresentation.api.IntermarketAPIHomeFactory.class;

            Object newOBJ = theClass.newInstance();

            if( interfaceClass.isInstance( newOBJ ) )
            {
                intermarketAPIHomeFactory = (IntermarketAPIHomeFactory) newOBJ;
            }
            else
            {
                throw new IllegalArgumentException( "InternmarketAPIHome: Factory must extend com.cboe.intermarketPresentation.api.IntermarketAPIHomeFactory.class Name = " + theClass );
            }
        }
        catch ( Exception e )
        {
            GUILoggerHome.find().exception( "com.cboe.intermarketPresentation.api.IntermarketAPIHome.create()", "", e );
        }
    }

    protected static IntermarketAPIHomeFactory getFactory()
    {
        if( intermarketAPIHomeFactory == null )
        {
            throw new IllegalStateException( "IntermarketAPIHomeFactory has not been created" );
        }
        else
        {
            if( isInitialized() == false )
            {
                throw new IllegalStateException( "IntermarketAPIHomeFactory has not been initialized" );
            }
        }
        return intermarketAPIHomeFactory;
    }

    public static boolean isInitialized()
    {
        if( intermarketAPIHomeFactory != null && intermarketAPIHomeFactory.isInitialized() )
        {
            return true;
        }
        return false;
    }

    public static void setUserSessionManager(UserSessionManager userSessionManager)
    {
        if( isInitialized() == false ) // do initialization only once
        {
            // DO NOT USE the getFactory method in this block, as it depends on the initialization being completed.
            // The factory initalize method will register to receive the login event and then initialize the factory,
            // so the IntermarketAPIHome intialization is not done until the factory initialization is done.
            intermarketAPIHomeFactory.setUserSessionManager(userSessionManager);
        }
    }

    public static NBBOAgentAPI findNBBOAgentAPI()
    {
        return getFactory().findNBBOAgentAPI();
    }

    public static IntermarketQueryAPI findIntermarketQueryAPI()
    {
        return getFactory().findIntermarketQueryAPI();
    }

    public static IntermarketAPI findIntermarketAPI()
    {
        return getFactory().findIntermarketAPI();
    }

}
