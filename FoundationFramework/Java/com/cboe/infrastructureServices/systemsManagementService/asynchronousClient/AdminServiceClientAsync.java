package com.cboe.infrastructureServices.systemsManagementService.asynchronousClient;

import com.cboe.infrastructureServices.interfaces.adminService.Command;
import com.cboe.infrastructureServices.interfaces.adminService.Property;


/**
 * AdminServiceClientAsync
 * 
 * @author Eric J. Fredericks
 * Date: Oct 8, 2003
 * 
 * @see AsynchronousAdminClient
 * 
 * 
 * This is the interface used by clients to submit admin requests asynchronously.  Each method
 * corresponds to a method on the synchronous API for submitting requests to a remote admin service.
 * 
 * A reference to an implementation of this client is provided by <code>AsynchronousAdminClient</code>.
 * 
 * Each method (aside from the accessors) corresponds to a request defined on the synchronous 
 * AdminService API.  Each request is guaranteed to call the given callback object once, either 
 * because of a normal return, exception, or timeout.  
 * 
 * The "default maximum timeout" for a request is 10 seconds.  A "default timeout" may be provided to the
 * framework and all requests using the methods without the timeout value will use the "default timeout"
 * The default "default timeout" is 10 seconds. All timeouts (default or otherwise) must be greater than
 * 0 milliseconds and less than the configurable maximum timeout.
 * 
 * This API uses the FoundationFramework TimeService to queue timeouts.  The TimeService will call the
 * framework back if a timeout occors.  Note that the TimeService processes timeouts in a single thread, so
 * implementations of the callback objects should take this into account.
 */

public interface AdminServiceClientAsync
{
    /**
     * setMaxTimeout
     * @param maxTimeout milliseconds
     */ 
    void setMaxTimeout(int maxTimeout);

    /**
     * getMaxTimeout 
     * @return max timeout in milliseconds
     */
    int getMaxTimeout();
    
    /**
     * setDefaultTimeout
     * @param defaultTimeout milliseconds
     */ 
    void setDefaultTimeout(int defaultTimeout);
    
    /**
     * getDefaultTimeout
     * @return default timeout in milliseconds
     */ 
    int getDefaultTimeout();
    
    /**
     * defineProperties
     * 
     * @param destination 
     * @param properties com.cboe.infrastructureServices.interfaces.adminService.Property[]
     * @param callback
     */ 
    void defineProperties(String destination, Property[] properties, DefinePropertiesCallback callback);
    
    /**
     * defineProperties
     * 
     * @param destination
     * @param timeout milliseconds
     * @param properties com.cboe.infrastructureServices.interfaces.adminService.Property[]
     * @param callback
     */ 
    void defineProperties(String destination, int timeout, Property[] properties, DefinePropertiesCallback callback);

    /**
     * defineProperty
     * 
     * @param destination
     * @param property com.cboe.infrastructureServices.interfaces.adminService.Property
     * @param callback
     */ 
    void defineProperty(String destination, Property property, DefinePropertyCallback callback);

    /**
     * defineProperty
     * 
     * @param destination
     * @param timeout milliseconds
     * @param property com.cboe.infrastructureServices.interfaces.adminService.Property
     * @param callback
     */ 
    void defineProperty(String destination, int timeout, Property property, DefinePropertyCallback callback);

    /**
     * executeCommand
     * 
     * @param destination
     * @param command
     * @param callback
     */ 
    void executeCommand(String destination, Command command, ExecuteCommandCallback callback);

    /**
     * executeCommand
     * 
     * @param destination
     * @param timeout milliseconds
     * @param command
     * @param callback
     */ 
    void executeCommand(String destination, int timeout, Command command, ExecuteCommandCallback callback);

    /**
     * getAllCommands
     * 
     * @param destination
     * @param callback
     */ 
    void getAllCommands(String destination, GetAllCommandsCallback callback);

    /**
     * getAllCommands
     * 
     * @param destination
     * @param timeout milliseconds
     * @param callback
     */ 
    void getAllCommands(String destination, int timeout, GetAllCommandsCallback callback);

    /**
     * getCommand
     * 
     * @param destination
     * @param commandName
     * @param callback
     */ 
    void getCommand(String destination, String commandName, GetCommandCallback callback);

    /**
     * getCommand
     * 
     * @param destination
     * @param timeout milliseconds
     * @param commandName
     * @param callback
     */ 
    void getCommand(String destination, int timeout, String commandName, GetCommandCallback callback);

    /**
     * getProperties
     *
     * @param destination
     * @param callback
     */ 
    void getProperties(String destination, GetPropertiesCallback callback);

    /**
     * getProperties
     *
     * @param destination
     * @param timeout milliseconds
     * @param callback
     */ 
    void getProperties(String destination, int timeout, GetPropertiesCallback callback);

    /**
     * getPropertyValue
     * 
     * @param destination
     * @param propertyName
     * @param callback
     */ 
    void getPropertyValue(String destination, String propertyName, GetPropertyValueCallback callback);

    /**
     * getPropertyValue
     * 
     * @param destination
     * @param timeout milliseconds
     * @param propertyName
     * @param callback
     */ 
    void getPropertyValue(String destination, int timeout, String propertyName, GetPropertyValueCallback callback);
}
