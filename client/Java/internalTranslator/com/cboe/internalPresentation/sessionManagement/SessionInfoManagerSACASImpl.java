//
// -----------------------------------------------------------------------------------
// Source file: SessionInfoManagerSACASImpl.java
//
// PACKAGE: com.cboe.internalPresentation.sessionManagement;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.sessionManagement;

import com.cboe.idl.infrastructureServices.sessionManagementService.SessionManagementAdminServiceOperations;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

import com.cboe.internalPresentation.api.SystemAdminAPIFactory;

/**
 * Implements the SessionInfoManager for operation through an SACAS
 */
public class SessionInfoManagerSACASImpl extends AbstractSessionInfoManager
{
    private SessionManagementAdminServiceOperations smsReference = null;

    /**
     * SessionInfoManagerSACASImpl constructor comment.
     */
    public SessionInfoManagerSACASImpl()
    {
      super();
    }

    /**
     * Returns a reference to the SessionManagementAdminService.
     * @return SessionManagementAdminServiceOperations
     */
    protected SessionManagementAdminServiceOperations getSessionManagementAdminService() throws SessionQueryException
    {
        if(smsReference == null)
        {
            synchronized(this)
            {
                if(smsReference == null)
                {
                    smsReference = (SessionManagementAdminServiceOperations)SystemAdminAPIFactory.find();
                }
            }
        }

        return smsReference;
    }
}