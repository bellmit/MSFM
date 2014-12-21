package com.cboe.infrastructureServices.systemsManagementService.asynchronousClient;

import com.cboe.infrastructureServices.interfaces.adminEvents.AdminExceptionStruct;
import com.cboe.infrastructureServices.interfaces.adminEvents.AdminServiceExceptionTypes;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.UnsupportedCommand;
import com.cboe.infrastructureServices.interfaces.adminService.Command;

/**
 * Created by IntelliJ IDEA.
 * User: Eric J. Fredericks
 * Date: Oct 8, 2003
 * Time: 11:45:37 AM
 * To change this template use Options | File Templates.
 */

class GetCommandRequest extends AdminRequestHolder
{
    private GetCommandCallback callback;
    
    GetCommandRequest(int requestId, String destination, int timeout, String methodName, GetCommandCallback callback)
    {
        super(requestId, destination, timeout, methodName);
        this.callback = callback;
    }
    
    void processException(AdminExceptionStruct exception)
    {
        switch(exception.type)
        {
            case AdminServiceExceptionTypes.UNSUPPORTED_COMMAND:
                UnsupportedCommand e1 = new UnsupportedCommand(exception.message);
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
    
    void returned(Command command)
    {
        callback.returned(command);    
    }
}
