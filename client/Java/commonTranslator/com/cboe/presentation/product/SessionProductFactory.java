//
// -----------------------------------------------------------------------------------
// Source file: SessionProductFactory.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import java.util.*;

import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionStrategy;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.presentation.common.formatters.Utility;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

/**
 *  Factory for creating instances of SessionProduct
 */
public class SessionProductFactory
{
    private static SessionProduct allSelectedSessionProduct = null;
    private static SessionProduct defaultSessionProduct = null;
    private static SessionStrategy defaultSessionStrategy = null;
    private static Map sessionAllSelected = new HashMap(5);
    private static Map sessionDefaultSelected = new HashMap(5);
    private static Map<SessionProductClass, SessionProduct> sessionAllSelectedByClass = new HashMap<SessionProductClass, SessionProduct>(50);

    /**
     *  Hidden to prevent instantiation of this factory since it is a static
     *  factory.
     */
    private SessionProductFactory()
    {}

    public static SessionProduct createInactiveSessionProduct(String sessionName, String inactiveSessionName, ProductStruct productStruct)
    {
        if( productStruct.productKeys.productType == ProductTypes.FUTURE )
        {
            return new InactiveSessionFutureProductImpl(sessionName, inactiveSessionName, productStruct);
        }
        else
        {
            return new InactiveSessionProductImpl(sessionName, inactiveSessionName, productStruct);
        }
    }

    public static SessionStrategy createInactiveSessionStrategy(String sessionName, String inactiveSessionName, StrategyStruct strategyStruct)
    {
        return new InactiveSessionStrategyImpl(sessionName, inactiveSessionName, strategyStruct);
    }

    /**
     * Creates an instance of a SessionProduct from a SessionProductStruct.
     * @param sessionProductStruct to wrap in instance of SessionProduct
     * @return SessionProduct to represent the SessionProductStruct
     */
    public static SessionProduct create(SessionProductStruct sessionProductStruct)
    {
        if (sessionProductStruct == null)
        {
            throw new IllegalArgumentException();
        }
        SessionProduct sessionProduct = null;
        if (sessionProductStruct.productStruct.productKeys.productType == ProductTypes.STRATEGY)
        {
            SessionStrategy sessionStrategy = (SessionStrategy)Utility.getProductByKeyForSession(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.productKey);
            SessionStrategyStruct strategyStruct = new SessionStrategyStruct();
            strategyStruct.sessionProductStruct = sessionProductStruct;
            strategyStruct.sessionStrategyLegs = sessionStrategy.getSessionStrategyLegStructs();
            strategyStruct.strategyType = sessionStrategy.getStrategyType();
            sessionProduct = create(strategyStruct);
        }
        else if (sessionProductStruct.productStruct.productKeys.productType == ProductTypes.FUTURE)
        {
            sessionProduct = new SessionFutureProductImpl(sessionProductStruct);
        }
        else
        {
            sessionProduct = new SessionProductImpl(sessionProductStruct);
        }
        return sessionProduct;
    }

    /**
     * Creates an instance of a SessionStrategy from a SessionProductStruct and SessionStrategyStruct
     * @param sessionProductStruct to wrap in instance of SessionStrategy
     * @return SessionStrategy to represent the SessionStrategyStruct
     */

    public static SessionStrategy create(SessionStrategyStruct sessionStrategyStruct)
    {
        if (sessionStrategyStruct == null )
        {
            throw new IllegalArgumentException();
        }
        SessionStrategy sessionStrategy = new SessionStrategyImpl(sessionStrategyStruct);
        return sessionStrategy;
    }

    /**
     * Returns the SessionProduct representing "All Selected"
     * @return SessionProduct to represent all SessionProduct's for a set
     * context.
     */
    public static synchronized SessionProduct createAllSelected()
    {
        if(allSelectedSessionProduct == null)
        {
            allSelectedSessionProduct = new SessionProductAllSelectedImpl();
            sessionAllSelected.put(allSelectedSessionProduct.getTradingSessionName(), allSelectedSessionProduct);
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug("SessionProductFactory.createAllSelected() caching SessionProductAllSelectedImpl for session '"+
                        allSelectedSessionProduct.getTradingSessionName()+"'", GUILoggerBusinessProperty.PRODUCT_QUERY);
            }
        }

        return allSelectedSessionProduct;
    }

    /**
     * Returns the SessionProduct representing "Default"
     * @return SessionProduct to represent the Default SessionProduct for a set
     * context.
     */
    public static synchronized SessionProduct createDefault()
    {
        if(defaultSessionProduct == null)
        {
            defaultSessionProduct = new SessionProductDefaultImpl();
            sessionDefaultSelected.put(defaultSessionProduct.getTradingSessionName(), defaultSessionProduct);
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug("SessionProductFactory.createDefault() caching SessionProductDefaultImpl for session '" +
                        defaultSessionProduct.getTradingSessionName() + "'", GUILoggerBusinessProperty.PRODUCT_QUERY);
            }
        }

        return defaultSessionProduct;
    }

    /**
     * Returns the SessionProduct representing "All Selected"
     * @return SessionProduct to represent all SessionProduct's for a set
     * context.
     */
    public static synchronized SessionProduct createAllSelected(String sessionName)
    {
        SessionProduct product = ( SessionProduct ) sessionAllSelected.get(sessionName);
        if( product == null )
        {
            product = new SessionProductAllSelectedImpl(sessionName);
            sessionAllSelected.put(sessionName, product);
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug("SessionProductFactory.createAllSelected() caching SessionProductAllSelectedImpl for session '" + sessionName + "'",
                        GUILoggerBusinessProperty.PRODUCT_QUERY);
            }
        }

        return product;
    }

    public static synchronized SessionProduct createAllSelected(SessionProductClass productClass)
    {
        SessionProduct sp = sessionAllSelectedByClass.get(productClass);
        if(sp == null)
        {
            sp = new SessionProductAllSelectedForClassImpl(productClass);
            sessionAllSelectedByClass.put(productClass, sp);
        }
        return sp;
    }

    /**
     * Returns the SessionProduct representing "Default"
     * @return SessionProduct to represent the Default SessionProduct for a set
     * context.
     */
    public static synchronized SessionProduct createDefault(String sessionName)
    {
        SessionProduct product = ( SessionProduct ) sessionDefaultSelected.get(sessionName);
        if( product == null )
        {
            product = new SessionProductDefaultImpl(sessionName);
            sessionDefaultSelected.put(sessionName, product);
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug("SessionProductFactory.createDefault() caching SessionProductDefaultImpl for session '" + sessionName + "'",
                        GUILoggerBusinessProperty.PRODUCT_QUERY);
            }
        }

        return product;
    }

    /**
     * Returns the SessionStrategy representing "Default"
     * @return SessionStrategy to represent the Default SessionStrategy for a set
     * context.
     */
    public static synchronized SessionStrategy createDefaultStrategy()
    {
        if(defaultSessionStrategy == null)
        {
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug("SessionProductFactory.createDefaultStrategy() caching SessionStrategyDefaultImpl",
                        GUILoggerBusinessProperty.PRODUCT_QUERY);
            }
            defaultSessionStrategy = new SessionStrategyDefaultImpl();
        }
        return defaultSessionStrategy;
    }
}