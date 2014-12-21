package com.cboe.interfaces.presentation.api;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiOrder.InternalizationOrderResultStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStructV2;
import com.cboe.idl.cmiOrder.OrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;

public interface OrderEntryV7API extends OrderEntryV5API
{
	OrderIdStruct acceptStrategyOrderV7(OrderEntryStruct anOrder,LegOrderEntryStructV2 [] legEntryDetailsV2)
                 throws	SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   	NotAcceptedException, TransactionFailedException, AlreadyExistsException;
    
	OrderIdStruct acceptStrategyOrderCancelReplaceRequestV7(OrderIdStruct orderId, int cancelQuantity, OrderEntryStruct newOrder, LegOrderEntryStructV2[] legEntryDetailsV2)
     throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException;
	
	
    InternalizationOrderResultStruct acceptInternalizationStrategyOrderV7(OrderEntryStruct primaryOrder,LegOrderEntryStructV2[] primaryOrderLegEntriesV2,
    		OrderEntryStruct matchOrder,LegOrderEntryStructV2[] matchOrderLegEntriesV2,short matchType)
                throws	SystemException,CommunicationException,AuthorizationException,DataValidationException,
                    NotAcceptedException,TransactionFailedException;
}
