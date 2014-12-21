package com.cboe.infrastructureServices.systemsManagementService.asynchronousClient;

/**
 * Created by IntelliJ IDEA.
 * User: Eric J. Fredericks
 * Date: Oct 14, 2003
 * 
 * This interface is the superinterface of all admin service callbacks.
 * It enforces the methods needed to asynchronously handle exceptions that
 * would have been thrown on the synchronous interface.
 */

public interface AdminBaseCallback
{
    public void catchException(RuntimeException e);
    public void timedOut();
}
