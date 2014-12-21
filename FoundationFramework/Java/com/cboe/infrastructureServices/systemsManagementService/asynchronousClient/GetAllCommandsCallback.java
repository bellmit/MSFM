package com.cboe.infrastructureServices.systemsManagementService.asynchronousClient;

import com.cboe.infrastructureServices.interfaces.adminService.Command;

/**
 * GetAllCommandsCallback
 * 
 * @see AdminBaseCallback
 * 
 * This interface extends the AdminBaseCallback.  Therefore, it inherits
 * a default method for handling timeouts and a method for handling
 * RuntimeExceptions over CORBA.
 * 
 * The API uses the FoundationFramework TimeService to queue timeouts.  The TimeService will call the
 * framework back if a timeout occors.  Note that the TimeService processes timeouts in a single thread, so
 * implementations of this interface should take this into account.
 */

public interface GetAllCommandsCallback extends AdminBaseCallback
{
    void returned(Command[] commands);
}
