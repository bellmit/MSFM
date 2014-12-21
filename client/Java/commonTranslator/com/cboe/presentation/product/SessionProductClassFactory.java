//
// -----------------------------------------------------------------------------------
// Source file: SessionProductClassFactory.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import java.util.*;

import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiProduct.ClassStruct;

import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.tradingSession.DefaultTradingSession;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

/**
 *  Factory for creating instances of SessionProductClass
 */
public class SessionProductClassFactory
{
    private static SessionProductClass allSelectedSessionProductClass = null;
    private static SessionProductClass defaultSessionProductClass = null;
    private static Map sessionAllSelected = new HashMap(5);
    private static Map sessionDefaultSelected = new HashMap(5);
    private static Map<String, Map<Short, SessionProductClass>> sessionAllSelectedByProductType = new HashMap<String, Map<Short, SessionProductClass>>(5);

    /**
     *  Hidden to prevent instantiation of this factory since it is a static
     *  factory.
     */
    private SessionProductClassFactory()
    {}

    /**
     * Creates an instance of a SessionProductClass from a SessionClassStruct.
     * @param sessionClassStruct to wrap in instance of SessionProductClass
     * @return SessionProductClass to represent the SessionClassStruct
     */
    public static SessionProductClass create(SessionClassStruct sessionClassStruct)
    {
        if (sessionClassStruct == null)
        {
            throw new IllegalArgumentException();
        }
        SessionProductClass sessionClass;
        sessionClass = new SessionProductClassImpl(sessionClassStruct);

        return sessionClass;
    }

    public static SessionProductClass create(String sessionName, ClassStruct classStruct)
    {
        if( classStruct == null )
        {
            throw new IllegalArgumentException("classStruct may not be null");
        }
        if(sessionName == null || sessionName.length() == 0)
        {
            throw new IllegalArgumentException("sessionName may not be null or empty String.");
        }
        if( !sessionName.equals(DefaultTradingSession.DEFAULT) )
        {
            throw new IllegalArgumentException("This is only valid for the default trading session.");
        }
        SessionProductClass sessionClass;
        sessionClass = new DefaultSessionProductClassImpl(sessionName, classStruct);

        return sessionClass;
    }

    public static SessionProductClass create(String sessionName, ProductClass productClass)
    {
        if( productClass == null )
        {
            throw new IllegalArgumentException("productClass may not be null");
        }
        SessionProductClass sessionClass = create(sessionName,  productClass.getClassStruct());

        return sessionClass;
    }

    /**
     * Returns the SessionProductClass representing "All Selected"
     * @return SessionProductClass to represent all SessionProductClass'es for a set
     * context.
     */
    public static synchronized SessionProductClass createAllSelected()
    {
        if(allSelectedSessionProductClass == null)
        {
            allSelectedSessionProductClass = new SessionProductClassAllSelectedImpl();
            sessionAllSelected.put(allSelectedSessionProductClass.getTradingSessionName(), allSelectedSessionProductClass);
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug("SessionProductClassFactory.createAllSelected() caching SessionProductClassAllSelectedImpl for session '"+
                        allSelectedSessionProductClass.getTradingSessionName()+"'", GUILoggerBusinessProperty.PRODUCT_QUERY);
            }
        }

        return allSelectedSessionProductClass;
    }

    public static synchronized SessionProductClass createAllSelected(String tradingSession, short productType)
    {
        Map<Short, SessionProductClass> prodTypeMap = sessionAllSelectedByProductType.get(tradingSession);
        if(prodTypeMap == null)
        {
            prodTypeMap = new HashMap<Short, SessionProductClass>(15);
            sessionAllSelectedByProductType.put(tradingSession, prodTypeMap);
        }
        SessionProductClass spc = prodTypeMap.get(productType);
        if(spc == null)
        {
            spc = new SessionProductClassAllSelectedForTypeImpl(tradingSession, productType);
            prodTypeMap.put(productType, spc);
        }
        return spc;
    }

    /**
     * Returns the SessionProductClass representing "Default"
     * @return SessionProductClass to represent the Default SessionProductClass for a set
     * context.
     */
    public static synchronized SessionProductClass createDefault()
    {
        if(defaultSessionProductClass == null)
        {
            defaultSessionProductClass = new SessionProductClassDefaultImpl();
            sessionDefaultSelected.put(defaultSessionProductClass.getTradingSessionName(), defaultSessionProductClass);
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug("SessionProductClassFactory.createDefault() caching SessionProductClassDefaultImpl for session '" +
                        defaultSessionProductClass.getTradingSessionName() + "'", GUILoggerBusinessProperty.PRODUCT_QUERY);
            }
        }

        return defaultSessionProductClass;
    }

    /**
     * Returns the SessionProductClass representing "All Selected"
     * @return SessionProductClass to represent all SessionProductClass'es for a set
     * context.
     */
    public static synchronized SessionProductClass createAllSelected(String sessionName)
    {
        SessionProductClass pc = ( SessionProductClass ) sessionAllSelected.get(sessionName);
        if( pc == null )
        {
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug("SessionProductClassFactory.createAllSelected() caching SessionProductClassAllSelectedImpl for session '"+
                        sessionName+"'", GUILoggerBusinessProperty.PRODUCT_QUERY);
            }
            pc = new SessionProductClassAllSelectedImpl(sessionName);
            sessionAllSelected.put(sessionName, pc);
        }

        return pc;
    }

    /**
     * Returns the SessionProductClass representing "Default"
     * @return SessionProductClass to represent the Default SessionProductClass for a set
     * context.
     */
    public static synchronized SessionProductClass createDefault(String sessionName)
    {
        SessionProductClass pc = ( SessionProductClass ) sessionDefaultSelected.get(sessionName);
        if( pc == null )
        {
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug("SessionProductClassFactory.createDefault() caching SessionProductClassDefaultImpl for session '"+
                        sessionName +"'", GUILoggerBusinessProperty.PRODUCT_QUERY);
            }
            pc = new SessionProductClassDefaultImpl(sessionName);
            sessionDefaultSelected.put(sessionName, pc);
        }

        return pc;
    }
    
    public static SessionProductClass createInvalid(SessionClassStruct classStruct)
    {
        if (classStruct == null)
        {
            throw new IllegalArgumentException("classStruct may not be null");
        }

        return new InvalidSessionProductClassImpl(classStruct);
    }

    public static SessionProductClass createInvalid(String sessionName, ProductClass productClass)
    {
        if (productClass == null)
        {
            throw new IllegalArgumentException("productClass may not be null");
        }
        
        if (sessionName == null)
        {
            throw new IllegalArgumentException("sessionName may not be null.");
        }

        return new InvalidSessionProductClassImpl(sessionName, productClass.getClassStruct());
    }
    
    public static SessionProductClass createInvalid(int classKey)
    {
        return new InvalidSessionProductClassImpl(classKey);
    }
    
}
