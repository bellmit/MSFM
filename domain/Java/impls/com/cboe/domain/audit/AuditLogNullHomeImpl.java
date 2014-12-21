
/**
 * Title:        AuditLogNullHomeImpl<p>
 * Description:  Creates/Finds an AuditLog for Clients<p>
 *               that will be used in the Regualr CAS Implementations
 * Copyright:    Copyright (c) 2000<p>
 * Company:      CBOE<p>
 * @author       David Blodgett
 * @version
 */
package com.cboe.domain.audit;

import java.util.*;
import com.cboe.interfaces.domain.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.domain.startup.ClientBOHome;

public class AuditLogNullHomeImpl extends ClientBOHome implements AuditLogHome
{
    private static AuditLogNullImpl auditLogNullImpl;

     /**
     * Find the AuditLog specified in the Propoerties File
     * If it exists return it, otherwise we ned to instantiate it
     */
    public AuditLog find()
    {
      if (auditLogNullImpl == null)
      {
        create();
      }
      return auditLogNullImpl;
    }

    /**
     * Ok, the AuditLog isn't there ... so instantiate one and
     * then let the container be aware of its existence
     */
    private  AuditLog create()
    {
      auditLogNullImpl = new AuditLogNullImpl();
      auditLogNullImpl.create("AuditLogNullImpl");
      addToContainer(auditLogNullImpl);
      return auditLogNullImpl;
    }
}
