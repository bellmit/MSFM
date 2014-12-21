// -----------------------------------------------------------------------------------
// Source file: OrderEntryV3API
//
// PACKAGE: api
// 
// Created: Sep 13, 2004 12:20:27 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiOrder.InternalizationOrderResultStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderEntryStruct;

public interface OrderEntryV5API extends OrderEntryV3API
{
    InternalizationOrderResultStruct acceptInternalizationStrategyOrder( OrderEntryStruct primaryOrder,
    																	 LegOrderEntryStruct[] primaryOrderLegDetails,
    																	 OrderEntryStruct matchOrder,
    																	 LegOrderEntryStruct[] matchOrderLegDetails,
                                                                		 short matchOrderType)
            							throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            								   NotAcceptedException, TransactionFailedException;
}
