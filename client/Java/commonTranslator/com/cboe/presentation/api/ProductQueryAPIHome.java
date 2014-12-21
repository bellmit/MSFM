//
// -----------------------------------------------------------------------------------
// Source file: ProductQueryAPIFactory.java
//
// PACKAGE: com.cboe.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import com.cboe.idl.cmi.ProductQuery;

import com.cboe.interfaces.presentation.api.ProductQueryAPI;
import com.cboe.interfaces.presentation.api.ProductQueryRedirectAPI;

import com.cboe.presentation.common.logging.GUILoggerHome;

public abstract class ProductQueryAPIHome
{
    private static ProductQueryRedirectAPI api = null;

    public static void create(String className, ProductQuery productQuery)
    {
        try
        {
            Class theClass = Class.forName(className);
            create(theClass, productQuery);
        }
        catch( Exception e )
        {
            GUILoggerHome.find().exception(ProductQueryAPIHome.class.getName(), "Could not load class for: " +
                                                                                className, e);
        }
    }

    public static void create(Class theClass, ProductQuery productQuery)
    {
        if(productQuery == null)
        {
            throw new IllegalArgumentException("productQuery may not be null.");
        }

        try
        {
            Class interfaceClass = ProductQueryRedirectAPI.class;

            Object newOBJ = theClass.newInstance();

            if( interfaceClass.isInstance(newOBJ) )
            {
                api = ( ProductQueryRedirectAPI ) newOBJ;
                api.setProductQuery(productQuery);
            }
            else
            {
                throw new IllegalArgumentException(ProductQueryAPIHome.class.getName() +
                                                   ": Does not support interface " +
                                                   "com.cboe.interfaces.presentation.api.ProductQueryRedirectAPI. " +
                                                   "className=" + theClass.getName());
            }
        }
        catch( Exception e )
        {
            GUILoggerHome.find().exception(ProductQueryAPIHome.class.getName(), "Could not load class for: " +
                                                                                theClass.getName(), e);
        }
    }

    public static ProductQueryAPI find()
    {
        if( api != null )
        {
            return api;
        }
        else
        {
            throw new IllegalStateException(ProductQueryAPIHome.class.getName() + ": Create has not been called yet.");
        }
    }
}
