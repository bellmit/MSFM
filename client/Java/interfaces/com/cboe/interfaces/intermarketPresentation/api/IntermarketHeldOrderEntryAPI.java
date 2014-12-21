
package com.cboe.interfaces.intermarketPresentation.api;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.exceptions.NotAcceptedException;

import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderEntryStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;

public interface IntermarketHeldOrderEntryAPI
{
     public void rerouteHeldOrder(OrderIdStruct heldOrderId,String session,int productKey,boolean nbboProtectionFlag)
          throws SystemException,CommunicationException,AuthorizationException,
                DataValidationException, TransactionFailedException,NotAcceptedException;

     public void rerouteHeldOrderByClass(int classKey, String session, boolean nbboProtectionFlag)
          throws SystemException, CommunicationException, AuthorizationException,
                DataValidationException, TransactionFailedException, NotAcceptedException;

     public void acceptCancelResponse(OrderIdStruct orderId,CboeIdStruct cancelRequestId,String session,int productKey)
          throws SystemException,CommunicationException,AuthorizationException,
                NotAcceptedException,TransactionFailedException,DataValidationException;

     public void acceptFillHeldOrder(OrderIdStruct heldOrderId,String session,OrderEntryStruct nbboAgentOrder)
          throws SystemException,CommunicationException,AuthorizationException,
                NotAcceptedException,TransactionFailedException,DataValidationException;
}
