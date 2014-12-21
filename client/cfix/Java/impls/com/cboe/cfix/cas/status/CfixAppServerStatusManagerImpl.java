package com.cboe.cfix.cas.status;

import com.cboe.application.status.AppServerStatusManagerImpl;
import com.cboe.interfaces.application.AppServerStatusManager;
import com.cboe.exceptions.*;
import com.cboe.cfix.cas.shared.CfixServicesHelper;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class CfixAppServerStatusManagerImpl extends AppServerStatusManagerImpl implements AppServerStatusManager
{
    protected static boolean isCFIXInitialized = false;

    public CfixAppServerStatusManagerImpl(String processName, String cacheDirectory, String cacheBasename)
    {
        super(processName, cacheDirectory, cacheBasename);
    }

    protected synchronized void reInitialize()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        super.reInitialize();
        if(!isCFIXInitialized)
        {
            CfixServicesHelper.getCfixHome().initializeCfixConnection();
            isCFIXInitialized = true;
            Log.notification(this, "Cfix CAS Re-Initialization is Complete.");
        }
    }

    protected synchronized void cleanup()
    {
        super.cleanup();
        if(isCFIXInitialized)
        {
            CfixServicesHelper.getCfixHome().stopCfixConnection();
            isCFIXInitialized = false;
            Log.notification(this, "Cfix fix connections stopped.");
        }
    }
}
