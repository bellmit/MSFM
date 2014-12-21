/**
 * Title:        AuditLog<p>
 * Description:  Describes the functionality of an AuditLog<p>
 * Copyright:    Copyright (c) 2000<p>
 * Company:      CBOE<p>
 * @author       David Blodgett
 * @version
 */
package com.cboe.interfaces.domain;

import com.cboe.infrastructureServices.foundationFramework.*;

public interface AuditLog
{
  /** Enable writing Audit Messages
   */
  public void enableAuditLogging ();

  /** Disable writing Audit Messages
   */
  public void disableAuditLogging ();

  /** Write an Audit Message of this Signature
  */
  public void audit(BObject loggingObject, String text);

  /** Write an Audit Message of this Signature
  */
  public void audit(BOHome loggingHome, String text);
}