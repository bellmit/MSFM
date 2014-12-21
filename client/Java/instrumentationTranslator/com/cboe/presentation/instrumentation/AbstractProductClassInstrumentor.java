// -----------------------------------------------------------------------------------
// Source file: AbstractProductClassInstrumentor.java
//
// PACKAGE: com.cboe.presentation.instrumentation;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.instrumentation;

import com.cboe.interfaces.instrumentation.ProductClassContainer;
import com.cboe.interfaces.instrumentation.api.InstrumentationMonitorAPI;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.presentation.api.InstrumentationTranslatorFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import org.omg.CORBA.UserException;

public abstract class AbstractProductClassInstrumentor extends AbstractInstrumentor implements ProductClassContainer
{
    protected SessionProductClass[] sessionProductClasses;
    protected ProductClass[] productClasses;
    protected SessionKeyWrapper[] sessionKeys;

    public AbstractProductClassInstrumentor()
    {
        super();
    }

    /**
     * Returns SessionProductClasses array for the method instrumentor object.
     * @return SessionProductClass[]
     */
    public SessionProductClass[] getSessionProductClasses()
    {
        InstrumentationMonitorAPI api = InstrumentationTranslatorFactory.find();
        if (sessionProductClasses == null)
        {
            sessionProductClasses = new SessionProductClass[0];
            if ( api.isProductQueryServiceInitialized())
            {

                SessionKeyWrapper[] wrappers = getSessionProductClassKeys();

                if (wrappers != null && wrappers.length > 0)
                {
                    sessionProductClasses = new SessionProductClass[wrappers.length];
                    int productKey = -1;
                    String session = "";
                    for (int i = 0; i < wrappers.length; i++)
                    {
                        try
                        {
                            productKey = wrappers[i].getKey();
                            session = wrappers[i].getSessionName();
                            sessionProductClasses[i] = api.getClassByKeyForSession(session, productKey);
                        }
                        catch (UserException e)
                        {
                            sessionProductClasses[i] = api.getDefaultSessionProductClass();
                            GUILoggerHome.find().exception(this.getClass().getName() + ".getSessionProductClasses()", "Unable to get ProductClass for a key=" + productKey, e);
                        }
                    }
                }
            }
        }
        return sessionProductClasses;
    }

    /**
     * Returns ProductClasses array for the queue instrumentor object.
     * @return ProductClass[]
     */
    public ProductClass[] getProductClasses()
    {
        InstrumentationMonitorAPI api = InstrumentationTranslatorFactory.find();
        if (productClasses == null) 
        {
            productClasses = new ProductClass[0];
            if (api.isProductQueryServiceInitialized())
            {
                SessionKeyWrapper[] wrappers = getSessionProductClassKeys();
    
                if (wrappers != null && wrappers.length > 0)
                {
                    productClasses = new ProductClass[wrappers.length];
                    int productKey = -1;
                    for (int i = 0; i < wrappers.length; i++)
                    {
                        try
                        {
                            productKey = wrappers[i].getKey();
                            productClasses[i] = api.getProductClassByKey(productKey);
                        }
                        catch (UserException e)
                        {
                            productClasses[i] = api.getDefaultProductClass();
                            GUILoggerHome.find().exception(this.getClass().getName()+".getProductClasses()", "Unable to get ProductClass for a key=" + productKey, e);
                        }
                    }
                }
            }
        }
        return productClasses;
    }

    /**
     * Returns SessionProductClassKeys for the queue instrumentor object.
     * @return SessionKeyWrapper[]
     */
    public SessionKeyWrapper[] getSessionProductClassKeys()
    {
        if (sessionKeys == null)
        {
            sessionKeys = getSessionKeysFromUserData();
        }
        return sessionKeys;
    }


}
