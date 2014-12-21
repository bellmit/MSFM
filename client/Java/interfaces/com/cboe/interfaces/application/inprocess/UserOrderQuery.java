package com.cboe.interfaces.application.inprocess;

import com.cboe.exceptions.*;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.idl.cmiOrder.*;

/**
 * @author Jing Chen
 */
public interface UserOrderQuery
{
    public OrderDetailStruct getOrderById(OrderIdStruct orderId)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public void subscribeOrderStatus(OrderStatusConsumer fixOrderStatusConsumer, boolean publishOnSubscribe)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void subscribeOrderStatusForFirm(OrderStatusConsumer fixOrderStatusConsumer, boolean publishOnSubscribe)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void subscribeAuctionForClass(String sessionName, int classKey, short auctionType, AuctionConsumer clientListener)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException;
    public void unsubscribeAuctionForClass(String sessionName, int classKey, short auctionType, AuctionConsumer clientListener)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException;
    public OrderDetailStruct getOrderByIdFromCache(OrderIdStruct orderId)
    	throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
}
