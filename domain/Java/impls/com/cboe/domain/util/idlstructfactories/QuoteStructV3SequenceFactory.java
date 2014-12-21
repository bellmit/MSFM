package com.cboe.domain.util.idlstructfactories;

import com.cboe.domain.util.TLSStructArray;
import com.cboe.idl.cmiQuote.QuoteStructV3;

public class QuoteStructV3SequenceFactory implements
        com.cboe.idl.cmiQuote.QuoteStructV3SequenceFactory
{
    public static class QuoteStructV3Array extends TLSStructArray<QuoteStructV3>
    {
        protected QuoteStructV3[] createArray(int p_len)
        {
            return new QuoteStructV3[p_len];
        }
    };

    private ThreadLocal<QuoteStructV3Array> structArray = new ThreadLocal<QuoteStructV3SequenceFactory.QuoteStructV3Array>()
    {
        protected QuoteStructV3Array initialValue()
        {
            return new QuoteStructV3Array();
        }
    };

    public com.cboe.idl.cmiQuote.QuoteStructV3[] create(int len)
    {
        return structArray.get().acquire(len);
    }
}
