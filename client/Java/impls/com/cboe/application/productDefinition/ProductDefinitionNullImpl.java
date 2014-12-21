// $Workfile$ com.cboe.application.productDefinition.ProductDefinitionNullImpl.java
// $Revision$
// Last Modification on:  09/07/1999 03:20:14 fengc
/* $Log$
*   Initial Version                         03/15/1999      fengc
*   Increment 4                             09/07/1999      fengc
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.application.productDefinition;

import java.util.*;

import com.cboe.interfaces.application.*;
import com.cboe.interfaces.businessServices.*;

import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.product.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiStrategy.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.idl.cmiUtil.*;

import com.cboe.util.*;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.event.*;
import com.cboe.domain.util.ProductStrategyStructBuilder;
import com.cboe.domain.util.ClientProductStructBuilder;

import com.cboe.application.shared.consumer.*;
import com.cboe.application.supplier.proxy.*;
import com.cboe.application.supplier.*;
import com.cboe.application.shared.*;

import com.cboe.interfaces.events.*;

import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.loggingService.*;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.*;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
/**
* <b>Description</b>
* <p>
*    Implemetation of the Product Query interface
* </p>
*/
public class ProductDefinitionNullImpl extends BObject
                                     implements ProductDefinition
{
    private ProductQueryService     productQueryService;

    private SessionManager          currentSession;

    private static int keyNumber = 1;
    /**
     * ProductDefinitionImpl constructor.
     */
    public ProductDefinitionNullImpl()
    {
        super();
    }// end of constructor

    protected void setSessionManager(SessionManager session)
    {
        currentSession = session;
    }
    /**
     * Returns reference to product query service.
     * The Home should return the ProductQueryServiceCacheProxy.
     *
     * @author Connie Feng
     */
    private ProductQueryService getQueryService()
    {
        if (productQueryService == null )
        {
            productQueryService = ServicesHelper.getProductQueryService();
        }

        return productQueryService;
    }// end of getQueryService


    /////////////// IDL exported methods ////////////////////////////////////

    /**
    * @description
    * Request a standard trading strategy
    *
    * @usage
    * You are required to select a standard strategy
    *
    *
    * @returns product structure for the standard strategy
    * exception NotFoundException
    * exception InvalidProductException
    * exception CreateFailedException
    */
    public SessionStrategyStruct acceptStrategy(String sessionName, StrategyRequestStruct strategyRequest)
       throws SystemException, CommunicationException, AuthorizationException,DataValidationException
    {
        SessionStrategyStruct sessionStrategyStruct = null;
        sessionStrategyStruct.strategyType = 2; //STRADDLE
        sessionStrategyStruct.sessionProductStruct = ClientProductStructBuilder.buildSessionProductStruct();

        SessionStrategyLegStruct[] strategyLegs = new SessionStrategyLegStruct[strategyRequest.strategyLegs.length];
        for (int i=0; i<strategyLegs.length; i++)
        {
            strategyLegs[i].product = strategyRequest.strategyLegs[i].product;
            strategyLegs[i].ratioQuantity = strategyRequest.strategyLegs[i].ratioQuantity;
            strategyLegs[i].side = strategyRequest.strategyLegs[i].side;
            strategyLegs[i].sessionName = sessionName;
        }
        sessionStrategyStruct.sessionStrategyLegs = strategyLegs;

        return sessionStrategyStruct;
    }

    /**
     * Build the Null Strategy Request struct
     *
     * @returns com.cboe.idl.cmiStrategy.StrategyRequestStruct
     * @param   strategyType (out of 8 different types)
     * @param   anchorProduct
     * @param   priceIncrement
     * @param   monthIncrement
     * @throws SystemException
     * @throws CommunicationException
     * @throws DataValidationException
     * @throws AuthorizationException
     */
     public StrategyRequestStruct buildStrategyRequestByName(short strategyType, ProductNameStruct anchorProduct, PriceStruct priceIncrement, short monthIncrement)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling buildStrategyRequestByName for " + currentSession);
        }

        StrategyLegStruct[] strategyLegs = new StrategyLegStruct[2];
        for (int i=0; i<strategyLegs.length; i++)
        {
            strategyLegs[i].product = keyNumber++;
            strategyLegs[i].ratioQuantity = 1;
            strategyLegs[i].side = 'B';
        }

        StrategyRequestStruct strategyRequest = new StrategyRequestStruct(strategyLegs);
        return strategyRequest;
    }

    /**
     * Build the Null Strategy Request struct
     *
     * @returns com.cboe.idl.cmiStrategy.StrategyRequestStruct
     * @param   strategyType (out of 8 different types)
     * @param   anchorProductKey
     * @param   priceIncrement
     * @param   monthIncrement
     * @throws SystemException
     * @throws CommunicationException
     * @throws DataValidationException
     * @throws AuthorizationException
     */
     public StrategyRequestStruct buildStrategyRequestByProductKey(short strategyType, int anchorProductKey, PriceStruct priceIncrement, short monthIncrement)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling buildStrategyRequestByProductKey for " + currentSession);
        }

        StrategyLegStruct[] strategyLegs = new StrategyLegStruct[2];
        for (int i=0; i<strategyLegs.length; i++)
        {
            strategyLegs[i].product = keyNumber++;
            strategyLegs[i].ratioQuantity = 1;
            strategyLegs[i].side = 'B';
        }

        StrategyRequestStruct strategyRequest = new StrategyRequestStruct(strategyLegs);
        return strategyRequest;
    }

    /**
    * @description
    * Request a custom trading strategy
    *
    * @usage
    * This method is used to request a custom trading strategy.
    * ProductStrategyLegStructs are created for each product that is part of the
    * strategy. At this point in time strategies are only permitted within a class.
    * The productKey for the product is only valid for the life of the order.
    *
    * @returns product structure
    * exception NotFoundException
    * exception InvalidStrategyException
    * exception CreateFailedException
    */
    //public StrategyStruct getCustomStrategy(StrategyLegStruct[] strategyLegs)
    //    throws NotFoundException, InvalidStrategyException, CreateFailedException
    //{
    //   return ProductStrategyStructBuilder.buildStrategyStruct(strategyLegs);
    //}
}// EOF
