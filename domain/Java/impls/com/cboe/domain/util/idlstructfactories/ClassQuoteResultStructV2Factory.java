package com.cboe.domain.util.idlstructfactories;

import com.cboe.idl.cmiQuote.ClassQuoteResultStructV2;
public class ClassQuoteResultStructV2Factory extends com.cboe.domain.util.TLSObjectPool<ClassQuoteResultStructV2> 
			implements com.cboe.idl.cmiQuote.ClassQuoteResultStructV2Factory {
	public ClassQuoteResultStructV2 createNewInstance() {
		return new ClassQuoteResultStructV2 ();
	}

	public void clear (ClassQuoteResultStructV2 value) {
		value.quoteKey = 0;
		value.productKey = 0;
		value.errorCode = 0;
	}
	public  ClassQuoteResultStructV2 create(int quoteKey, int productKey, int errorCode)
	{
		ClassQuoteResultStructV2 rval = acquire();
		rval.quoteKey=quoteKey;
		rval.productKey=productKey;
		rval.errorCode=errorCode;
		return rval;
	}
}
