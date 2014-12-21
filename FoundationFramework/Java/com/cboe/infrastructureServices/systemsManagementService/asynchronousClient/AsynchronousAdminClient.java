package com.cboe.infrastructureServices.systemsManagementService.asynchronousClient;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Created by IntelliJ IDEA.
 * User: Eric J. Fredericks
 * Date: Oct 3, 2003
 *
 * @see AdminServiceClientAsync
 * @see AdminBaseCallback
 * @see AdminCallback
 * @see DefinePropertiesCallback
 * @see DefinePropertyCallback
 * @see ExecuteCommandCallback
 * @see GetAllCommandsCallback
 * @see GetCommandCallback
 * @see GetPropertiesCallback
 * @see GetPropertyValueCallback
 * 
 * This class provides the interface for aquiring a reference to the
 * AdminServiceClientAsync object which provides the asynchronous API
 * for submitting admin requests.  There can be only one active
 * AdminServiceClientAsync per process.
 * 
 * In order to use this API, the following properties need to be defined:
 * 
 * -D asyncAdminUniqueProcess=<processId>
 * -D asyncAdminChannelName=<channelName>  (most likely <prefix>AdminService)
 *
 * See the documentation for <code>AdminServiceClientAsync</code> for details.  
 */

public abstract class AsynchronousAdminClient// implements AdminServiceClientAsync
{
    
    private static AdminServiceAsyncClientImpl instance;
    private static String uniqueProcessIdentifier;
    private static String eventChannelName;
    private static boolean initialized = false;

    /**
     * getInstance
     * 
     * @return AsynchronousAdminClient
     * @throws InitializationFailedException if initializtion fails
     */ 
    public static synchronized AdminServiceClientAsync getInstance()
        throws InitializationFailedException
    {
        if(!initialized)
        {
            String processId = System.getProperty("asyncAdminUniqueProcess");
            String channelName = System.getProperty("asyncAdminChannelName");
            
            if(processId == null || processId.equals(""))
            {
                if(Log.isDebugOn())
                {
                    Log.debug("AsynchronousAdminClient -> processId is null or empty, throwing InitializationFailedException");
                }
                throw new InitializationFailedException("AdminServiceAsyncClient -> unique process identifier cannot be null or empty.");
            }
            
            if(channelName == null || channelName.equals(""))
            {
                if(Log.isDebugOn())
                {
                    Log.debug("AsynchronousAdminClient -> channelName is null, or empty, throwing InitializationFailedException");
                }
                throw new InitializationFailedException("AdminServiceAsyncClient -> unique process identifier cannot be null.");
            }

            uniqueProcessIdentifier = processId;
            eventChannelName = channelName;

            instance = new AdminServiceAsyncClientImpl();
            try
            {
                instance.initialize(uniqueProcessIdentifier, eventChannelName);
                initialized = true;
            }
            catch(Exception e)
            {
                instance = null;
                Log.exception("AsynchronousAdminClient -> Exception during initialization.", e);
                throw new InitializationFailedException(e.getMessage());
            }
        }
        
        return instance;
    }
}
