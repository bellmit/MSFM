
package com.cboe.interfaces.intermarketPresentation.api;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.util.event.EventChannelListener;

public interface IntermarketHeldOrderQueryAPI
{
    public HeldOrderDetailStruct getHeldOrderById(String session, int productKey, OrderIdStruct orderId)
	     throws SystemException,CommunicationException,AuthorizationException,DataValidationException,NotFoundException;

    public HeldOrderDetailStruct[] getHeldOrdersByClassForSession(String session, int classKey);
}
