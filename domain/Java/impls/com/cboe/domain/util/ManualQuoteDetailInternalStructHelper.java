package com.cboe.domain.util;

import com.cboe.idl.marketData.ManualQuoteDetailInternalStruct;

public class ManualQuoteDetailInternalStructHelper
{

    public static ManualQuoteDetailInternalStruct[] structToArray(ManualQuoteDetailInternalStruct struct)
    {
        return struct==null ? new ManualQuoteDetailInternalStruct[0] : new ManualQuoteDetailInternalStruct[]{struct};
    }       
}
