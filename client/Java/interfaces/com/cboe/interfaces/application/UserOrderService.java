package com.cboe.interfaces.application;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiQuote.RFQStruct;
import com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.order.CrossOrderIdStruct;

/**
 * @author Jing Chen
 */
public interface UserOrderService
{
    void acceptCancel(CancelRequestStruct cancelRequestStruct, ProductKeysStruct productKeysStruct)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException;
    OrderIdStruct acceptCancelReplace(CancelRequestStruct cancelRequestStruct, OrderStruct orderStruct)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException;
    OrderIdStruct acceptOrder(OrderStruct orderStruct)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException;
    InternalizationOrderResultStruct acceptInternalizationOrder(OrderStruct primaryOrder, OrderStruct matchOrder, short matchType)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException;
    InternalizationOrderResultStruct acceptInternalizationStrategyOrder(OrderStruct orderStruct, LegOrderEntryStruct[] legOrderEntryStructs,
                                                                        OrderStruct orderStruct1, LegOrderEntryStruct[] legOrderEntryStructs1, short i)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException;
    InternalizationOrderResultStruct acceptInternalizationStrategyOrderV7(OrderStruct orderStruct, LegOrderEntryStructV2[] legOrderEntryStructsV2,
                                                                            OrderStruct orderStruct1, LegOrderEntryStructV2[] legOrderEntryStructs1V2, short i)
                throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException;
    void acceptCrossingOrder(OrderStruct orderStruct, OrderStruct orderStruct1)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException;
    CrossOrderIdStruct acceptCrossingOrderV2(OrderStruct orderStruct, OrderStruct orderStruct1)
    	throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException;
    OrderIdStruct acceptStrategyCancelReplace(CancelRequestStruct cancelRequestStruct, OrderStruct orderStruct, LegOrderEntryStruct[] legOrderEntryStructs)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException;
    OrderIdStruct acceptStrategyCancelReplaceV7(CancelRequestStruct cancelRequestStruct, OrderStruct orderStruct, LegOrderEntryStructV2[] legOrderEntryStructsV2)
                throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException;
    OrderIdStruct acceptStrategyOrder(OrderStruct orderStruct, LegOrderEntryStruct[] legOrderEntryStructs)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException;
    OrderIdStruct acceptStrategyOrderV7(OrderStruct orderStruct, LegOrderEntryStructV2[] legOrderEntryStructsV2)
                throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException;
    void acceptStrategyUpdate(int remainingQuantity, OrderStruct orderStruct, LegOrderEntryStruct[] legOrderEntryStructs)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException;
    void acceptUpdate(int remainingQuantity, OrderStruct orderStruct)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException;
    OrderDetailStruct getOrderById(OrderIdStruct orderIdStruct)
            throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException;
    OrderDetailStruct getOrderByIdFromCache(OrderIdStruct orderIdStruct)
    		throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException;
    OrderDetailStruct getOrderByIdFromCacheForFIX(OrderIdStruct orderIdStruct)
    		throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException;
    PendingOrderStruct[] getPendingAdjustmentOrdersByClass(String sessionName, int classKey)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException;
    PendingOrderStruct[] getPendingAdjustmentOrdersByProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException;
    void publishOrdersForFirm(ExchangeFirmStruct exchangeFirmStruct)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException;
    void publishOrders()
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException;
    ActivityHistoryStruct queryOrderHistory(String sessionName, int productKey, OrderIdStruct orderIdStruct)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    void requestForQuote(RFQStruct rfq)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException,AuthorizationException;
    OrderDetailStruct[] getOrdersForClass(int productClass)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    OrderDetailStruct[] getOrdersForProduct(int pKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    OrderDetailStruct[] getOrdersForSession(String sessionName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    OrderDetailStruct[] getOrdersForType(short type)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    void publishUnAckedOrders()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    void publishUnAckedOrdersForClass(int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserOrderEntryEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserOrderQueryEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserOrderQueryEnablementForSession(String sessionName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserOrderRFQEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserAuctionEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;    
    public void verifyUserOrderEntryEnablementForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserOrderQueryEnablementForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserOrderRFQEnablementForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    LightOrderResultStruct acceptLightOrder(LightOrderEntryStruct orderEntryStruct)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException;
    LightOrderResultStruct acceptLightOrderCancelRequest(String branch, int branchSequenceNumber, int productKey, String activeSession, String userAssignedCancelId)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException;
    LightOrderResultStruct acceptLightOrderCancelRequestById(int orderHighId, int orderLowId, int productKey, String activeSession, String userAssignedCancelId)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException;

}
