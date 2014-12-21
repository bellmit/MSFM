package com.cboe.domain.util.idlstructfactories;
import com.cboe.idl.cmiQuote.ClassQuoteResultStructV3;
public class ClassQuoteResultStructV3Factory extends com.cboe.domain.util.TLSObjectPool<ClassQuoteResultStructV3> 
			implements com.cboe.idl.cmiQuote.ClassQuoteResultStructV3Factory {
	public ClassQuoteResultStructV3 createNewInstance() {
		return new ClassQuoteResultStructV3 ();
	}

	public void clear (ClassQuoteResultStructV3 value) {
		value.quoteResult = null;
		value.quoteUpdateControlId = 0;
	}
	public  ClassQuoteResultStructV3 create(com.cboe.idl.cmiQuote.ClassQuoteResultStructV2 quoteResult, short quoteUpdateControlId)
	{
		ClassQuoteResultStructV3 rval = acquire();
		rval.quoteResult=quoteResult;
		rval.quoteUpdateControlId=quoteUpdateControlId;
		return rval;
	}
}
