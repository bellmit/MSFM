package com.cboe.infrastructureServices.systemsManagementService.asynchronousClient;

/**
 * GetPropertyValueCallback
 * 
 * @see AdminCallback
 * @see AdminBaseCallback
 * 
 * This interface extends the AdminCallback.  Therefore, it inherits
 * a default method for UserException subclass handling as well as
 * a method for handling timeouts and a method for handling RuntimeExceptions
 * over CORBA.
 * 
 * When implementing the catchException from the AdminCallback interface which
 * handles asynchronous reporting of a UserException from the synchronous API,
 * classes may safely cast to PropertyNotFound or InvalidPropertyName.
 * 
 * The API uses the FoundationFramework TimeService to queue timeouts.  The TimeService will call the
 * framework back if a timeout occors.  Note that the TimeService processes timeouts in a single thread, so
 * implementations of this interface should take this into account.
 */

public interface GetPropertyValueCallback extends AdminCallback
{
    void returned(String propertyValue);
}
