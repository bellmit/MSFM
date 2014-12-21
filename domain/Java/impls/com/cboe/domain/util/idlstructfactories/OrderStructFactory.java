package com.cboe.domain.util.idlstructfactories;
import com.cboe.idl.cmiOrder.OrderStruct;
public class OrderStructFactory extends com.cboe.domain.util.TLSObjectPool<OrderStruct> 
			implements com.cboe.idl.cmiOrder.OrderStructFactory {
	public OrderStruct createNewInstance() {
		return new OrderStruct ();
	}

	public void clear (OrderStruct value) {
		value.orderId = null;
		value.originator = null;
		value.originalQuantity = 0;
		value.productKey = 0;
		value.side = 0;
		value.price = null;
		value.timeInForce = 0;
		value.expireTime = null;
		value.contingency = null;
		value.cmta = null;
		value.extensions = null;
		value.account = null;
		value.subaccount = null;
		value.positionEffect = 0;
		value.cross = false;
		value.orderOriginType = 0;
		value.coverage = 0;
		value.orderNBBOProtectionType = 0;
		value.optionalData = null;
		value.userId = null;
		value.userAcronym = null;
		value.productType = 0;
		value.classKey = 0;
		value.receivedTime = null;
		value.state = 0;
		value.tradedQuantity = 0;
		value.cancelledQuantity = 0;
		value.leavesQuantity = 0;
		value.averagePrice = null;
		value.sessionTradedQuantity = 0;
		value.sessionCancelledQuantity = 0;
		value.sessionAveragePrice = null;
		value.orsId = null;
		value.source = 0;
		value.crossedOrder = null;
		value.transactionSequenceNumber = 0;
		value.userAssignedId = null;
		value.sessionNames = null;
		value.activeSession = null;
		value.legOrderDetails = null;
	}
	public  OrderStruct create(com.cboe.idl.cmiOrder.OrderIdStruct orderId, com.cboe.idl.cmiUser.ExchangeAcronymStruct originator, int originalQuantity, int productKey, char side, com.cboe.idl.cmiUtil.PriceStruct price, char timeInForce, com.cboe.idl.cmiUtil.DateTimeStruct expireTime, com.cboe.idl.cmiOrder.OrderContingencyStruct contingency, com.cboe.idl.cmiUser.ExchangeFirmStruct cmta, java.lang.String extensions, java.lang.String account, java.lang.String subaccount, char positionEffect, boolean cross, char orderOriginType, char coverage, short orderNBBOProtectionType, java.lang.String optionalData, java.lang.String userId, com.cboe.idl.cmiUser.ExchangeAcronymStruct userAcronym, short productType, int classKey, com.cboe.idl.cmiUtil.DateTimeStruct receivedTime, short state, int tradedQuantity, int cancelledQuantity, int leavesQuantity, com.cboe.idl.cmiUtil.PriceStruct averagePrice, int sessionTradedQuantity, int sessionCancelledQuantity, com.cboe.idl.cmiUtil.PriceStruct sessionAveragePrice, java.lang.String orsId, char source, com.cboe.idl.cmiOrder.OrderIdStruct crossedOrder, int transactionSequenceNumber, java.lang.String userAssignedId, java.lang.String[] sessionNames, java.lang.String activeSession, com.cboe.idl.cmiOrder.LegOrderDetailStruct[] legOrderDetails)
	{
		OrderStruct rval = acquire();
		rval.orderId=orderId;
		rval.originator=originator;
		rval.originalQuantity=originalQuantity;
		rval.productKey=productKey;
		rval.side=side;
		rval.price=price;
		rval.timeInForce=timeInForce;
		rval.expireTime=expireTime;
		rval.contingency=contingency;
		rval.cmta=cmta;
		rval.extensions=extensions;
		rval.account=account;
		rval.subaccount=subaccount;
		rval.positionEffect=positionEffect;
		rval.cross=cross;
		rval.orderOriginType=orderOriginType;
		rval.coverage=coverage;
		rval.orderNBBOProtectionType=orderNBBOProtectionType;
		rval.optionalData=optionalData;
		rval.userId=userId;
		rval.userAcronym=userAcronym;
		rval.productType=productType;
		rval.classKey=classKey;
		rval.receivedTime=receivedTime;
		rval.state=state;
		rval.tradedQuantity=tradedQuantity;
		rval.cancelledQuantity=cancelledQuantity;
		rval.leavesQuantity=leavesQuantity;
		rval.averagePrice=averagePrice;
		rval.sessionTradedQuantity=sessionTradedQuantity;
		rval.sessionCancelledQuantity=sessionCancelledQuantity;
		rval.sessionAveragePrice=sessionAveragePrice;
		rval.orsId=orsId;
		rval.source=source;
		rval.crossedOrder=crossedOrder;
		rval.transactionSequenceNumber=transactionSequenceNumber;
		rval.userAssignedId=userAssignedId;
		rval.sessionNames=sessionNames;
		rval.activeSession=activeSession;
		rval.legOrderDetails=legOrderDetails;
		return rval;
	}
}
