package com.cboe.domain.util;

import com.cboe.idl.orderBook.TradableStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class TradableStructHelper
{
    /**
     * Converts an TradableStruct into a String.
     * 
     * @param p_struct: the TradableStruct to convert.
     * @return String representation of the TradableStruct.
     * @throw IOException: if a error occurs when converting to String. 
     * 
     */        
    public static String tradableStructToString(TradableStruct p_struct)
    {
        String methodName = "TradableStructHelper.tradableStructToString";
        try
        {
            java.io.StringWriter resultWriter = new java.io.StringWriter();
            ReflectiveStructBuilder.printStruct(p_struct, "TradableStruct.", resultWriter);
            return resultWriter.getBuffer().toString();
        }
        catch (java.io.IOException ex)
        {
            Log.exception("In " + methodName + " IOException writing TradableStruct to string.  Unexpected. " + ex, ex );
            return "IOException writing struct to string.  Unexpected. " + ex;
        }
    }        
}
