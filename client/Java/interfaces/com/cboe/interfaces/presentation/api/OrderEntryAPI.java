package com.cboe.interfaces.presentation.api;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiProduct.ProductNameStruct;

public interface OrderEntryAPI
{
     OrderIdStruct acceptOrder(OrderEntryStruct anOrder)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException;

     OrderIdStruct acceptOrderByProductName(ProductNameStruct product, OrderEntryStruct anOrder)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException;

     void acceptOrderCancelRequest(CancelRequestStruct cancelRequest)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException;

     void acceptOrderUpdateRequest(int currentRemainingQuantity, OrderEntryStruct updatedOrder)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException;

     OrderIdStruct acceptOrderCancelReplaceRequest(OrderIdStruct orderId, int originalOrderRemainingQuantity, OrderEntryStruct newOrder)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException;

     void acceptCrossingOrder(OrderEntryStruct buyCrossingOrder, OrderEntryStruct sellCrossingOrder)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException;

     void acceptRequestForQuote(RFQEntryStruct rfq)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException;

     OrderIdStruct acceptStrategyOrder(OrderEntryStruct anOrder, LegOrderEntryStruct[] legEntryDetails)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException;

     void acceptStrategyOrderUpdateRequest(int currentRemainingQuantity, OrderEntryStruct updateOrder, LegOrderEntryStruct[] legEntryDetails)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException;

     OrderIdStruct acceptStrategyOrderCancelReplaceRequest(OrderIdStruct orderId, int cancelQuantity, OrderEntryStruct newOrder, LegOrderEntryStruct[] legEntryDetails)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException;
}
