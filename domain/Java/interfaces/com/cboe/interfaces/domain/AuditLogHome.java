
/**
 * Title:        AuditLogHome<p>
 * Description:  Interface for finding an AuditLog<p>
 * Copyright:    Copyright (c) 2000<p>
 * Company:      CBOE<p>
 * @author       David Blodgett
 * @version
 */
package com.cboe.interfaces.domain;

public interface AuditLogHome
{
    /**
     * Generic Name for all AuditLogImpls
     */
    public final static String HOME_NAME = "AuditLogHome";

    /**
     * Give the User the AuditLogImpl they specified in the
     * Properties File
     */
    public AuditLog find();

}