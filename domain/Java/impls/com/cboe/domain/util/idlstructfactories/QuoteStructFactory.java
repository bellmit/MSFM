package com.cboe.domain.util.idlstructfactories;
import com.cboe.idl.cmiQuote.QuoteStruct;
public class QuoteStructFactory extends com.cboe.domain.util.TLSObjectPool<QuoteStruct> 
			implements com.cboe.idl.cmiQuote.QuoteStructFactory {
	public QuoteStruct createNewInstance() {
		return new QuoteStruct ();
	}

	public void clear (QuoteStruct value) {
		value.quoteKey = 0;
		value.productKey = 0;
		value.sessionName = null;
		value.userId = null;
		value.bidPrice = null;
		value.bidQuantity = 0;
		value.askPrice = null;
		value.askQuantity = 0;
		value.transactionSequenceNumber = 0;
		value.userAssignedId = null;
	}
	public  QuoteStruct create(int quoteKey, int productKey, java.lang.String sessionName, java.lang.String userId, com.cboe.idl.cmiUtil.PriceStruct bidPrice, int bidQuantity, com.cboe.idl.cmiUtil.PriceStruct askPrice, int askQuantity, int transactionSequenceNumber, java.lang.String userAssignedId)
	{
		QuoteStruct rval = acquire();
		rval.quoteKey=quoteKey;
		rval.productKey=productKey;
		rval.sessionName=sessionName;
		rval.userId=userId;
		rval.bidPrice=bidPrice;
		rval.bidQuantity=bidQuantity;
		rval.askPrice=askPrice;
		rval.askQuantity=askQuantity;
		rval.transactionSequenceNumber=transactionSequenceNumber;
		rval.userAssignedId=userAssignedId;
		return rval;
	}
}
