/*
 *  Copyright 2000
 *
 *  CBOE
 *  All rights reserved
 */
package com.cboe.presentation.qrm;

import java.lang.reflect.*;

import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 *  This class used for building UserTradingParametersAPIFactories for GUI and
 *  SAGUI.
 *
 *@author     Alex Brazhnichenko Creation date (3/8/00 1:50:51 PM)
 *@created    November 30, 2000
 *@version    03/08/2000
 */

public class GUIUserTradingParametersAPIHome
{
    private static UserTradingParametersAPIFactory tradingParametersAPIFactory;

    /**
     *  GUIUserTradingParametersAPIHome constructor comment.
     */
    public GUIUserTradingParametersAPIHome()
    {
        super();
    }

    /**
     *  Insert the method's description here. Creation date: (3/8/00 1:59:51 PM)
     *
     *@param  className  Description of Parameter
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
            GUILoggerHome.find().exception("com.cboe.presentation.common.create()","",e);
        }
    }

    /**
     *  Description of the Method
     *
     *@param  theClass  Description of Parameter
     */
    public static void create(Class theClass)
    {
        try
        {
            Class interfaceClass = com.cboe.presentation.qrm.UserTradingParametersAPIFactory.class;

            Object newOBJ = theClass.newInstance();

            if (interfaceClass.isInstance(newOBJ))
            {
                tradingParametersAPIFactory = (UserTradingParametersAPIFactory) newOBJ;
            }
            else
            {
                throw new IllegalArgumentException("GUIUserTradingParametersAPIHome: Does not support interface com.cboe.presentation.qrm.UserTradingParametersAPIFactory. className = " + theClass);
            }
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception("com.cboe.presentation.common.create()","",e);
        }
    }

    /**
     *  Insert the method's description here. Creation date: (3/8/00 1:56:22 PM)
     *
     *@return
     *      com.cboe.presentation.qrm.GUIUserTradingParametersAPI
     *@exception  java.lang.IllegalStateException  The exception description.
     */
    public static GUIUserTradingParametersAPI find() throws java.lang.IllegalStateException
    {
        if (tradingParametersAPIFactory != null)
        {
            return tradingParametersAPIFactory.find();
        }
        else
        {
            throw new IllegalStateException("GUIUserTradingParametersAPIHome: Create has not been called yet.");
        }
    }
}
