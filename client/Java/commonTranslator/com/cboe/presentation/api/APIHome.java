//
// -----------------------------------------------------------------------------------
// Source file: APIHome.java
//
// PACKAGE: com.cboe.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import com.cboe.interfaces.presentation.api.*;
import com.cboe.interfaces.presentation.productConfiguration.ProductConfigurationQueryAPI;

import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 *  Used for building APIFactories to locate various Translator API services
 *  among client apps.
 *
 *@author     Troy Wehrle
 *@created    November 30, 2000
 *@version    (4/11/00 1:48:00 PM)
 */
public class APIHome
{
    private static APIFactory apiFactory = null;

    /**
     *  Creates the appropriate API Factory based on class name passed in and
     *  uses reflection to create it for later finds.
     *
     *@param  className  fully qualified class name to call find on to obtain an
     *      APIFactory.
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
     *  Creates the appropriate API Factory based on class passed in and uses
     *  reflection to create it for later finds.
     *
     *@param  theClass  class to call find on to obtain an APIFactory.
     */

    public static void create(Class theClass)
    {
        try
        {

            Class interfaceClass = com.cboe.interfaces.presentation.api.APIFactory.class;

            Object newOBJ = theClass.newInstance();

            if (interfaceClass.isInstance(newOBJ))
            {
                apiFactory = (APIFactory) newOBJ;
            }
            else
            {
                throw new IllegalArgumentException("APIHome: Does not support interface com.cboe.interfaces.presentation.api.APIFactory.class Name = " + theClass);
            }
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception("com.cboe.presentation.common.create()","",e);
        }
    }

    /**
     *  findAdministratorAPI method comment.
     *
     *@return    Description of the Returned Value
     */
    public static AdministratorAPI findAdministratorAPI()
    {
        if (apiFactory != null)
        {
            return apiFactory.findAdministratorAPI();
        }
        else
        {
            throw new IllegalStateException("APIHome: Create has not been called yet.");
        }
    }

    /**
     *  findCommonAPI method comment.
     *
     *@return    Description of the Returned Value
     */
    public static CommonAPI findCommonAPI()
    {
        if (apiFactory != null)
        {
            return apiFactory.findCommonAPI();
        }
        else
        {
            throw new IllegalStateException("APIHome: Create has not been called yet.");
        }
    }

    /**
     * Create a MaketQueryV4API.
     * @return the marketQueryV4API.
     * 
     * @see com.cboe.interfaces.presentation.api.MarketQueryV4API
     */
    public static MarketQueryV4API findMarketQueryV4API()
    {
        if(apiFactory != null)
        {
            return apiFactory.findMarketQueryV4API();
        }
        else
        {
            throw new IllegalStateException("APIHome: Create has not been called yet.");
        }
    }

    /**
     * Create a MarketQueryV5API.
     * 
     * @return the marketQueryV5API.
     * 
     * @see com.cboe.interfaces.presentation.api.MarketQueryV5API
     */
    public static MarketQueryV5API findMarketQueryV5API(){
    	if (apiFactory != null){
    		return apiFactory.findMarketQueryV5API();
    	}
    	else {
    		throw new IllegalStateException("APIHome: Create has not been called yet.");
    	}
    }
    
    
    /**
     *  findMarketQueryAPI method comment.
     *
     *@return    Description of the Returned Value
     */
    public static MarketQueryV3API findMarketQueryAPI()
    {
        if (apiFactory != null)
        {
            return apiFactory.findMarketQueryAPI();
        }
        else
        {
            throw new IllegalStateException("APIHome: Create has not been called yet.");
        }
    }

    /**
     *  findOrderQueryAPI method comment.
     *
     *@return    Description of the Returned Value
     */
    public static OrderQueryV3API findOrderQueryAPI()
    {
        if (apiFactory != null)
        {
            return apiFactory.findOrderQueryAPI();
        }
        else
        {
            throw new IllegalStateException("APIHome: Create has not been called yet.");
        }
    }

    /**
     *  findProductDefinitionAPI method comment.
     *
     *@return    Description of the Returned Value
     */
    public static ProductDefinitionAPI findProductDefinitionAPI()
    {
        if (apiFactory != null)
        {
            return apiFactory.findProductDefinitionAPI();
        }
        else
        {
            throw new IllegalStateException("APIHome: Create has not been called yet.");
        }
    }

