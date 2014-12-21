package com.cboe.infrastructureServices.systemsManagementService.asynchronousClient;
import com.cboe.infrastructureServices.interfaces.adminEvents.AdminExceptionStruct;
import com.cboe.infrastructureServices.interfaces.adminEvents.AdminServiceExceptionTypes;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.PropertyNotFound;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidPropertyName;

/**
 * Created by IntelliJ IDEA.
 * User: Eric J. Fredericks
 * Date: Oct 8, 2003
 * Time: 11:45:37 AM
 * To change this template use Options | File Templates.
 */

class GetPropertyValueRequest extends AdminRequestHolder
{
    private GetPropertyValueCallback callback;
    GetPropertyValueRequest(int requestId, String destination, int timeout, String methodName, GetPropertyValueCallback callback)
    {
        super(requestId, destination, timeout, methodName);
        this.callback = callback;
    }
    
    void processException(AdminExceptionStruct exception)
    {
        switch(exception.type)
        {
            case AdminServiceExceptionTypes.PROPERTY_NOT_FOUND:
                PropertyNotFound e1 = new PropertyNotFound(exception.message);
                callback.catchException(e1);
                break;
            case AdminServiceExceptionTypes.INVALID_PROPERTY_NAME:
                InvalidPropertyName e2 = new InvalidPropertyName(exception.message);
                callback.catchException(e2);
                break;
            default:
                RuntimeException e3 = new RuntimeException(exception.message);
                callback.catchException(e3);
                break;
        }
    }
    
    void processTimeout()
    {
        callback.timedOut();
    }
    
    void returned(String propertyValue)
    {
        callback.returned(propertyValue);
    }
    
}
