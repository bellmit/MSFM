package com.cboe.infrastructureServices.systemsManagementService.asynchronousClient;

import com.cboe.infrastructureServices.interfaces.adminEvents.AdminExceptionStruct;
import com.cboe.infrastructureServices.interfaces.adminEvents.AdminServiceExceptionTypes;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.MultipleExceptions;

/**
 * Created by IntelliJ IDEA.
 * User: Eric J. Fredericks
 * Date: Oct 8, 2003
 * Time: 11:45:37 AM
 * To change this template use Options | File Templates.
 */

class DefinePropertiesRequest extends AdminRequestHolder
{
    private DefinePropertiesCallback callback;
    
    DefinePropertiesRequest(int requestId, String destination, int timeout, String methodName, DefinePropertiesCallback callback)
    {
        super(requestId, destination, timeout, methodName);
        this.callback = callback;
    }
    
    void processException(AdminExceptionStruct exception)
    {
        switch(exception.type)
        {
            case AdminServiceExceptionTypes.MULTIPLE_EXCEPTIONS:
                MultipleExceptions e1 = new MultipleExceptions(exception.message);
                callback.catchException(e1);
                break;
            default:
                RuntimeException e2 = new RuntimeException(exception.message);
                callback.catchException(e2);
                break;
        }
    }

    void processTimeout()
    {
        callback.timedOut();
    }
    
    void returned()
    {
        callback.returned();
    }
}
