package com.cboe.domain.util.idlstructfactories;
import com.cboe.idl.cmiQuote.QuoteStructV3;
public class QuoteStructV3Factory extends com.cboe.domain.util.TLSObjectPool<QuoteStructV3> 
			implements com.cboe.idl.cmiQuote.QuoteStructV3Factory {
	public QuoteStructV3 createNewInstance() {
		return new QuoteStructV3 ();
	}

	public void clear (QuoteStructV3 value) {
		value.quote = null;
		value.quoteUpdateControlId = 0;
	}
	public  QuoteStructV3 create(com.cboe.idl.cmiQuote.QuoteStruct quote, short quoteUpdateControlId)
	{
		QuoteStructV3 rval = acquire();
		rval.quote=quote;
		rval.quoteUpdateControlId=quoteUpdateControlId;
		return rval;
	}
}
