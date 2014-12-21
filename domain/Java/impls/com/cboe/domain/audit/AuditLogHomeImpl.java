/**
 * Title:        AuditLogHomeImpl<p>
 * Description:  Creates/Finds an AuditLog for Clients<p>
 *               will be used in the CAS Simulator
 * Copyright:    Copyright (c) 2000<p>
 * Company:      CBOE<p>
 * @author       David Blodgett
 * @version
 */

package com.cboe.domain.audit;

import java.util.*;
import com.cboe.interfaces.domain.*;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.domain.startup.ClientBOHome;

public class AuditLogHomeImpl extends ClientBOHome implements AuditLogHome
{
    private static boolean DEFAULT_AUDIT_ENABLED=false;
    private static AuditLogImpl auditLogImpl;

    /**
    * Find the AuditLog specified in the Propoerties File
    * If it exists return it, otherwise we ned to instantiate it
    */
    public AuditLog find()
    {
        if (auditLogImpl == null)
        {
            create();
        }

        return auditLogImpl;
    }

    /**
    * Ok, the AuditLog isn't there ... so instantiate one and
    * then let the container be aware of its existence
    */
    private  AuditLog create()
    {
        boolean enableAudit = false;
        try {
            ConfigurationService configService = FoundationFramework.getInstance().getConfigService();
            enableAudit = configService.getBoolean(getFullName() + ".enableAudit", false);
        } catch (Exception e) {
            Log.exception(this, e);
        }
        auditLogImpl = new AuditLogImpl();
        auditLogImpl.create("AuditLogImpl");
        addToContainer(auditLogImpl);
        if (enableAudit) {
            Log.debug(this, "Enabling interceptor auditing");
            auditLogImpl.enableAuditLogging();
        } else {
            Log.debug(this, "Disabling interceptor auditing");
            auditLogImpl.disableAuditLogging();
        }

        return auditLogImpl;
    }
}
