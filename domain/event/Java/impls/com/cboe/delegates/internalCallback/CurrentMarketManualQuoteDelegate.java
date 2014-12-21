package com.cboe.delegates.internalCallback;


import com.cboe.interfaces.internalCallback.CurrentMarketManualQuoteConsumer;
import com.cboe.idl.internalCallback.POA_CurrentMarketManualQuoteConsumer_tie;

public class CurrentMarketManualQuoteDelegate extends POA_CurrentMarketManualQuoteConsumer_tie
{
	public CurrentMarketManualQuoteDelegate(CurrentMarketManualQuoteConsumer delegate)
    {
	    super(delegate);
    }
}
