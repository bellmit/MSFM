package com.cboe.domain.util;

import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class CurrentMarketStructHelper
{
    
    public static String currentMarketStructToString(CurrentMarketStruct p_struct)
    {
        String methodName = "CurrentMarketStructHelper.currentMarketStructToString";
        try
        {
            java.io.StringWriter resultWriter = new java.io.StringWriter();
            ReflectiveStructBuilder.printStruct(p_struct, "CurrentMarketStruct.", resultWriter);
            return resultWriter.getBuffer().toString();
        }
        catch (java.io.IOException ex)
        {
            Log.exception("In " + methodName + " IOException writing CurrentMarketStruct to string.  Unexpected. " + ex, ex );
            return "IOException writing struct to string.  Unexpected. " + ex;
        }
    }    
    

    public static String currentMarketStructsToString(CurrentMarketStruct[] p_structs)
    {
        String methodName = "CurrentMarketStructHelper.currentMarketStructsToString";
        String result = "";
        try
        {
            for(int i = 0; i < p_structs.length; i ++)
            {
                java.io.StringWriter resultWriter = new java.io.StringWriter();
                ReflectiveStructBuilder.printStruct(p_structs[i], "CurrentMarketStruct["+i+"].", resultWriter);
                result += resultWriter.getBuffer().toString() + "\n";                
            }
        }
        catch (java.io.IOException ex)
        {
            Log.exception("In " + methodName + " IOException writing CurrentMarketStruct to string.  Unexpected. " + ex, ex );
            return "IOException writing struct to string.  Unexpected. " + ex;
        }        
        return result;
    }    
    
    public static CurrentMarketStruct[] structToArray(CurrentMarketStruct struct)
    {
        return struct==null ? new CurrentMarketStruct[0] : new CurrentMarketStruct[]{struct};
    }    
        
}
