/**
 * Title:        AuditLogNullImpl<p>
 * Description:  Serves as the Regular CAS's leadin to the LoggingService's Audit Category<p>
 * Copyright:    Copyright (c) 2000<p>
 * Company:      CBOE<p>
 * @author       David Blodgett
 * @version
 */
package com.cboe.domain.audit;

import com.cboe.interfaces.domain.AuditLog;
import com.cboe.infrastructureServices.foundationFramework.*;


public class AuditLogNullImpl extends BObject implements AuditLog
{

  //default behavior is not to write to the AuditLog
  public boolean enabledAudit = false;

  /**
  * Write to the AuditLog, do so if the user has specified so
  */
  public void audit(BObject loggingObject, String text)
  {
  }

  /**
   * Write to the AuditLog, do so if the user has specified so
  */
  public void audit(BOHome loggingHome, String text)
  {
  }

  /**
   * Enable writing to the Audit Log
  */
  public void enableAuditLogging ()
  {
  }

  /**
   * Disable writing to the Audit Log
  */
  public void disableAuditLogging()
  {
  }
}