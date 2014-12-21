package com.cboe.domain.util;

import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class RoutingParameterStructHelper
{

    /**
     * Converts an RoutingParameterStruct into a String.
     * 
     * @param p_struct: the RoutingParameterStruct to convert.
     * @return String representation of the RoutingParameterStruct.
     * @throw IOException: if a error occurs when converting to String. 
     * 
     */        
    public static String routingParameterStructToString(RoutingParameterStruct p_struct)
    {
        String methodName = "RoutingParameterStructHelper.routingParameterStructToString";
        try
        {
            java.io.StringWriter resultWriter = new java.io.StringWriter();
            ReflectiveStructBuilder.printStruct(p_struct, "RoutingParameterStruct.", resultWriter);
            return resultWriter.getBuffer().toString();
        }
        catch (java.io.IOException ex)
        {
            Log.exception("In " + methodName + " IOException writing RoutingParameterStruct to string.  Unexpected. " + ex, ex );
            return "IOException writing struct to string.  Unexpected. " + ex;
        }
    }           
}
