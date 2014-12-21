//
// -----------------------------------------------------------------------------------
// Source file: ProductDefinitionAPI.java
//
// PACKAGE: com.cboe.interfaces.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api;

import com.cboe.idl.cmiStrategy.StrategyRequestStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiUtil.PriceStruct;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.presentation.product.SessionStrategy;

public interface ProductDefinitionAPI
{
    /**
     * Accepts a new strategy for addition to the system.
     *
     * @return the accepted strategy struct
     * @param strategyRequest the new strategy request struct
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public SessionStrategy acceptSessionStrategy(String sessionName, StrategyRequestStruct strategyRequest)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public StrategyRequestStruct buildStrategyRequestByName(short strategyType, ProductNameStruct anchorProduct, PriceStruct priceIncrement, short monthIncrement)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public StrategyRequestStruct buildStrategyRequestByProductKey(short strategyType, int anchorProductKey, PriceStruct priceIncrement, short monthIncrement)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
