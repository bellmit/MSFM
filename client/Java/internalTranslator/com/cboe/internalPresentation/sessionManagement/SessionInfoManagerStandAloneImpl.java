//
// -----------------------------------------------------------------------------------
// Source file: SessionInfoManagerStandAloneImpl.java
//
// PACKAGE: com.cboe.internalPresentation.sessionManagement;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.sessionManagement;

import javax.swing.JOptionPane;

import org.omg.CORBA.ORB;
import com.cboe.ORBInfra.ORB.Orb;

import com.cboe.idl.infrastructureServices.sessionManagementService.SessionManagementAdminServiceOperations;
import com.cboe.idl.infrastructureServices.sessionManagementService.SessionManagementAdminServiceHelper;

/**
 * Implements the SessionInfoManager for Stand Alone operation
 */
public class SessionInfoManagerStandAloneImpl extends AbstractSessionInfoManager
{
    private SessionManagementAdminServiceOperations smsReference = null;

    /**
     * SessionInfoManagerStandAloneImpl constructor comment.
     */
    public SessionInfoManagerStandAloneImpl()
    {
      super();
    }

    /**
     * Returns a reference to the SessionManagementAdminService. Performs initrefs resolution and binding
     * to the remote service if necessary.
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
                    try
                    {
                        ORB orb = Orb.init();
                        org.omg.CORBA.Object orbObject = orb.resolve_initial_references("SessionManagementAdminService");
                        smsReference = (SessionManagementAdminServiceOperations)SessionManagementAdminServiceHelper.narrow(orbObject);
                    }
                    catch(Exception e)
                    {
                        System.err.println("Error finding SessionManagementAdminService proxy.");
                        e.printStackTrace();
                        throw new SessionQueryException("Could not connect to the SessionManagementService.",e);
                    }
                }
            }
        }

        return smsReference;
    }
}