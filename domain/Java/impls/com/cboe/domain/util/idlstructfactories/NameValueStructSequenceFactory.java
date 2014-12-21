package com.cboe.domain.util.idlstructfactories;

import com.cboe.domain.util.TLSStructArray;
import com.cboe.idl.util.NameValueStruct;

public class NameValueStructSequenceFactory implements
        com.cboe.idl.util.NameValueStructSequenceFactory
{
    public static class NameValueStructArray extends TLSStructArray<NameValueStruct>
    {
        protected NameValueStruct[] createArray(int p_len)
        {
            return new NameValueStruct[p_len];
        }
    };

    private ThreadLocal<NameValueStructArray> structArray = new ThreadLocal<NameValueStructSequenceFactory.NameValueStructArray>()
    {
        protected NameValueStructArray initialValue()
        {
            return new NameValueStructArray();
        }
    };

    public com.cboe.idl.util.NameValueStruct[] create(int len)
    {
        return structArray.get().acquire(len);
    }
}
