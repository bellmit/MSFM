package com.cboe.infrastructureServices.systemsManagementService.asynchronousClient;

import com.cboe.infrastructureServices.interfaces.adminEvents.AdminExceptionStruct;
import com.cboe.infrastructureServices.interfaces.adminService.Property;

/**
 * Created by IntelliJ IDEA.
 * User: Eric J. Fredericks
 * Date: Oct 8, 2003
 * Time: 11:45:37 AM
 * To change this template use Options | File Templates.
 */

class GetPropertiesRequest extends AdminRequestHolder
{
    private GetPropertiesCallback callback;
    
    GetPropertiesRequest(int requestId, String destination, int timeout, String methodName, GetPropertiesCallback callback)
    {
        super(requestId, destination, timeout, methodName);
        this.callback = callback;
    }
    
    void processException(AdminExceptionStruct exception)
    {
        RuntimeException e = new RuntimeException(exception.message);
        callback.catchException(e);
    }

    void processTimeout()
    {
        callback.timedOut();
    }
    
    void returned(Property[] properties)
    {
        callback.returned(properties);
    }
}
