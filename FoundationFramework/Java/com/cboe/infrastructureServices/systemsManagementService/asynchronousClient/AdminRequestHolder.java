package com.cboe.infrastructureServices.systemsManagementService.asynchronousClient;

import com.cboe.infrastructureServices.interfaces.adminEvents.AdminExceptionStruct;
import com.cboe.infrastructureServices.interfaces.adminEvents.AdminServiceExceptionTypes;

/**
 * Created by IntelliJ IDEA.
 * User: Eric J. Fredericks
 * Date: Oct 3, 2003
 * Time: 11:33:46 AM
 * To change this template use Options | File Templates.
 */

abstract class AdminRequestHolder
{
    private int requestId;
    private String destination;
    private String methodName;
    private int timerId;
    private int timeout;
    
    AdminRequestHolder(final int requestId, final String destination, int timeout, final String methodName)
    {
        this.requestId = requestId;
        this.destination = destination;
        this.methodName = methodName;
        this.timeout = timeout;
        timerId = -1;
        
    }
    
    public int getTimeout()
    {
        return timeout;
    }
    
    public int getRequestId()
    {
        return requestId;
    }
    
    public String getDestination()
    {
        return destination;
    }
    
    public String getMethodName()
    {
        return methodName;
    }
    
    public void setTimerId(int timerId)
    {
        this.timerId = timerId;
    }
    
    public int getTimerId()
    {
        return timerId;
    }

    abstract void processException(AdminExceptionStruct e);
    abstract void processTimeout();

    static void invalidException(String methodName, short type) throws Exception
    {
        String typeString = null;
        switch(type)
        {
            case AdminServiceExceptionTypes.INVALID_PARAMETER:
                typeString = "InvalidParameter";
                break;
            case AdminServiceExceptionTypes.INVALID_PROPERTY_NAME:
                typeString = "InvalidPropertyName";
                break;
            case AdminServiceExceptionTypes.INVALID_PROPERTY_VALUE:
                typeString = "InvalidPropertyValue";
                break;
            case AdminServiceExceptionTypes.MULTIPLE_EXCEPTIONS:
                typeString = "MultipleExceptions";
                break;
            case AdminServiceExceptionTypes.PROPERTY_NOT_FOUND:
                typeString = "PropertyNotFound";
                break;
            case AdminServiceExceptionTypes.UNSUPPORTED_COMMAND:
                typeString = "UnsupportedCommand";
                break;
            case AdminServiceExceptionTypes.UNSUPPORTED_PROPERTY:
                typeString = "UnsupportedProperty";
                break;
            default:
                typeString = "Unknown exception type " + type;
        }
        
        throw new Exception("AdminRequestHolder-> Received invalid exception (type=" + typeString + ") for method (methodName=" + methodName +")");
    }
    
}
