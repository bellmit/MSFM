package com.cboe.infrastructureServices.systemsManagementService.asynchronousClient;

import com.cboe.infrastructureServices.interfaces.adminEvents.AdminExceptionStruct;
import com.cboe.infrastructureServices.interfaces.adminEvents.AdminServiceExceptionTypes;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidPropertyName;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidPropertyValue;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.UnsupportedProperty;

/**
 * Created by IntelliJ IDEA.
 * User: Eric J. Fredericks
 * Date: Oct 8, 2003
 * Time: 11:45:37 AM
 * To change this template use Options | File Templates.
 */

class DefinePropertyRequest extends AdminRequestHolder
{
    private DefinePropertyCallback callback;
    
    DefinePropertyRequest(int requestId, String destination, int timeout, String methodName, DefinePropertyCallback callback)
    {
        super(requestId, destination, timeout, methodName);
        this.callback = callback;
    }
    
    void processException(AdminExceptionStruct exception)
    {
        switch(exception.type)
        {
            case AdminServiceExceptionTypes.INVALID_PROPERTY_NAME:
                InvalidPropertyName e1 = new InvalidPropertyName(exception.message);
                callback.catchException(e1);
                break;
            case AdminServiceExceptionTypes.INVALID_PROPERTY_VALUE:
                InvalidPropertyValue e2 = new InvalidPropertyValue(exception.message);
                callback.catchException(e2);
                break;
            case AdminServiceExceptionTypes.UNSUPPORTED_PROPERTY:
                UnsupportedProperty e3 = new UnsupportedProperty(exception.message);
                callback.catchException(e3);
                break;
            default:
                RuntimeException e4 = new RuntimeException(exception.message);
                callback.catchException(e4);
                break;
        }
    }

    void processTimeout()
    {
        callback.timedOut();
    }
    
    void retruned()
    {
        callback.returned();    
    }
}
