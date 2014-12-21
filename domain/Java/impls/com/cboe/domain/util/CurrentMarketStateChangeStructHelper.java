package com.cboe.domain.util;

import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.marketData.CurrentMarketStateChangeStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class CurrentMarketStateChangeStructHelper
{
    /**
     * Converts an CurrentMarketStateChangeStruct into a String.
     * 
     * @param p_struct: the CurrentMarketStateChangeStruct to convert.
     * @return String representation of the CurrentMarketStateChangeStruct.
     * @throw IOException: if a error occurs when converting to String. 
     * 
     */        
    public static String currentMarketStateChangeStructToString(CurrentMarketStateChangeStruct p_struct)
    {
        String methodName = "CurrentMarketStateChangeStructHelper.currentMarketStateChangeStructToString";
        try
        {
            java.io.StringWriter resultWriter = new java.io.StringWriter();
            ReflectiveStructBuilder.printStruct(p_struct, "CurrentMarketStateChangeStruct.", resultWriter);
            return resultWriter.getBuffer().toString();
        }
        catch (java.io.IOException ex)
        {
            Log.exception("In " + methodName + " IOException writing CurrentMarketStateChangeStruct to string.  Unexpected. " + ex, ex );
            return "IOException writing struct to string.  Unexpected. " + ex;
        }
    }

    public static String currentMarketStateChangeStructToString(CurrentMarketStateChangeStruct[] p_structs)
    {
        String str = "";
        if(p_structs != null)
        {
            str = "";
            for(int i=0; i < p_structs.length; i++)
            {
                str+= currentMarketStateChangeStructToString(p_structs[i]);
                if(i< p_structs.length-1)
                {
                    str+="\n";
                }            
            }
        }
        return str;
    }   
    
    
    public static CurrentMarketStateChangeStruct[] structToArray(CurrentMarketStateChangeStruct struct)
    {
        return struct==null ? new CurrentMarketStateChangeStruct[0] : new CurrentMarketStateChangeStruct[]{struct};
    }        
}
