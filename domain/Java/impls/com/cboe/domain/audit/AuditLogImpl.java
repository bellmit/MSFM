/**
 * Title:        AuditLogImpl<p>
 * Description:  Implements a AuditLog for the Simulator CAS <p>
 * Copyright:    Copyright (c) 2000<p>
 * Company:      CBOE<p>
 * @author       David Blodgett
 * @version
 */

package com.cboe.domain.audit;

import com.cboe.interfaces.domain.AuditLog;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;


public class AuditLogImpl extends BObject implements AuditLog
{
  //default behavior is not to write to the AuditLog
  public boolean enabledAudit = false;

  /**
   * Write to the AuditLog, do so if the user has specified so
   */
  public void audit(BObject loggingObject, String text)
  {
    //Make a call to the Log's Audit Service
    if (enabledAudit)
    {
      Log.audit(loggingObject,text);
    }
  }

  /**
   * Write to the AuditLog, do so if the user has specified so
  */
  public void audit(BOHome loggingHome, String text)
  {
    //Make a call to the Log's Audit Service
    if (enabledAudit)
    {
      Log.audit(loggingHome,text);
    }
  }

  /**
   * Enable writing to the Audit Log
  */
  public void enableAuditLogging ()
  {
    enabledAudit = true;
  }

  /**
   * Disable writing to the Audit Log
  */

  public void disableAuditLogging()
  {
    enabledAudit = false;
  }
}