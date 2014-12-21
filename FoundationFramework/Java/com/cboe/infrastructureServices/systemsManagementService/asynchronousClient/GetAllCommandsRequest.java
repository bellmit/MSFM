package com.cboe.infrastructureServices.systemsManagementService.asynchronousClient;

import com.cboe.infrastructureServices.interfaces.adminEvents.AdminExceptionStruct;
import com.cboe.infrastructureServices.interfaces.adminService.Command;

/**
 * Created by IntelliJ IDEA.
 * User: Eric J. Fredericks
 * Date: Oct 8, 2003
 * Time: 11:45:37 AM
 * To change this template use Options | File Templates.
 */

class GetAllCommandsRequest extends AdminRequestHolder
{
    private GetAllCommandsCallback callback;
    GetAllCommandsRequest(int requestId, String destination, int timeout, String methodName, GetAllCommandsCallback callback)
    {
        super(requestId, destination,timeout, methodName);
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
    
    void returned(Command[] commands)
    {
        callback.returned(commands);
    }
}
