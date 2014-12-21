package com.cboe.domain.util;

import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class ExpectedOpeningPriceStructHelper
{

    /**
     * Converts an ExpectedOpeningPriceStruct into a String.
     * 
     * @param p_struct: the ExpectedOpeningPriceStruct to convert.
     * @return String representation of the ExpectedOpeningPriceStruct.
     * @throw IOException: if a error occurs when converting to String. 
     * 
     */         
    public static String expectedOpeningPriceStructToString(ExpectedOpeningPriceStruct p_struct)
    {
        String methodName = "ExpectedOpeningPriceStructHelper.expectedOpeningPriceStructToString";
        try
        {
            java.io.StringWriter resultWriter = new java.io.StringWriter();
            ReflectiveStructBuilder.printStruct(p_struct, "ExpectedOpeningPriceStruct.", resultWriter);
            return resultWriter.getBuffer().toString();
        }
        catch (java.io.IOException ex)
        {
            Log.exception("In " + methodName + " IOException writing ExpectedOpeningPriceStruct to string.  Unexpected. " + ex, ex );
            return "IOException writing struct to string.  Unexpected. " + ex;
        }
    }       
    
    public static ExpectedOpeningPriceStruct[] structToArray(ExpectedOpeningPriceStruct struct)
    {
        return struct==null ? new ExpectedOpeningPriceStruct[0] : new ExpectedOpeningPriceStruct[]{struct};
    }      
}
