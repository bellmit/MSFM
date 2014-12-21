package com.cboe.infrastructureServices.systemsManagementService.asynchronousClient;

import org.omg.CORBA.UserException;

/**
 * Created by IntelliJ IDEA.
 * User: Eric J. Fredericks
 * Date: Oct 14, 2003
 *
 * This interface is the superinterface of all admin service callbacks which
 * must handle UserExceptions per the synchronous admin service interface.
 * It enforces the methods needed to asynchronously handle exceptions that
 * would have been thrown on the synchronous interface.
 */

public interface AdminCallback extends AdminBaseCallback
{
    public void catchException(UserException e);
}
