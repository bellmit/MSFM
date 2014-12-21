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

import com.cboe.idl.cmiOrder.InternalizationOrderResultStruct;
import com.cboe.idl.cmiOrder.OrderEntryStruct;
import com.cboe.exceptions.*;

public interface OrderEntryV3API extends OrderEntryAPI
{
    InternalizationOrderResultStruct acceptInternalizationOrder(OrderEntryStruct orderEntryStruct1,
                                                                OrderEntryStruct orderEntryStruct2, short matchOrderType)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   NotAcceptedException, TransactionFailedException;

}
