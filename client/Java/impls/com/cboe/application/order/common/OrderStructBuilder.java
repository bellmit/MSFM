package com.cboe.application.order.common;

import com.cboe.domain.util.TimeServiceWrapper;
import com.cboe.idl.cmiConstants.OrderStates;
import com.cboe.idl.cmiConstants.Sources;
import com.cboe.idl.cmiOrder.OrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;

/*
 * This class defines few methods for CAS only use.  
 */
public class OrderStructBuilder {

	public static OrderIdStruct buildOrderIdStruct( OrderEntryStruct orderEntry ) {

	    OrderIdStruct orderId = null;
	    if (orderEntry != null )
	    {
	        orderId = new OrderIdStruct(orderEntry.executingOrGiveUpFirm,
	        		orderEntry.branch,
	                orderEntry.branchSequenceNumber,
	                orderEntry.correspondentFirm,
	                orderEntry.orderDate,
	                0,
	                0);
	        if (orderId.orderDate == null || "".equals(orderEntry.orderDate)) 
	        {
	            orderId.orderDate = TimeServiceWrapper.formatToDate();
	        }
	    }
	    else
	    {
	        orderId = com.cboe.domain.util.OrderStructBuilder.buildOrderIdStruct();
	    }
	    return orderId;
	}
	
	public static OrderStruct  buildOrderStruct(OrderEntryStruct orderEntry, ProductKeysStruct productKeys, String theUserId, ExchangeAcronymStruct userAcronym)
	{
	    OrderStruct order = null;
	    if(orderEntry != null)
	    {
	        OrderIdStruct orderId = buildOrderIdStruct(orderEntry);

	        order = new OrderStruct();
	        order.account = orderEntry.account;
	        order.cancelledQuantity = 0;

	        order.leavesQuantity              = 0;
	        order.averagePrice                = orderEntry.price;
	        order.sessionTradedQuantity       = 0;
	        order.sessionCancelledQuantity    = 0;
	        order.sessionAveragePrice         = orderEntry.price;

	        order.classKey = productKeys.classKey;
	        order.cmta = orderEntry.cmta;
	        order.contingency = orderEntry.contingency;
	        order.coverage = orderEntry.coverage;
	        order.orderNBBOProtectionType = orderEntry.orderNBBOProtectionType;
	        order.cross = orderEntry.cross;
	        order.crossedOrder = com.cboe.domain.util.OrderStructBuilder.buildOrderIdStruct(); // default
	        order.expireTime = orderEntry.expireTime;
	        order.extensions = orderEntry.extensions;
	        order.optionalData = orderEntry.optionalData;
	        order.orderId = orderId;
	        order.originalQuantity = orderEntry.originalQuantity;
	        order.orderOriginType = orderEntry.orderOriginType;
	        order.originator = orderEntry.originator;
	        order.orsId = "";
	        order.positionEffect = orderEntry.positionEffect;
	        order.price = orderEntry.price;
	        order.productKey = productKeys.productKey;
	        order.productType = productKeys.productType;
	        order.receivedTime = TimeServiceWrapper.toDateTimeStruct();
	        order.side = orderEntry.side;
	        order.source = Sources.SBT;
	        order.state = OrderStates.ACTIVE;
	        order.subaccount = orderEntry.subaccount;
	        order.timeInForce = orderEntry.timeInForce;
	        order.tradedQuantity = 0;
	        order.transactionSequenceNumber = 1;
	        order.userId = theUserId;
	        order.userAssignedId = orderEntry.userAssignedId;
	        order.activeSession = "";
	        order.sessionNames = orderEntry.sessionNames;
	        order.userAcronym = userAcronym;
	        order.legOrderDetails = com.cboe.domain.util.OrderStructBuilder.buildLegOrderDetailStructSequence();
	    }
	    else
	    {
	        order = com.cboe.domain.util.OrderStructBuilder.buildOrderStruct();
	    }
	    return order;
	}

}
