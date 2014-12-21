package com.cboe.domain.util;

import java.io.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.util.ReflectiveObjectWriter;
import com.cboe.interfaces.domain.session.CallbackDeregistrationInfo;

/**
 * Wrapper object for CallbackInformationStruct, reason and errorCode.
 * The quote details are cloned, not the orginal
 * @author Connie Feng
 */
public class CallbackDeregistrationInfoStruct implements CallbackDeregistrationInfo
{
    private CallbackInformationStruct   callbackStruct;
    private String      reason;
    private int                 errorCode;

    public CallbackDeregistrationInfoStruct(CallbackInformationStruct callBack, String reason, int errorCode)
    {
        this.callbackStruct = callBack;
        this.reason = reason;
        this.errorCode = errorCode;
    }

    public CallbackInformationStruct getCallbackInformationStruct()
    {
        return this.callbackStruct;
    }

    public String getReason()
    {
        return this.reason;
    }

    public int getErrorCode()
    {
        return this.errorCode;
    }
    
    public String toString()
    {
        StringBuilder callbackInfoBuffer = new StringBuilder(50);
        callbackInfoBuffer.append("ErrorCode: ")
        .append(getErrorCode())
        .append(". ")         
        .append("Reason: ")
        .append(getReason())
        .append(". "); 
        
        StringWriter structStringWriter = new StringWriter();
        try
        {
            ReflectiveObjectWriter.writeObject(this.callbackStruct, "CallbackDeregistrationInfoStruct.callbackStruct", structStringWriter);
            callbackInfoBuffer.append(structStringWriter.toString()); 
        }
        catch (IOException e)
        {
            System.err.println("Error in CallbackDeregistrationInfoStruct.toString()");
            e.printStackTrace();
        }
        return callbackInfoBuffer.toString();
    }

}// end  class CallbackDeregistrationInfoStruct
