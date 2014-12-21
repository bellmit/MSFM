package com.cboe.domain.util.idlstructfactories;
import com.cboe.idl.cmiOrder.OrderIdStruct;
public class OrderIdStructFactory extends com.cboe.domain.util.TLSObjectPool<OrderIdStruct> 
			implements com.cboe.idl.cmiOrder.OrderIdStructFactory {
	public OrderIdStruct createNewInstance() {
		return new OrderIdStruct ();
	}

	public void clear (OrderIdStruct value) {
		value.executingOrGiveUpFirm = null;
		value.branch = null;
		value.branchSequenceNumber = 0;
		value.correspondentFirm = null;
		value.orderDate = null;
		value.highCboeId = 0;
		value.lowCboeId = 0;
	}
	public  OrderIdStruct create(com.cboe.idl.cmiUser.ExchangeFirmStruct executingOrGiveUpFirm, java.lang.String branch, int branchSequenceNumber, java.lang.String correspondentFirm, java.lang.String orderDate, int highCboeId, int lowCboeId)
	{
		OrderIdStruct rval = acquire();
		rval.executingOrGiveUpFirm=executingOrGiveUpFirm;
		rval.branch=branch;
		rval.branchSequenceNumber=branchSequenceNumber;
		rval.correspondentFirm=correspondentFirm;
		rval.orderDate=orderDate;
		rval.highCboeId=highCboeId;
		rval.lowCboeId=lowCboeId;
		return rval;
	}
}