    /**
     *  findProductQueryAPI method comment.
     *
     *@return    Description of the Returned Value
     */
    public static ProductQueryAPI findProductQueryAPI()
    {
        if (apiFactory != null)
        {
            return apiFactory.findProductQueryAPI();
        }
        else
        {
            throw new IllegalStateException("APIHome: Create has not been called yet.");
        }
    }

    /**
     *  findQuoteAPI method comment.
     *
     *@return    Description of the Returned Value
     */
    public static QuoteV7API findQuoteAPI()
    {
        if (apiFactory != null)
        {
            return apiFactory.findQuoteAPI();
        }
        else
        {
            throw new IllegalStateException("APIHome: Create has not been called yet.");
        }
    }

    /**
     *  findTradingSessionAPI method comment.
     *
     *@return    Description of the Returned Value
     */
    public static TradingSessionAPI findTradingSessionAPI()
    {
        if (apiFactory != null)
        {
            return apiFactory.findTradingSessionAPI();
        }
        else
        {
            throw new IllegalStateException("APIHome: Create has not been called yet.");
        }
    }

    /**
     *  findUserPreferenceQueryAPI method comment.
     *
     *@return    Description of the Returned Value
     */
    public static UserPreferenceQueryAPI findUserPreferenceQueryAPI()
    {
        if (apiFactory != null)
        {
            return apiFactory.findUserPreferenceQueryAPI();
        }
        else
        {
            throw new IllegalStateException("APIHome: Create has not been called yet.");
        }
    }

    /**
     *  findUserTradingParametersAPI method comment.
     *
     *@return    Description of the Returned Value
     */
    public static UserTradingParametersAPI findUserTradingParametersAPI()
    {
        if (apiFactory != null)
        {
            return apiFactory.findUserTradingParametersAPI();
        }
        else
        {
            throw new IllegalStateException("APIHome: Create has not been called yet.");
        }
    }

    /**
     *  findUserHistoryAPI method comment.
     *
     *@return    Description of the Returned Value
     */
    public static UserHistoryAPI findUserHistoryAPI()
    {
        if (apiFactory != null)
        {
            return apiFactory.findUserHistoryAPI();
        }
        else
        {
            throw new IllegalStateException("APIHome: Create has not been called yet.");
        }
    }

    public static ProductConfigurationQueryAPI findProductConfigurationQueryAPI()
    {
        if(apiFactory != null)
        {
            return apiFactory.findProductConfigurationQueryAPI();
        }
        else
        {
            throw new IllegalStateException("APIHome: Create has not been called yet.");
        }
    }

    public static ManualReportingAPI findManualReportingAPI()
    {
        if(apiFactory != null)
        {
            return apiFactory.findManualReportingAPI();
        }
        else
        {
            throw new IllegalStateException("APIHome: Create has not been called yet.");
        }
    }

    /**
     * Create an OrderManagementTerminalAPI.
     *
     * @see com.cboe.interfaces.presentation.api.OrderManagementTerminalAPI
     * @return 
     */
    public static OrderManagementTerminalAPI findOrderManagementTerminalAPI()
    {
        if(apiFactory != null)
        {
            return apiFactory.findOrderManagementTerminalAPI();
        }
        else
        {
            throw new IllegalStateException("APIHome: Create has not been called yet.");
        }
    }

    /**
     *  Initiates clean up on apiFactory.
     */
    public static void cleanUp()
    {
        if (apiFactory != null)
        {
            apiFactory.cleanUp();
        }
        else
        {
            throw new IllegalStateException("APIHome: Create has not been called yet.");
        }
    }
    
    public static FloorTradeAPI findFloorTradeAPI()
    {
        if(apiFactory != null)
        {
            return apiFactory.findFloorTradeAPI();
        }
        else
        {
            throw new IllegalStateException("APIHome: Create has not been called yet.");
        }
    }

    public static OrderFillCountAPI findOrderFillCountAPI()
    {
        if (apiFactory != null)
        {
            return apiFactory.findOrderFillCountAPI();
        }
        else
        {
            throw new IllegalArgumentException("APIHome: Create has not been called yet.");
        }
    }
}